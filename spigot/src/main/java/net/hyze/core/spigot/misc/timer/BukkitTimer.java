package net.hyze.core.spigot.misc.timer;

import net.hyze.core.shared.misc.timer.AbstractTimer;
import net.hyze.core.spigot.CoreSpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class BukkitTimer extends AbstractTimer  {

    private BukkitTask task;

    private final boolean async;

    public BukkitTimer(long tickDelay, boolean async) {
        this(tickDelay, 0, async);
    }

    public BukkitTimer(long tickDelay, long initDelay, boolean async) {
        super(tickDelay, initDelay);

        this.async = async;
    }

    public BukkitTimer(long tickDelay) {
        this(tickDelay, 0);
    }

    public BukkitTimer(long tickDelay, long initDelay) {
        this(tickDelay, initDelay, false);
    }

    @Override
    public void start() {
        this.task = async ? Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSpigotPlugin.getInstance(), this::run, this.initDelay, this.tickDelay) :
                Bukkit.getScheduler().runTaskTimer(CoreSpigotPlugin.getInstance(), this::run, this.initDelay, this.tickDelay);
    }

    @Override
    public void cancel() {
        if(this.task != null) {
            this.task.cancel();
        }
    }
}
