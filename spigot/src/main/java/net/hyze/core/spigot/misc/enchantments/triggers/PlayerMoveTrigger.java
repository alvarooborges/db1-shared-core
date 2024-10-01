package net.hyze.core.spigot.misc.enchantments.triggers;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerMoveTrigger extends EnchantmentTrigger<PlayerMoveEvent> {

    public PlayerMoveTrigger(PlayerMoveEvent event, Player player, ItemStack stack, int level) {
        super(event, player, stack, level);
    }
}
