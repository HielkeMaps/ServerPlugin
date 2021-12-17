package io.github.hielkemaps.serverplugin.wrapper;

import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerWrapper {
    private final UUID uuid;
    private final Set<UUID> incoming = new HashSet<>();
    private final Set<UUID> outgoing = new HashSet<>();

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
        Player player = Bukkit.getPlayer(this.uuid);
        if (player != null) {
            CommandAPI.updateRequirements(player);
        }
    }
}
