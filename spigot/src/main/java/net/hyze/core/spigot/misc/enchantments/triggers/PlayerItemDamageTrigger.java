package net.hyze.core.spigot.misc.enchantments.triggers;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemDamageTrigger extends EnchantmentTrigger<PlayerItemDamageEvent> {

    public PlayerItemDamageTrigger(PlayerItemDamageEvent event, Player player, ItemStack stack, int level) {
        super(event, player, stack, level);
    }

}
