package net.hyze.core.spigot.misc.scoreboard.bukkit;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import lombok.Getter;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.spigot.misc.scoreboard.Boardable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class BaseScoreboard implements Boardable {

    private final TreeMap<Integer, Team> teams = Maps.newTreeMap();
    private final Map<Integer, String> entries = Maps.newHashMap();

    @Getter
    private String title;

    @Getter
    protected Scoreboard scoreboard;

    protected Objective objective;

    public BaseScoreboard(Player player) {
        this.scoreboard = player.getScoreboard();
        this.objective = scoreboard.getObjective("main" + Bukkit.getPort());
    }

    public BaseScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("main" + Bukkit.getPort(), "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (this instanceof ScoreboardMarkable) {
            add(((ScoreboardMarkable) this).mark());
        }
    }

    @Override
    public void setTitle(String title) {
        this.title = MessageUtils.translateColorCodes(title);
        objective.setDisplayName(this.title);
    }

    @Override
    public void reset(int score) {
        String entry = entries.get(score);

        if (entry == null) {
            return;
        }

        if (teams.containsKey(score)) {
            Team team = teams.remove(score);

            if (team != null) {
                scoreboard.resetScores(entry);
                entries.remove(score);
                try {
                    team.unregister();
                } catch (IllegalStateException e) {
                }
            }
        }
    }

    @Override
    public void set(int score, String text) {
        text = MessageUtils.translateColorCodes(text);

        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        StringBuilder prefixBuilder = new StringBuilder(iterator.next());
        StringBuilder suffixBuilder = iterator.hasNext() ? new StringBuilder(iterator.next()) : new StringBuilder();

        int index = prefixBuilder.length() - 1;
        if (prefixBuilder.charAt(index) == ChatColor.COLOR_CHAR) {
            prefixBuilder.deleteCharAt(index);
            suffixBuilder.insert(0, ChatColor.COLOR_CHAR);
        }

        suffixBuilder.insert(0, ChatColor.getLastColors(prefixBuilder.toString()));

        String prefix = prefixBuilder.toString();
        String suffix = suffixBuilder.toString();

        Team team = teams.get(score);
        boolean fresh = team == null;

        String entry = (fresh ? hash(score) : entries.get(score));

        if (fresh) {

            try {
                team = scoreboard.registerNewTeam(String.format("%d-t", score));
            } catch (IllegalArgumentException e) {
                team = scoreboard.getTeam(String.format("%d-t", score));
                System.err.println("[Scoreboard] failed register - get " + (team == null ? String.format("%d-t", score) : team.getName()));
            }

            try {
                if (team == null) {
                    team = scoreboard.registerNewTeam(String.format("%d-t", score));
                }
            } catch (Exception e) {
                System.err.println("[Scoreboard] failed re-register - get " + (team == null ? String.format("%d-t", score) : team.getName()));
                return;
            }

            if (!team.hasEntry(entry)) {
                team.addEntry(entry);
            }

            teams.put(score, team);
            entries.put(score, entry);
        }

        try {
            if (!Objects.equals(team.getPrefix(), prefix)) {
                team.setPrefix(prefix);
            }
        } catch (IllegalStateException e) {
            try {
                team.setPrefix(prefix);
            } catch (IllegalStateException ex) {
                System.err.println("[Scoreboard] failed set prefix - " + score + " - " + entry + " - " + prefix + " - " + fresh);
                ex.printStackTrace();
            }
        }

        try {
            if (!Objects.equals(team.getSuffix(), suffix)) {
                team.setSuffix(StringUtils.substring(suffix, 0, 15));
            }
        } catch (IllegalStateException e) {
            try {
                team.setSuffix(StringUtils.substring(suffix, 0, 15));
            } catch (IllegalStateException ex) {
                System.err.println("[Scoreboard] failed set suffix - " + score + " - " + entry + " - " + suffix + " - " + fresh);
                ex.printStackTrace();
            }
        }

        if (fresh) {
            objective.getScore(entry).setScore(score);
        }
    }

    public void clear() {
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        teams.values().forEach((Team t) -> t.unregister());

        entries.clear();
        teams.clear();
    }

    public final void add(String text) {
        if (teams.isEmpty()) {
            set(1, text);
        } else {
            set(teams.lastKey() + 1, text);
        }
    }

    public String getEntry(int score) {
        return entries.get(score);
    }

    public boolean exists(int score) {
        return entries.containsKey(score);
    }

    public void send(Player... players) {
        for (Player player : players) {
            player.setScoreboard(scoreboard);
        }
    }

    public void send(Collection<Player> players) {
        for (Player player : players) {
            player.setScoreboard(scoreboard);
        }
    }

    private String hash(int score) {
        StringBuilder builder = new StringBuilder();

        for (char character : Integer.toHexString(score).toCharArray()) {
            ChatColor color = ChatColor.getByChar(character);
            if (color != null) {
                builder.append(color.toString());
            }
        }

        return builder.toString();
    }
}
