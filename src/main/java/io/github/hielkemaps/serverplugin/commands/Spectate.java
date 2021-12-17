package io.github.hielkemaps.serverplugin.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.EntitySelector;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Spectate {
    public Spectate() {
        CommandAPI.unregister("spectate", true);
        (new CommandAPICommand("spectate")).executesPlayer((p, args) -> {
            if (p.getScoreboardTags().contains("inRace")) {
                CommandAPI.fail("You can't use this command while in a race");
            }

            if (p.getGameMode() == GameMode.SPECTATOR) {
                p.setGameMode(GameMode.ADVENTURE);
                p.removeScoreboardTag("joined");
            } else {
                p.setGameMode(GameMode.SPECTATOR);
            }

        }).register();
        (new CommandAPICommand("spectate")).withArguments((new EntitySelectorArgument("player", EntitySelector.ONE_PLAYER))
                .replaceSuggestions((info) -> Bukkit.getOnlinePlayers().stream().filter((player) -> !player.getGameMode().equals(GameMode.SPECTATOR) && !info.sender().getName().equals(player.getName())).map(OfflinePlayer::getName).toArray(String[]::new)))
                .executesPlayer((p, args) -> {
            Player target = (Player)args[0];
            if (p.getScoreboardTags().contains("inRace")) {
                CommandAPI.fail("You can't use this command while in a race");
            }

            if (target.getUniqueId().equals(p.getUniqueId())) {
                CommandAPI.fail("Cannot spectate yourself");
            }

            if (target.getGameMode().equals(GameMode.SPECTATOR)) {
                CommandAPI.fail("Player must not be in spectator mode");
            }

            if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                p.setGameMode(GameMode.SPECTATOR);
            }

            p.setSpectatorTarget(target);
        }).register();
    }
}
