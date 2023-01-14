package io.github.hielkemaps.serverplugin.wrapper;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.jorel.commandapi.CommandAPI;
import io.github.hielkemaps.serverplugin.Main;
import io.github.hielkemaps.serverplugin.PlayerVisibilityOption;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerWrapper {
    private final UUID uuid;
    private final Set<UUID> incoming = new HashSet<>();
    private final Set<UUID> outgoing = new HashSet<>();
    private boolean getCoins = true;

    private PlayerVisibilityOption visibilityOption = PlayerVisibilityOption.VISIBLE;

    public PlayerWrapper(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean addIncoming(UUID uuid) {
        if (this.incoming.contains(uuid)) {
            return false;
        } else {
            this.incoming.add(uuid);
            this.updateRequirements();
            return true;
        }
    }

    public void removeIncoming(UUID uuid) {
        this.incoming.remove(uuid);
        this.updateRequirements();
    }

    public boolean shouldGetCoins() {
        return this.getCoins;
    }

    public void setGetCoins(boolean getCoins) {
        this.getCoins = getCoins;
    }

    public boolean hasIncoming() {
        return !this.incoming.isEmpty();
    }

    public void clearIncoming() {
        this.incoming.clear();
        this.updateRequirements();
    }

    public Set<UUID> getIncoming() {
        return this.incoming;
    }

    public void addOutGoing(UUID uuid) {
        this.outgoing.add(uuid);
    }

    public void afterTeleport(UUID uuid) {
        this.removeOutgoing(uuid);
        this.getCoins = false;
    }

    public void removeOutgoing(UUID uuid) {
        this.outgoing.remove(uuid);
        this.updateRequirements();
    }

    public void clearOutgoing() {
        for (UUID uuid : this.outgoing) {
            PlayerManager.getPlayer(uuid).removeIncoming(this.uuid);
        }
        this.outgoing.clear();
    }

    public void updateRequirements() {
        Player player = getPlayer();
        if (player != null) {
            CommandAPI.updateRequirements(player);
        }
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public PlayerVisibilityOption getVisibilityOption() {
        return this.visibilityOption;
    }

    public void setVisibilityOption(PlayerVisibilityOption option) {
        Player player = getPlayer();

        if (player != null) {
            if (this.visibilityOption == option) {
                player.sendMessage(ChatColor.RED + "You are already " + option.toString().toLowerCase() + "!");
                return;
            }

            this.visibilityOption = option;

            if (option == PlayerVisibilityOption.VISIBLE) {
                player.sendMessage(ChatColor.GRAY + "Players are now visible");

                WrappedDataWatcher watcher = new WrappedDataWatcher();
                WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
                watcher.setEntity(player);
                watcher.setObject(0, serializer, (byte) 0x0);

                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!uuid.equals(other.getUniqueId())) {
                        player.showPlayer(Main.getInstance(), other);

                        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
                        packet.setMetadata(watcher.getWatchableObjects());

                        packet.setEntityID(other.getEntityId());
                        packet.sendPacket(player);
                    }
                }
            } else if (option == PlayerVisibilityOption.INVISIBLE) {
                player.sendMessage(ChatColor.GRAY + "Players are now invisible");

                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!uuid.equals(other.getUniqueId())) {
                        player.hidePlayer(Main.getInstance(), other);
                    }
                }
            } else if (option == PlayerVisibilityOption.GHOST) {
                player.sendMessage(ChatColor.GRAY + "Players are now semi-transparent");

                WrappedDataWatcher watcher = new WrappedDataWatcher();
                WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
                watcher.setEntity(player);
                watcher.setObject(0, serializer, (byte) 0x20);

                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!uuid.equals(other.getUniqueId())) {
                        player.showPlayer(Main.getInstance(), other);

                        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
                        packet.setMetadata(watcher.getWatchableObjects());

                        packet.setEntityID(other.getEntityId());
                        packet.sendPacket(player);
                    }
                }
            }
        }
    }
}
