package io.github.hielkemaps.serverplugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;

public class LocalTime {
    public LocalTime() {
        (new CommandAPICommand("localtime"))
                .withArguments(new IntegerArgument("time"))
                .executesPlayer((p, args) -> {
                    int time = (int) args.get("time");
                    p.setPlayerTime(time, false);
                    p.sendMessage("Set your local game time to " + time);
                }).register();
    }
}
