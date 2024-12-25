# Block Count Winner Plugin

## Overview
This plugin allows Minecraft server operators to create customizable block-count-based competitions in a defined area. Players compete by placing specific blocks, and the plugin calculates the percentages of block placements to determine the winner. The plugin supports multiple areas and configurable team names, messages, and block types.

---

# ブロックカウント勝者プラグイン

## 概要
このプラグインは、Minecraftサーバーのオペレーターがカスタマイズ可能なブロック計測ベースのコンペティションを実施できるようにします。プレイヤーは特定のブロックを置き、プラグインはブロックの計測割合を計算して勝者を決定します。複数のエリアとチーム名、メッセージ、ブロックタイプが設定可能です。

---

## Features / 機能

- Supports multiple areas (`hani1`, `hani2`, etc.)
- Configurable block types and team names
- Customizable victory messages with color codes
- Command-based results calculation
- All commands are restricted to server operators

・複数のエリアに対応 (例: `hani1`, `hani2`)
・ブロックタイプとチーム名が自由に設定可能
・カラーコード使用可能な勝利メッセージ
・コマンド基盤の結果計算
・すべてのコマンドはオペレーターに限定

---

## Commands / コマンド

### `/blockwinner hani1`
- Calculates the block percentages for the area `hani1` and announces the winner in chat.

例: `/blockwinner hani1`
- `hani1`のエリアのブロック割合を計算し、チャットに勝者を告知します。

### `/blockwinnerpercent x y z dx dy dz`
- Calculates the block percentages for the specified coordinates and announces the winner in chat.

例: `/blockwinnerpercent x y z dx dy dz`
- 指定された座標のブロック割合を計算し、チャットに勝者を告知します。

---

## Configuration / 設定

```yaml
teamname1: "Team Red"
teamname2: "Team Blue"

block1: "minecraft:red_wool"
block2: "minecraft:blue_wool"

# Victory message
message: "&6{team}&rの勝利！結果は &c{percent1}% &rVS &9{percent2}% でした。"

hani1:
  x: 0
  y: 64
  z: 0
  dx: 10
  dy: 10
  dz: 10
hani2:
  x: -20
  y: 64
  z: -20
  dx: 10
  dy: 10
  dz: 10
```

---

## Installation / インストール

1. Place the JAR file into the `plugins` folder of your Minecraft server.
2. Start or restart the server.
3. Edit the configuration file (`plugins/BlockCountWinner/config.yml`) to your preferences.
4. Use the commands to start competitions.

1. JARファイルをMinecraftサーバーの`plugins`フォルダに設置します。
2. サーバーを起動または再起動します。
3. コンフィグファイル(`plugins/BlockCountWinner/config.yml`)を編集します。
4. コマンドを使用してコンペティションを始めます。

