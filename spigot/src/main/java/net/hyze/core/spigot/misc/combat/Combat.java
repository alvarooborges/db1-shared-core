package net.hyze.core.spigot.misc.combat;

import java.util.concurrent.TimeUnit;
import org.bukkit.scheduler.BukkitTask;

public class Combat {

    private final BukkitTask task;
    private final long start;
    private long end;

    public Combat(BukkitTask task) {
        this.task = task;
        this.start = System.currentTimeMillis();
        this.end = start + TimeUnit.SECONDS.toMillis(CombatManager.getCombatTime());
    }

    public void cancelTask() {
        task.cancel();
        end = start;
    }

    public int getRemainingSeconds() {
        return (int) TimeUnit.MILLISECONDS.toSeconds(end - System.currentTimeMillis());
    }

    public boolean hasEnded() {
        return end < System.currentTimeMillis();
    }
}
