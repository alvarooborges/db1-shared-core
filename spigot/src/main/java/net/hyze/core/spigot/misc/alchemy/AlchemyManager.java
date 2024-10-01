package net.hyze.core.spigot.misc.alchemy;

import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.alchemy.runnables.AlchemyBrewCheckTask;
import net.hyze.core.spigot.misc.alchemy.runnables.AlchemyBrewTask;
import net.hyze.core.spigot.misc.alchemy.runnables.PlayerUpdateInventoryTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlchemyManager {

    public static Map<Location, AlchemyBrewTask> brewingStandMap = new HashMap<Location, AlchemyBrewTask>();

    public static final int INGREDIENT_SLOT = 3;

    public static boolean isValidBrew(Player player, ItemStack[] contents) {
        if (!isValidIngredient(player, contents[INGREDIENT_SLOT])) {
            return false;
        }

        for (int i = 0; i < 3; i++) {
            if (contents[i] == null || contents[i].getType() != Material.POTION) {
                continue;
            }

            if (getChildPotion(Recipes.getPotion(contents[i].getDurability()), contents[INGREDIENT_SLOT]) != null) {
                return true;
            }
        }

        return false;
    }

    private static AlchemyPotion getChildPotion(AlchemyPotion potion, ItemStack ingredient) {
        if (potion != null && potion.getChildDataValue(ingredient) != -1) {
            return Recipes.getPotion(potion.getChildDataValue(ingredient));
        }

        return null;
    }

    public static boolean isValidIngredient(Player player, ItemStack item) {
        if (isEmpty(item)) {
            return false;
        }

        for (ItemStack ingredient : getValidIngredients(player)) {
            if (item.isSimilar(ingredient)) {
                return true;
            }
        }

        return false;
    }

    private static List<ItemStack> getValidIngredients(Player player) {
        return Recipes.getIngredients();
    }

    public static void scheduleCheck(Player player, BrewingStand brewingStand) {
        new AlchemyBrewCheckTask(player, brewingStand).runTask(CoreSpigotPlugin.getInstance());
    }

    public static void scheduleUpdate(Inventory inventory) {
        for (HumanEntity humanEntity : inventory.getViewers()) {
            if (humanEntity instanceof Player) {
                new PlayerUpdateInventoryTask((Player) humanEntity).runTask(CoreSpigotPlugin.getInstance());
            }
        }
    }

    public static boolean transferItems(InventoryView view, int fromSlot, ClickType click) {
        boolean success = false;

        if (click.isLeftClick()) {
            success = transferItems(view, fromSlot);
        } else if (click.isRightClick()) {
            success = transferOneItem(view, fromSlot);
        }

        return success;
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getAmount() == 0;
    }

    private static boolean transferOneItem(InventoryView view, int fromSlot) {
        ItemStack from = view.getItem(fromSlot).clone();
        ItemStack to = view.getItem(INGREDIENT_SLOT).clone();

        if (isEmpty(from)) {
            return false;
        }

        boolean emptyTo = isEmpty(to);
        int fromAmount = from.getAmount();

        if (!emptyTo && fromAmount >= from.getType().getMaxStackSize()) {
            return false;
        } else if (emptyTo || from.isSimilar(to)) {
            if (emptyTo) {
                to = from.clone();
                to.setAmount(1);
            } else {
                to.setAmount(to.getAmount() + 1);
            }

            from.setAmount(fromAmount - 1);
            view.setItem(INGREDIENT_SLOT, to);
            view.setItem(fromSlot, from);

            return true;
        }

        return false;
    }

    /**
     * Transfer items between two ItemStacks, returning the leftover status
     */
    private static boolean transferItems(InventoryView view, int fromSlot) {
        ItemStack from = view.getItem(fromSlot).clone();
        ItemStack to = view.getItem(INGREDIENT_SLOT).clone();

        if (isEmpty(from)) {
            return false;
        } else if (isEmpty(to)) {
            view.setItem(INGREDIENT_SLOT, from);
            view.setItem(fromSlot, null);

            return true;
        } else if (from.isSimilar(to)) {
            int fromAmount = from.getAmount();
            int toAmount = to.getAmount();
            int maxSize = to.getType().getMaxStackSize();

            if (fromAmount + toAmount > maxSize) {
                int left = fromAmount + toAmount - maxSize;

                to.setAmount(maxSize);
                view.setItem(INGREDIENT_SLOT, to);

                from.setAmount(left);
                view.setItem(fromSlot, from);

                return true;
            }

            to.setAmount(fromAmount + toAmount);
            view.setItem(fromSlot, null);
            view.setItem(INGREDIENT_SLOT, to);

            return true;
        }

        return false;
    }

    public static void finishBrewing(BlockState brewingStand, Player player, boolean forced) {
        if (!(brewingStand instanceof BrewingStand)) {
            return;
        }

        BrewerInventory inventory = ((BrewingStand) brewingStand).getInventory();
        ItemStack ingredient = inventory.getIngredient() == null ? null : inventory.getIngredient().clone();


        if (!forced) {
            scheduleUpdate(inventory);
        }
    }
}
