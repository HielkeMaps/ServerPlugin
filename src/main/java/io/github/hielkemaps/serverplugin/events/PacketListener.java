package io.github.hielkemaps.serverplugin.events;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
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

        if (entity instanceof Player) {
            if (!entity.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                for (WrappedWatchableObject watchedObject : packet.getMetadata()) {
                    if (watchedObject.getIndex() == 0) {
                        if (player.getVisibilityOption() == PlayerVisibilityOption.GHOST) {
                            if (((byte) watchedObject.getValue() & 0x20) != 0x20) {
                                WrappedDataWatcher watcher = new WrappedDataWatcher();
                                WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
                                watcher.setEntity(event.getPlayer());
                                watcher.setObject(0, serializer, (byte) ((byte) watchedObject.getRawValue() | 0x20));

                                WrapperPlayServerEntityMetadata newPacket = new WrapperPlayServerEntityMetadata();
                                newPacket.setMetadata(watcher.getWatchableObjects());

                                newPacket.setEntityID(packet.getEntityID());
                                newPacket.sendPacket(event.getPlayer());

                                event.setCancelled(true);
                            }
                        } else {
                            if (((byte) watchedObject.getRawValue() & 0x20) == 0x20) {

                                WrappedDataWatcher watcher = new WrappedDataWatcher();
                                WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
                                watcher.setEntity(event.getPlayer());
                                watcher.setObject(0, serializer, (byte) ((byte) watchedObject.getRawValue() & (0xFF - 0x20)));

                                WrapperPlayServerEntityMetadata newPacket = new WrapperPlayServerEntityMetadata();
                                newPacket.setMetadata(watcher.getWatchableObjects());

                                newPacket.setEntityID(packet.getEntityID());
                                newPacket.sendPacket(event.getPlayer());

                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}