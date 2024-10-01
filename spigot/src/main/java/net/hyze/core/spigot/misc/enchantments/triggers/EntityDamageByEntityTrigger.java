package net.hyze.core.spigot.misc.enchantments.triggers;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDamageByEntityTrigger extends EnchantmentTrigger<EntityDamageByEntityEvent> {

    @Getter
    private final double rawDamage;

    public EntityDamageByEntityTrigger(EntityDamageByEntityEvent event, Player player, ItemStack stack, int level) {
        super(event, player, stack, level);
        this.rawDamage = event.getDamage();
    }

}
