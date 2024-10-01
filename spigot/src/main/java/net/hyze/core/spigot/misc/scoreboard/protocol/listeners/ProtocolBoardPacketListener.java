package net.hyze.core.spigot.misc.scoreboard.protocol.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Maps;
import net.hyze.core.spigot.misc.scoreboard.protocol.api.Board;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import net.hyze.core.spigot.misc.scoreboard.protocol.packetwrapper.WrapperPlayServerScoreboardDisplayObjective;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtocolBoardPacketListener extends PacketAdapter implements Listener {

    private Map<Player, String> scoreboards = Maps.newConcurrentMap();

    public ProtocolBoardPacketListener(JavaPlugin plugin) {
        super(plugin, PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrapperPlayServerScoreboardDisplayObjective packet
                = new WrapperPlayServerScoreboardDisplayObjective(event.getPacket());
        if (packet.getPosition() != 1) {
            return;
        }
        if (scoreboards.containsKey(event.getPlayer())) {
            scoreboards.remove(event.getPlayer());
        }
        scoreboards.put(event.getPlayer(), packet.getScoreName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (scoreboards.containsKey(event.getPlayer())) {
            scoreboards.remove(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        for (Board board : Board.REGISTERED_BOARDS) {
            board.getObjective().send(event.getPlayer());
            board.lineWrappers().forEach(line -> line.update(event.getPlayer()));
        }
    }

    public String getCurrentScoreboard(Player player) {
        return scoreboards.get(player);
    }

    public Set<Player> getPlayersWithObjective(String objective) {
        return scoreboards.entrySet().stream()
                .filter(entry -> entry.getValue().equals(objective))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
