package net.hyze.core.spigot.misc.blockdrops;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockDropsManager {

    private static final Multimap<Material, BlockDropsExecutor> HANDLERS = ArrayListMultimap.create();

    public static Collection<ItemStack> getDrops(@NonNull Block block) {
        return getDrops(block, null, null);
    }

    public static boolean hasHandler(Material type) {
        return HANDLERS.containsKey(type);
    }

    public static Collection<ItemStack> getDrops(@NonNull Block block, Player player, ItemStack tool) {
        if (HANDLERS.containsKey(block.getType())) {
            Collection<ItemStack> stacks = Lists.newArrayList();

            HANDLERS.get(block.getType()).forEach(executor -> {
                Collection<ItemStack> drops = executor.getDrops(block, player, tool);

                if (drops != null && !drops.isEmpty()) {
                    stacks.addAll(drops);
                }
            });

            return stacks;
        } else if (tool != null) {
            return block.getDrops(tool);
        } else {
            return block.getDrops();
        }
    }

    public static void registerHandler(Material type, BlockDropsExecutor executor) {
        HANDLERS.put(type, executor);
    }
}
