package net.hyze.core.spigot.misc.scoreboard.protocol.api;

public class BoardBuilder {

    private Board board;

    BoardBuilder(Board board) {
        this.board = board;
    }

    public BoardBuilder write(int index, String text) {
        board.write(index, text);
        return this;
    }

    public BoardBuilder updater(long interval, BoardUpdater updater) {
        board.addUpdater(interval, updater);
        return this;
    }

    public Board build() {
        board.create();
        return board;
    }
}
