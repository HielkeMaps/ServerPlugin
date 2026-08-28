package io.github.hielkemaps.serverplugin.events;

import com.hibiscusmc.hmccosmetics.api.events.PlayerCosmeticEquipEvent;
import io.github.hielkemaps.serverplugin.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Everyone may open the cosmetics menu and browse; equipping any cosmetic additionally needs
 * {@code wardrobe.equip-permission} (VIP+). Same gate as LobbyPlugin's. Only registered when HMCCosmetics is loaded.
 */
public final class CosmeticEquipGate implements Listener {
    private final Map<UUID, Long> lastMessage = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEquip(PlayerCosmeticEquipEvent event) {
        var config = Main.getInstance().getConfig();
        String perm = config.getString("wardrobe.equip-permission", "");
        if (perm.isEmpty()) return;
        Player player = event.getUser().getPlayer();
        if (player == null || player.hasPermission(perm)) return;
        event.setCancelled(true);
        String msg = config.getString("wardrobe.equip-no-permission-message", "");
        long now = System.currentTimeMillis();
        if (!msg.isEmpty() && now - lastMessage.getOrDefault(player.getUniqueId(), 0L) > 1500) {
            lastMessage.put(player.getUniqueId(), now);
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
        }
    }
}
