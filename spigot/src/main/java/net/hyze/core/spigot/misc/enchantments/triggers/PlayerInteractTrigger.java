package net.hyze.core.spigot.misc.enchantments.triggers;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractTrigger extends EnchantmentTrigger<PlayerInteractEvent> {

    public PlayerInteractTrigger(PlayerInteractEvent event, Player player, ItemStack stack, int level) {
        super(event, player, stack, level);
    }

}
