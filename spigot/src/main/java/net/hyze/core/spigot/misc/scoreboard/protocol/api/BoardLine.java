package net.hyze.core.spigot.misc.scoreboard.protocol.api;

import com.comphenix.protocol.wrappers.EnumWrappers;
import net.hyze.core.shared.messages.MessageUtils;
import org.bukkit.entity.Player;
import net.hyze.core.spigot.misc.scoreboard.protocol.packetwrapper.WrapperPlayServerScoreboardScore;

public final class BoardLine {

    private final Board board;
    private final int index;
    private String text;

    public BoardLine(Board board, int index, String text) {
        this.board = board;
        this.index = index;
        setText(text);
    }

    public int getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        boolean writable = board.isWritable();

        if (!writable) {
            remove();
        }
        
        this.text = text.length() > 48 ? text.substring(0, 48) : text;
        
        if (!writable) {
            update();
        }
    }

    public void update(Player player) {
        preparePacket(EnumWrappers.ScoreboardAction.CHANGE).sendPacket(player);
    }

    public void update() {
        if (board instanceof PersonalBoard) {
            update(((PersonalBoard) board).getOwner());
        } else {
            preparePacket(EnumWrappers.ScoreboardAction.CHANGE).broadcastPacket();
        }
    }

    public void remove() {
        if (board instanceof PersonalBoard) {
            preparePacket(EnumWrappers.ScoreboardAction.REMOVE).sendPacket(((PersonalBoard) board).getOwner());
        } else {
            preparePacket(EnumWrappers.ScoreboardAction.REMOVE).broadcastPacket();
        }
    }

    public WrapperPlayServerScoreboardScore preparePacket(EnumWrappers.ScoreboardAction action) {
        WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
        packet.setObjectiveName(board.getObjective().getName());

        if (text != null) {
            packet.setScoreName(MessageUtils.translateColorCodes(text));
        }

        packet.setValue(index);
        packet.setScoreboardAction(action);

        return packet;
    }
}
