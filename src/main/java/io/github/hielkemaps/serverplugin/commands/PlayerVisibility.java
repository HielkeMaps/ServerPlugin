package io.github.hielkemaps.serverplugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.hielkemaps.serverplugin.PlayerVisibilityOption;
import io.github.hielkemaps.serverplugin.wrapper.PlayerManager;

public class PlayerVisibility {

    public PlayerVisibility() {
        new CommandAPICommand("player_visibliity")
                .withAliases("playervisibility", "pv")

                .withSubcommand(new CommandAPICommand("visible")
                        .executesPlayer((p, args) -> {
                            PlayerManager.getPlayer(p.getUniqueId()).setVisibilityOption(PlayerVisibilityOption.VISIBLE);
                        }))

                .withSubcommand(new CommandAPICommand("invisible")
                        .executesPlayer((p, args) -> {
                            PlayerManager.getPlayer(p.getUniqueId()).setVisibilityOption(PlayerVisibilityOption.INVISIBLE);
                        }))

                .withSubcommand(new CommandAPICommand("ghost")
                        .executesPlayer((p, args) -> {
                            PlayerManager.getPlayer(p.getUniqueId()).setVisibilityOption(PlayerVisibilityOption.GHOST);
                        }))

                .register();
    }
}