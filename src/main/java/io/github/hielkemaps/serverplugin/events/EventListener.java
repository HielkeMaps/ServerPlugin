package io.github.hielkemaps.serverplugin.events;

import dev.jorel.commandapi.CommandAPI;
import io.github.hielkemaps.serverplugin.Main;
import io.github.hielkemaps.serverplugin.PlayerVisibilityOption;
import io.github.hielkemaps.serverplugin.wrapper.PlayerManager;
import io.github.hielkemaps.serverplugin.wrapper.PlayerWrapper;
import java.util.Collection;
import java.util.Set;
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

        PlayerWrapper player = PlayerManager.getPlayer(e.getPlayer().getUniqueId());

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.getUniqueId().equals(e.getPlayer().getUniqueId())) {
                PlayerWrapper wrapper = PlayerManager.getPlayer(other.getUniqueId());

                // If the other player has players invisible, hide the new player
                if (wrapper.getVisibilityOption() == PlayerVisibilityOption.INVISIBLE) {
                    other.hidePlayer(Main.getInstance(), e.getPlayer());
                } else {
                    other.showPlayer(Main.getInstance(), e.getPlayer());
                }

                // If the new player has players invisible, hide the other player
                if (player.getVisibilityOption() == PlayerVisibilityOption.INVISIBLE) {
                    e.getPlayer().hidePlayer(Main.getInstance(), other);
                } else {
                    e.getPlayer().showPlayer(Main.getInstance(), other);
                }

                // Don't need to handle ghost players here, as it is handled by the PacketListener
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
