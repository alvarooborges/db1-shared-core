package net.hyze.core.spigot.misc.scoreboard;

public interface Boardable {

    void set(int score, String text);

    void setTitle(String title);

    void reset(int score);

}
