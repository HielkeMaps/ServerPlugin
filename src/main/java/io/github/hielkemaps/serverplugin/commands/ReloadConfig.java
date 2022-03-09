package io.github.hielkemaps.serverplugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.hielkemaps.serverplugin.Main;

public class ReloadConfig {
    public ReloadConfig() {

        new CommandAPICommand("serverplugin")
                .withSubcommand(new CommandAPICommand("reload")
                        .executes(((commandSender, objects) -> {

                            Main.getInstance().reloadConfig();
                            Main.updateCommands();
                            commandSender.sendMessage("Reloaded config!");
                        }))
                ).register();
    }
}
