package com.koirdsuzu.blockcountwinner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlockCountWinner extends JavaPlugin {

    private File logFile;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("BlockCountWinner has been enabled.");
        createLogFile();
    }

    @Override
    public void onDisable() {
        getLogger().info("BlockCountWinner has been disabled.");
    }

    private void createLogFile() {
        logFile = new File(getDataFolder(), "results.log");
        if (!logFile.exists()) {
            try {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Failed to create results log file.");
                e.printStackTrace();
            }
        }
    }

    private void logResult(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            getLogger().severe("Failed to write to results log file.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        FileConfiguration config = getConfig();

        if (command.getName().equalsIgnoreCase("checkrange")) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Please specify a range identifier (e.g., hani1)." );
                return true;
            }

            String rangeKey = args[0];

            if (!config.contains(rangeKey)) {
                sender.sendMessage(ChatColor.RED + "The range " + rangeKey + " is not defined in the configuration.");
                return true;
            }

            // Retrieve range and block data from config
            Location start = new Location(
                    Bukkit.getWorld(config.getString(rangeKey + ".world")),
                    config.getInt(rangeKey + ".x"),
                    config.getInt(rangeKey + ".y"),
                    config.getInt(rangeKey + ".z")
            );

            Location end = start.clone().add(
                    config.getInt(rangeKey + ".dx"),
                    config.getInt(rangeKey + ".dy"),
                    config.getInt(rangeKey + ".dz")
            );

            Material block1 = Material.getMaterial(config.getString("block1"));
            Material block2 = Material.getMaterial(config.getString("block2"));

            if (block1 == null || block2 == null) {
                sender.sendMessage(ChatColor.RED + "Invalid block types defined in the configuration.");
                return true;
            }

            String team1Name = ChatColor.translateAlternateColorCodes('&', config.getString("teamname1"));
            String team2Name = ChatColor.translateAlternateColorCodes('&', config.getString("teamname2"));

            // Count blocks
            int count1 = countBlocks(start, end, block1);
            int count2 = countBlocks(start, end, block2);

            // Calculate percentages
            int total = count1 + count2;
            double percent1 = total > 0 ? (count1 * 100.0 / total) : 0;
            double percent2 = total > 0 ? (count2 * 100.0 / total) : 0;

            // Determine winner
            String winner = count1 > count2 ? team1Name : team2Name;

            // Send victory message
            String message = ChatColor.translateAlternateColorCodes('&',
                    config.getString("message").replace("{team}", winner)
                            .replace("{team1}", team1Name)
                            .replace("{team2}", team2Name)
                            .replace("{percent1}", String.format("%.2f%%", percent1))
                            .replace("{percent2}", String.format("%.2f%%", percent2))
            );

            Bukkit.broadcastMessage(message);

            // Log result
            logResult("Range: " + rangeKey + ", Winner: " + winner + ", " + team1Name + " " + percent1 + "% vs " + team2Name + " " + percent2 + "%");
            return true;
        }

        if (command.getName().equalsIgnoreCase("checkcustomrange")) {
            if (args.length < 6) {
                sender.sendMessage(ChatColor.RED + "Usage: /checkcustomrange <x> <y> <z> <dx> <dy> <dz>");
                return true;
            }

            try {
                Location start = new Location(
                        ((Player) sender).getWorld(),
                        Integer.parseInt(args[0]),
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2])
                );

                Location end = start.clone().add(
                        Integer.parseInt(args[3]),
                        Integer.parseInt(args[4]),
                        Integer.parseInt(args[5])
                );

                Material block1 = Material.getMaterial(config.getString("block1"));
                Material block2 = Material.getMaterial(config.getString("block2"));

                if (block1 == null || block2 == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid block types defined in the configuration.");
                    return true;
                }

                String team1Name = ChatColor.translateAlternateColorCodes('&', config.getString("teamname1"));
                String team2Name = ChatColor.translateAlternateColorCodes('&', config.getString("teamname2"));

                // Count blocks
                int count1 = countBlocks(start, end, block1);
                int count2 = countBlocks(start, end, block2);

                // Calculate percentages
                int total = count1 + count2;
                double percent1 = total > 0 ? (count1 * 100.0 / total) : 0;
                double percent2 = total > 0 ? (count2 * 100.0 / total) : 0;

                // Determine winner
                String winner = count1 > count2 ? team1Name : team2Name;

                // Send victory message
                String message = ChatColor.translateAlternateColorCodes('&',
                        config.getString("message").replace("{team}", winner)
                                .replace("{team1}", team1Name)
                                .replace("{team2}", team2Name)
                                .replace("{percent1}", String.format("%.2f%%", percent1))
                                .replace("{percent2}", String.format("%.2f%%", percent2))
                );

                Bukkit.broadcastMessage(message);

                // Log result
                logResult("Custom range command, Winner: " + winner + ", " + team1Name + " " + percent1 + "% vs " + team2Name + " " + percent2 + "%");
                return true;

            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid number format.");
                return true;
            }
        }

        return false;
    }

    private int countBlocks(Location start, Location end, Material material) {
        int count = 0;
        for (int x = Math.min(start.getBlockX(), end.getBlockX()); x <= Math.max(start.getBlockX(), end.getBlockX()); x++) {
            for (int y = Math.min(start.getBlockY(), end.getBlockY()); y <= Math.max(start.getBlockY(), end.getBlockY()); y++) {
                for (int z = Math.min(start.getBlockZ(), end.getBlockZ()); z <= Math.max(start.getBlockZ(), end.getBlockZ()); z++) {
                    if (start.getWorld().getBlockAt(x, y, z).getType() == material) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
