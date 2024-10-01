package net.hyze.core.spigot.misc.alchemy.runnables;

import net.hyze.core.spigot.misc.alchemy.AlchemyManager;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class AlchemyBrewCheckTask extends BukkitRunnable {

    private Player player;
    private BrewingStand brewingStand;
    private ItemStack[] oldInventory;

    public AlchemyBrewCheckTask(Player player, BrewingStand brewingStand) {
        this.player = player;
        this.brewingStand = brewingStand;
        this.oldInventory = Arrays.copyOfRange(brewingStand.getInventory().getContents(), 0, 4);
    }

    @Override
    public void run() {
        Location location = brewingStand.getLocation();
        ItemStack[] newInventory = Arrays.copyOfRange(brewingStand.getInventory().getContents(), 0, 4);
        boolean validBrew = AlchemyManager.isValidBrew(player, newInventory);

        if (AlchemyManager.brewingStandMap.containsKey(location)) {
            if (oldInventory[AlchemyManager.INGREDIENT_SLOT] == null
                    || newInventory[AlchemyManager.INGREDIENT_SLOT] == null
                    || !oldInventory[AlchemyManager.INGREDIENT_SLOT].isSimilar(newInventory[AlchemyManager.INGREDIENT_SLOT])
                    || !validBrew) {
                AlchemyManager.brewingStandMap.get(location).cancelBrew();
            }
        } else if (validBrew) {
            AlchemyManager.brewingStandMap.put(location, new AlchemyBrewTask(brewingStand, player));
        }
    }
}
