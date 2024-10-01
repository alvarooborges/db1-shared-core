package net.hyze.core.spigot.misc.scoreboard.protocol.api;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import net.hyze.core.spigot.misc.scoreboard.protocol.packetwrapper.WrapperPlayServerScoreboardDisplayObjective;
import net.hyze.core.spigot.misc.scoreboard.protocol.packetwrapper.WrapperPlayServerScoreboardObjective;
import lombok.Getter;

public final class BoardObjective {

    private final WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();
    private final Board board;

    @Getter
    private boolean registered = false;

    public BoardObjective(Board board, String name, String displayName) {
        this.board = board;
        packet.setName(name.length() > 16 ? name.substring(0, 16) : name);
        setDisplayName(displayName);
    }

    public String getName() {
        return packet.getName();
    }

    public void setDisplayName(String displayName) {
        packet.setDisplayName(displayName.length() > 32 ? displayName.substring(0, 32) : displayName);
        
        if (isRegistered()) {
            update();
        }
    }

    public void register() {
        Preconditions.checkArgument(!isRegistered(), "Objective already registered");

        packet.setMode(WrapperPlayServerScoreboardObjective.Mode.ADD_OBJECTIVE);

        if (board instanceof PersonalBoard) {
            packet.sendPacket(((PersonalBoard) board).getOwner());
        } else {
            packet.broadcastPacket();
        }

        registered = true;
    }

    public void send(Player player) {
        packet.setMode(WrapperPlayServerScoreboardObjective.Mode.ADD_OBJECTIVE);
        packet.sendPacket(player);
    }

    public void display(Player... players) {
        WrapperPlayServerScoreboardDisplayObjective displayPacket = new WrapperPlayServerScoreboardDisplayObjective();

        displayPacket.setScoreName(getName());
        displayPacket.setPosition(1);

        for (Player player : players) {
            displayPacket.sendPacket(player);
        }
    }

    public void update() {
        packet.setMode(WrapperPlayServerScoreboardObjective.Mode.UPDATE_VALUE);

        if (board instanceof PersonalBoard) {
            packet.sendPacket(((PersonalBoard) board).getOwner());
        } else {
            packet.broadcastPacket();
        }
    }

    public void unregister() {
        Preconditions.checkArgument(isRegistered(), "Objective must be registered");

        board.lineWrappers().forEach(BoardLine::remove);

        packet.setMode(WrapperPlayServerScoreboardObjective.Mode.REMOVE_OBJECTIVE);

        if (board instanceof PersonalBoard) {
            packet.sendPacket(((PersonalBoard) board).getOwner());
        } else {
            packet.broadcastPacket();
        }

        registered = false;
    }
}
