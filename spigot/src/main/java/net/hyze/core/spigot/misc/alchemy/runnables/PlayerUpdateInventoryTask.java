package net.hyze.core.spigot.misc.alchemy.runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerUpdateInventoryTask extends BukkitRunnable {

    private Player player;

    public PlayerUpdateInventoryTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        player.updateInventory();
    }
}
