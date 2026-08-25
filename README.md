# ServerPlugin

A small Paper plugin that powers the [Hielke Maps](https://github.com/hielkemaps) parkour servers. It handles the little things a map server needs: rewarding players with Parcoins when they finish a map, teleport requests, spectating, highscores, a `/hub` shortcut, and a handful of gameplay protections.

## Features

### Parcoin rewards
When a player earns the `finished` scoreboard tag (set by the map's datapack), the plugin pays out Parcoins through [PlayerPoints](https://www.spigotmc.org/resources/playerpoints.80745/):

- `points-given` base reward (default **100**)
- `points-bonus` extra for an S rank, detected via the `rank_s` tag (default **50**)
- A rank multiplier based on the player's primary [LuckPerms](https://luckperms.net/) group:

  | Group | Multiplier |
  |-----------------------|------------|
  | `owner`, `admin`, `mvp` | 2×   |
  | `vip+`                | 1.5×       |
  | `builder`, `moderator`, `vip` | 1.25× |

Players see a chat breakdown of their reward, an action-bar message and a level-up sound. Players with the `training_mode` tag are ignored, and players who used `/tpa` during a run receive nothing until they restart the map (the `finished` tag is removed).

### Commands

| Command | Description |
|---------|-------------|
| `/hub` (alias `/lobby`) | Sends the player to the `lobby` server via the BungeeCord plugin channel. |
| `/tpa <player>` | Sends a teleport request. Only shown when more than one player is online. Using it forfeits the Parcoin reward for the current run. |
| `/tpaccept` | Accepts all pending teleport requests. Only shown when you have incoming requests. |
| `/spectate` | Toggles spectator mode. Leaving spectator mode triggers the map's `restart` trigger. |
| `/spectate <player>` | Spectates a specific (non-spectating) player. |
| `/highscores <count>` | Prints the top `count` times from the `time_pb` scoreboard objective. Requires `serverplugin.command.highscores`. |
| `/localtime <time>` | Sets the player's client-side time of day. |
| `/serverplugin reload` | Reloads `config.yml`. Requires OP. |

`/tpa`, `/tpaccept` and `/spectate` are blocked while the player has the `inRace` tag.

### Scoreboard tag hooks
Datapacks can control the plugin by adding tags to a player:

| Tag | Effect |
|-----|--------|
| `SERVER_ENABLE_FLIGHT` | Enables flight, then removes the tag. |
| `SERVER_DISABLE_FLIGHT` | Disables flight (unless in Creative), then removes the tag. |
| `finished` | Marks a map as completed and triggers the reward. |
| `rank_s` | Grants the S-rank bonus. |
| `training_mode` | Excludes the player from rewards. |
| `inRace` | Blocks `/tpa`, `/tpaccept` and `/spectate`. |

### Gameplay protections
- Farmland can't be trampled.
- Sweet berry bushes, cave vines and flower pots can't be interacted with (except in Creative).
- Players with Fire Resistance never visually catch fire.
- Players are dismounted from vehicles when they leave.

## Configuration

`plugins/ServerPlugin/config.yml`:

```yaml
# commands that can be disabled:
# hub, spectate, tpa, highscores, localtime
disabled-commands: []
points-given: 100
points-bonus: 50
```

Reload with `/serverplugin reload`.

## Requirements

- Paper **1.21+** (Java 17)
- [CommandAPI](https://commandapi.jorel.dev/)
- [PlayerPoints](https://www.spigotmc.org/resources/playerpoints.80745/)
- [LuckPerms](https://luckperms.net/)

## Building

```sh
mvn package
```

The jar is written to `target/ServerPlugin-<version>.jar`. Drop it in your server's `plugins/` folder alongside the dependencies above.
