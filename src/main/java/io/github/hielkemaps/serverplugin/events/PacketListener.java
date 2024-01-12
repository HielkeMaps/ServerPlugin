package io.github.hielkemaps.serverplugin.events;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.Lists;
import io.github.hielkemaps.serverplugin.PlayerVisibilityOption;
import io.github.hielkemaps.serverplugin.wrapper.PlayerManager;
import io.github.hielkemaps.serverplugin.wrapper.PlayerWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PacketListener extends PacketAdapter {

    public PacketListener(Plugin plugin) {
        super(plugin, PacketType.Play.Server.ENTITY_METADATA);
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        PlayerWrapper player = PlayerManager.getPlayer(event.getPlayer().getUniqueId());

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event.getPacket());
        Entity entity = packet.getEntity(event);

        // Check if the entity is a player and if it is not the player that is sending the packet
        if (entity instanceof Player) {
            if (!entity.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                for (WrappedDataValue watchedObject : packet.getMetadata()) {

                    // Index 0 is the bitmask that contains the visibility flag (0x20)
                    if (watchedObject.getIndex() == 0) {
                        if (player.getVisibilityOption() == PlayerVisibilityOption.GHOST) {
                            if (((byte) watchedObject.getValue() & 0x20) != 0x20) {
                                WrapperPlayServerEntityMetadata newPacket = new WrapperPlayServerEntityMetadata();
                                newPacket.setMetadata(Lists.newArrayList(
                                        new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) ((byte) watchedObject.getRawValue() | 0x20))
                                ));

                                newPacket.setEntityID(packet.getEntityID());
                                newPacket.sendPacket(event.getPlayer());

                                event.setCancelled(true);
                            }
                        } else {
                            if (((byte) watchedObject.getRawValue() & 0x20) == 0x20) {
                                WrapperPlayServerEntityMetadata newPacket = new WrapperPlayServerEntityMetadata();
                                newPacket.setMetadata(Lists.newArrayList(
                                        new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) ((byte) watchedObject.getRawValue() & (0xFF - 0x20)))
                                ));

                                newPacket.setEntityID(packet.getEntityID());
                                newPacket.sendPacket(event.getPlayer());

                                event.setCancelled(true);
                            }
                        }

                        return;
                    }
                }
            }
        }
    }
}