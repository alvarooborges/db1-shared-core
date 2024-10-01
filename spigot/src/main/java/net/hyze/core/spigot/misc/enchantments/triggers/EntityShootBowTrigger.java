package net.hyze.core.spigot.misc.enchantments.triggers;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class EntityShootBowTrigger extends EnchantmentTrigger<EntityShootBowEvent> {

    public EntityShootBowTrigger(EntityShootBowEvent event, Player player, ItemStack stack, int level) {
        super(event, player, stack, level);
    }
}
