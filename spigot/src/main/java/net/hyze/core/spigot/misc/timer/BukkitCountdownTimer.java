package net.hyze.core.spigot.misc.timer;

import net.hyze.core.shared.misc.timer.AbstractTimer;
import net.hyze.core.spigot.CoreSpigotPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class BukkitCountdownTimer extends BukkitTimer  {

    private final int amount;

    private int index;

    public BukkitCountdownTimer(int amount, int tickDelay, boolean async) {
        super(tickDelay, async);

        this.amount = amount;
    }

    public BukkitCountdownTimer(int amount, int tickDelay) {
        super(tickDelay);

        this.amount = amount;
    }

    @Override
    public void start() {
        super.start();

        this.index = this.amount;
    }

    @Override
    public void run() {
        this.tick(index);

        if(index == 0) {
            this.cancel();
            return;
        }

        this.index--;
    }

    public abstract void tick(int index);
}
