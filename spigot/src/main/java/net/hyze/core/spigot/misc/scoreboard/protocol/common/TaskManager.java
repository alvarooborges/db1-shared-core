package net.hyze.core.spigot.misc.scoreboard.protocol.common;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.hyze.core.spigot.misc.scoreboard.protocol.api.Board;
import net.hyze.core.spigot.misc.scoreboard.protocol.api.BoardUpdater;

import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskManager {

    private static final ThreadFactory FACTORY = new ThreadFactoryBuilder()
            .setNameFormat("BoardUpdater #%s")
            .setDaemon(true)
            .build();

    private volatile long tick = 0L;
    private Board board;
    private Thread updateThread;
    private Map<BoardUpdater, Long> updaters = Maps.newHashMap();

    public TaskManager(Board board) {
        this.board = board;
    }

    public void startUpdate() {
        updateThread = FACTORY.newThread(() -> {
            while (!Thread.currentThread().isInterrupted() && board != null) {
                try {
                    Thread.sleep(50);
                    if (tick == Long.MAX_VALUE) {
                        tick = 0;
                    }
                    for (Map.Entry<BoardUpdater, Long> entry : updaters.entrySet()) {
                        if (tick != 0 && tick % entry.getValue() != 0) {
                            continue;
                        }
                        entry.getKey().update(board);
                    }
                    tick++;
                } catch (InterruptedException ex) {
                    Logger.getLogger(TaskManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        updateThread.start();
    }

    public void addUpdater(long interval, BoardUpdater updater) {
        updaters.put(updater, interval);
    }

    public void cancel() {
        if (updateThread == null || !updateThread.isAlive()) {
            return;
        }
        updateThread.interrupt();
        updaters.clear();
    }

    public int size() {
        return updaters.size();
    }
}
