package io.github.hielkemaps.serverplugin;

import dev.jorel.commandapi.CommandAPI;
import io.github.hielkemaps.serverplugin.commands.*;
import io.github.hielkemaps.serverplugin.events.EventListener;
import io.github.hielkemaps.serverplugin.wrapper.PlayerManager;
import io.github.hielkemaps.serverplugin.wrapper.PlayerWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Main extends JavaPlugin {
    public static Plugin instance;
    public static final String TAG_ENABLE_FLIGHT = "SERVER_ENABLE_FLIGHT";
    public static final String TAG_DISABLE_FLIGHT = "SERVER_DISABLE_FLIGHT";
    public static boolean moreThanOnePlayer = false;

    public static Scoreboard scoreboard;
    public static Map<UUID, String> teams = new HashMap<>();
    public static int pointsGiven;
    public static int pointsBonus;

    public Main() {
        instance = this;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static PlayerPointsAPI pointsAPI;

    public void onEnable() {
        this.saveDefaultConfig();
        new ReloadConfig();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);

        this.getServer().getScheduler().scheduleSyncRepeatingTask(instance, Main::tick, 1L, 1L);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(instance, Main::tick_20, 1L, 20L);

        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        pointsAPI = PlayerPoints.getInstance().getAPI();

        loadConfig();
    }

    public static void loadConfig() {
        pointsGiven = Main.getInstance().getConfig().getInt("points-given");
        pointsBonus = Main.getInstance().getConfig().getInt("points-bonus");

        List<String> disabledCommands = Main.getInstance().getConfig().getStringList("disabled-commands");
        disabledCommands.forEach(c -> Bukkit.getLogger().info("[ServerPlugin] Disabled command " + c));

        if (!disabledCommands.contains("hub")) new Hub();
        else CommandAPI.unregister("hub");

        if (!disabledCommands.contains("tpa")) new Tpa();
        else CommandAPI.unregister("tpa");

        if (!disabledCommands.contains("spectate")) new Spectate();
        else CommandAPI.unregister("spectate");

        if (!disabledCommands.contains("highscores")) new Highscores();
        else CommandAPI.unregister("highscores");

        Bukkit.getOnlinePlayers().forEach(CommandAPI::updateRequirements);
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
                if (player.getGameMode() != GameMode.CREATIVE) player.setAllowFlight(false);
                player.removeScoreboardTag(TAG_DISABLE_FLIGHT);
            }
        }
    }

    private static void tick_20() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {

            if (player.getScoreboardTags().contains("training_mode")) continue;

            Team team = scoreboard.getEntityTeam(player);
            if (team == null) continue;

            UUID playerUUID = player.getUniqueId();
            String currentTeam = team.getName();
            String prevTeam = teams.get(playerUUID);
            teams.put(playerUUID, currentTeam);

            if (prevTeam != null && !currentTeam.equals(prevTeam)) {
                if (currentTeam.equals("finished")) {

                    PlayerWrapper player1 = PlayerManager.getPlayer(playerUUID);
                    if (player1.shouldGetCoins()) {

                        int addedCoins = pointsGiven;

                        //Reward for S rank
                        if(player.getScoreboardTags().contains("rank_s")){
                            addedCoins += pointsBonus;
                        }

                        pointsAPI.give(playerUUID, addedCoins);
                        player.sendActionBar(Component.text("You earned " + addedCoins + " Parcoins!").color(TextColor.fromHexString("#FFFFFC")));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

                        int finalAddedCoins = addedCoins;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            if (player.isOnline()) {
                                player.sendActionBar(Component.text("You earned " + finalAddedCoins + " Parcoins!").color(TextColor.fromHexString("#FFFFFC")));
                            }
                        }, 40);
                    } else {
                        player.sendMessage(Component.text(":cancel: You did not receive any Parcoins because you used /tpa :cancel:").color(TextColor.fromHexString("#FF0000")));
                    }
                }

                if(currentTeam.equals("main")){
                    PlayerManager.getPlayer(playerUUID).setGetCoins(true);
                }
            }
        }
    }
}
