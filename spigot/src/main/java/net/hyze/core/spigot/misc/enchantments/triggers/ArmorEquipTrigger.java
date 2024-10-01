package net.hyze.core.spigot.misc.enchantments.triggers;

import net.hyze.core.spigot.events.ArmorEquipEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorEquipTrigger extends EnchantmentTrigger<ArmorEquipEvent> {

    public ArmorEquipTrigger(ArmorEquipEvent event, Player player, ItemStack stack, int level) {
        super(event, player, stack, level);
    }
}
