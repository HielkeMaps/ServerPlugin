package io.github.hielkemaps.serverplugin.objects;

public class ScorePair implements Comparable<ScorePair> {

    private String name;
    private int score;

    public ScorePair(String name, int score){
        this.name = name;
        this.score = score;
    }

    public int getScore(){
        return score;
    }

    public String getName(){
        return name;
    }

    public String getTimeString() {
        int hours = score / 3600;
        int secondsLeft = score - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds ;

        return formattedTime;
    }

    @Override
    public int compareTo(ScorePair o) {
        return Integer.compare(getScore(), o.getScore());
    }
}
