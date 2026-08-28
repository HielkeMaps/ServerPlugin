package io.github.hielkemaps.serverplugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import io.github.hielkemaps.serverplugin.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * /wardrobe [player]: opens the HMCCosmetics cosmetics menu (no physical wardrobe on the maps, unlike the Lobby).
 * The HMCC command runs from console so players never need hmccosmetics.* permissions; equipping is gated by
 * {@link io.github.hielkemaps.serverplugin.events.CosmeticEquipGate}. Console/OP may pass a player.
 */
public class Wardrobe {
    public Wardrobe() {
        new CommandAPICommand("wardrobe")
                .withOptionalArguments(new EntitySelectorArgument.OnePlayer("player")
                        .withPermission(CommandPermission.fromString("serverplugin.command.wardrobe.other")))
                .executes((sender, args) -> {
                    Player target = (Player) args.getOptional("player").orElse(sender instanceof Player p ? p : null);
                    if (target == null) {
                        sender.sendMessage(Component.text("Usage: /wardrobe <player>").color(NamedTextColor.RED));
                        return;
                    }
                    open(target);
                })
                .register();
    }

    public static void open(Player player) {
        var config = Main.getInstance().getConfig();
        String perm = config.getString("wardrobe.permission", "");
        if (!perm.isEmpty() && !player.hasPermission(perm)) {
            String msg = config.getString("wardrobe.no-permission-message", "");
            if (!msg.isEmpty()) player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return;
        }
        for (String cmd : config.getStringList("wardrobe.console-commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player_name%", player.getName()));
        }
    }
}
