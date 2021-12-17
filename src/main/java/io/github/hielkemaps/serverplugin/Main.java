package io.github.hielkemaps.serverplugin;

import io.github.hielkemaps.serverplugin.commands.Hub;
import io.github.hielkemaps.serverplugin.commands.Spectate;
import io.github.hielkemaps.serverplugin.commands.Tpa;
import io.github.hielkemaps.serverplugin.events.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Set;

public class Main extends JavaPlugin {
    public static Plugin instance;
    public static final String TAG_ENABLE_FLIGHT = "SERVER_ENABLE_FLIGHT";
    public static final String TAG_DISABLE_FLIGHT = "SERVER_DISABLE_FLIGHT";
    public static boolean moreThanOnePlayer = false;

    public Main() {
        instance = this;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(instance, Main::tick, 1L, 1L);
        new Hub();
        new Tpa();
        new Spectate();
    }

    private static void tick() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        for (Player player : onlinePlayers) {
            if (player.getFireTicks() > 0 && player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                player.setFireTicks(0);
            }

            Set<String> tags = player.getScoreboardTags();
            if (tags.contains(TAG_ENABLE_FLIGHT)) {
                player.setAllowFlight(true);
                player.removeScoreboardTag(TAG_ENABLE_FLIGHT);
            }

            if (tags.contains(TAG_DISABLE_FLIGHT)) {
                player.setAllowFlight(false);
                player.removeScoreboardTag(TAG_DISABLE_FLIGHT);
            }
        }

    }
}
