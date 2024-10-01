package net.hyze.core.spigot.misc.enchantments.triggers;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDamageTrigger extends EnchantmentTrigger<EntityDamageEvent> {

    @Getter
    private final double rawDamage;

    public EntityDamageTrigger(EntityDamageEvent event, Player player, ItemStack stack, int level) {
        super(event, player, stack, level);
        this.rawDamage = event.getDamage();
    }
}
