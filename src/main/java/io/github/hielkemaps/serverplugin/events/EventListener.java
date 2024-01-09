package io.github.hielkemaps.serverplugin.events;

import dev.jorel.commandapi.CommandAPI;
import io.github.hielkemaps.serverplugin.Main;
import io.github.hielkemaps.serverplugin.wrapper.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Set;

public class EventListener implements Listener {
    Set<Material> flowerpots;

    public EventListener() {
        this.flowerpots = Tag.FLOWER_POTS.getValues();
    }

    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block != null) {
            if (event.getAction() == Action.PHYSICAL && block.getType() == Material.FARMLAND) {
                event.setCancelled(true);
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    return;
                }

                if (block.getType() == Material.SWEET_BERRY_BUSH || block.getType() == Material.CAVE_VINES) {
                    event.setCancelled(true);
                } else if (this.flowerpots.contains(block.getType()) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!Main.moreThanOnePlayer) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if (onlinePlayers.size() > 1) {
                Main.moreThanOnePlayer = true;
                onlinePlayers.forEach(CommandAPI::updateRequirements);
            }
        }
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent e) {
        if (Main.moreThanOnePlayer) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if (onlinePlayers.size() == 1) {
                Main.moreThanOnePlayer = false;
                onlinePlayers.forEach(CommandAPI::updateRequirements);
            }
        }

        Player p = e.getPlayer();
        if (p.isInsideVehicle()) {
            p.getVehicle().eject();
        }

        PlayerManager.getPlayer(p.getUniqueId()).clearOutgoing();
    }
}
