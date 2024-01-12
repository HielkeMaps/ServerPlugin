package io.github.hielkemaps.serverplugin.wrapper;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import dev.jorel.commandapi.CommandAPI;
import io.github.hielkemaps.serverplugin.Main;
import io.github.hielkemaps.serverplugin.PlayerVisibilityOption;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
                player.sendMessage(Component.text("Players are already set to " + option.toString().toLowerCase() + "!").color(TextColor.fromHexString("#FF0000")));
                return;
            }

            this.visibilityOption = option;

            switch (option) {
                case VISIBLE -> {
                    List<WrappedDataValue> data = Lists.newArrayList(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0));

                    for (Player other : Bukkit.getOnlinePlayers()) {
                        if (!other.getUniqueId().equals(uuid)) {
                            player.showPlayer(Main.getInstance(), other);

                            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
                            packet.setEntityID(other.getEntityId());
                            packet.setMetadata(data);
                            packet.sendPacket(player);
                        }
                    }
                }
                case GHOST -> {
                    List<WrappedDataValue> data = Lists.newArrayList(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 20));

                    for (Player other : Bukkit.getOnlinePlayers()) {
                        if (!other.getUniqueId().equals(uuid)) {
                            player.showPlayer(Main.getInstance(), other);

                            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
                            packet.setEntityID(other.getEntityId());
                            packet.setMetadata(data);
                            packet.sendPacket(player);
                        }
                    }
                }
                case INVISIBLE -> {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        if (!other.getUniqueId().equals(uuid)) {
                            player.hidePlayer(Main.getInstance(), other);
                        }
                    }
                }
            }
        }
    }
}
