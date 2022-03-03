package io.github.hielkemaps.serverplugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.IntegerArgument;
import io.github.hielkemaps.serverplugin.objects.ScorePair;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Highscores {
    public Highscores() {
        (new CommandAPICommand("highscores"))
                .withPermission(CommandPermission.OP)
                .withArguments(new IntegerArgument("count"))
                .executesPlayer((p, args) -> {

                    int count = (int) args[0];

                    List<ScorePair> highscores = getHighScores(count);

                    for (int i = 0; i < highscores.size(); i++) {
                        ScorePair pair = highscores.get(i);
                        int score = pair.getScore();
                        p.sendMessage(i + 1 + ": " + pair.getName() + "," + pair.getTimeString() + " (" + score + ")");
                    }
                }).register();
    }

    public List<ScorePair> getHighScores(int maxCount) {
        List<ScorePair> list = new ArrayList<>();

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return null;

        Scoreboard scoreboard = manager.getMainScoreboard();
        Objective pbObjective = scoreboard.getObjective("time_pb");
        if (pbObjective == null) return null;

        int i = 0;
        Set<String> entries = scoreboard.getEntries();
        for (String entry : entries) {
            if(i == maxCount) break;

            int score = pbObjective.getScore(entry).getScore();
            if (score == 0 || score == 2147483647) continue; //we don't want empty scores
            list.add(new ScorePair(entry,score));
            i++;
        }

        Collections.sort(list);

        return list;
    }
}
