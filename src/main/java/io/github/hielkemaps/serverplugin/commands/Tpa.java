package io.github.hielkemaps.serverplugin.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.github.hielkemaps.serverplugin.Main;
import io.github.hielkemaps.serverplugin.wrapper.PlayerManager;
import io.github.hielkemaps.serverplugin.wrapper.PlayerWrapper;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public class Tpa {
    Predicate<CommandSender> hasInvites = (sender) -> PlayerManager.getPlayer(((Player)sender).getUniqueId()).hasIncoming();
    Predicate<CommandSender> othersOnline = (sender) -> Main.moreThanOnePlayer;

    public Tpa() {
        (new CommandAPICommand("tpa")).withRequirement(this.othersOnline).withArguments((new PlayerArgument("player"))
                .replaceSuggestions((info) -> Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).filter((name) -> !info.sender().getName().equals(name)).toArray(String[]::new)))
                .executesPlayer((p, args) -> {
            Player target = (Player)args[0];
            if (p.getScoreboardTags().contains("inRace")) {
                CommandAPI.fail("You can't use this command while in a race");
            }

            if (target.getUniqueId().equals(p.getUniqueId())) {
                CommandAPI.fail("You can't tpa to yourself silly!");
            } else if (PlayerManager.getPlayer(target.getUniqueId()).addIncoming(p.getUniqueId())) {
                TextComponent msg = new TextComponent(ChatColor.GOLD + "" + ChatColor.BOLD + p.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + " wants to teleport to you! Use ");
                TextComponent accept = new TextComponent(ChatColor.GOLD + "/tpaccept");
                accept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpaccept"));
                accept.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new Text("Accept request")));
                TextComponent msg2 = new TextComponent(ChatColor.YELLOW + " to accept!");
                msg.addExtra(accept);
                msg.addExtra(msg2);
                target.spigot().sendMessage(msg);
                PlayerManager.getPlayer(p.getUniqueId()).addOutGoing(target.getUniqueId());
                p.sendMessage(ChatColor.YELLOW + "Teleport request sent to " + ChatColor.GOLD + target.getDisplayName());
                target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1.0F, 1.0F);
            } else {
                CommandAPI.fail("You can only send one request");
            }
        }).register();
        (new CommandAPICommand("tpaccept")).withRequirement(this.hasInvites).executesPlayer((p, args) -> {
            if (p.getScoreboardTags().contains("inRace")) {
                CommandAPI.fail("You can't use this command while in a race");
            }

            PlayerWrapper player = PlayerManager.getPlayer(p.getUniqueId());

            for (UUID uuid : player.getIncoming()) {
                Player invited = Bukkit.getPlayer(uuid);
                if (invited != null) {
                    invited.teleport(p);
                    PlayerManager.getPlayer(uuid).removeOutgoing(p.getUniqueId());
                    invited.playSound(invited.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1.0F, 1.0F);
                    invited.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.GOLD + p.getDisplayName());
                }
            }

            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1.0F, 1.0F);
            p.sendMessage(ChatColor.YELLOW + "Accepted incoming teleports");
            player.clearIncoming();
        }).register();
    }
}
