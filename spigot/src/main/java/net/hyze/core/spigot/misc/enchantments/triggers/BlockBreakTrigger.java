package net.hyze.core.spigot.misc.enchantments.triggers;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakTrigger extends EnchantmentTrigger<BlockBreakEvent> {

    public BlockBreakTrigger(BlockBreakEvent event, Player player, ItemStack stack, int level) {
        super(event, player, stack, level);
    }

}
