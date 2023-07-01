package io.github.hielkemaps.serverplugin.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Spectate {
    public Spectate() {
        CommandAPI.unregister("spectate", true);
        (new CommandAPICommand("spectate")).executesPlayer((p, args) -> {
            if (p.getScoreboardTags().contains("inRace")) {
                throw CommandAPI.failWithString("You can't use this command while in a race");
            }

            if (p.getGameMode() == GameMode.SPECTATOR) {
                p.setGameMode(GameMode.ADVENTURE);
                p.removeScoreboardTag("joined");
            } else {
                p.removeScoreboardTag("ingame");
                p.setGameMode(GameMode.SPECTATOR);
            }

        }).register();
        (new CommandAPICommand("spectate"))
                .withArguments(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info ->
                        Bukkit.getOnlinePlayers().stream().filter(player -> !player.getGameMode().equals(GameMode.SPECTATOR) && !info.sender().getName().equals(player.getName())).map(OfflinePlayer::getName).toArray(String[]::new)))
                )
                .executesPlayer((p, args) -> {
            Player target = (Player)args.get("player");
            if (p.getScoreboardTags().contains("inRace")) {
                throw CommandAPI.failWithString("You can't use this command while in a race");
            }

            if (target.getUniqueId().equals(p.getUniqueId())) {
                throw CommandAPI.failWithString("Cannot spectate yourself");
            }

            if (target.getGameMode().equals(GameMode.SPECTATOR)) {
                throw CommandAPI.failWithString("Player must not be in spectator mode");
            }

            p.removeScoreboardTag("ingame");

            if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                p.setGameMode(GameMode.SPECTATOR);
            }

            p.setSpectatorTarget(target);
        }).register();
    }
}
