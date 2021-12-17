package io.github.hielkemaps.serverplugin.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private static final Map<UUID, PlayerWrapper> players = new HashMap<>();

    public PlayerManager() {
    }

    public static PlayerWrapper getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            players.put(uuid, new PlayerWrapper(uuid));
        }

        return players.get(uuid);
    }
}
