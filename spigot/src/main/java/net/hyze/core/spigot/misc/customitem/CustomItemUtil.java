package net.hyze.core.spigot.misc.customitem;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class CustomItemUtil {

    public static void tagItem(CustomItem customItem, ItemStack stack) {
        ItemBuilder builder = ItemBuilder.of(stack, true);
        builder.nbt(CustomItemRegistry.NBT_KEY, customItem.getKey());
    }
}
