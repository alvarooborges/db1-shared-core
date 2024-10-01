package net.hyze.core.spigot.misc.blockdrops;

import java.util.Collection;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface BlockDropsExecutor {

    Collection<ItemStack> getDrops(Block block, Player player, ItemStack tool);
}
