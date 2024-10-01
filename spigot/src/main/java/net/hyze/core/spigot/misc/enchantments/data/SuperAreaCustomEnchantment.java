package net.hyze.core.spigot.misc.enchantments.data;

import com.google.common.collect.Maps;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

import java.util.Map;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.BlockBreakTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.PlayerInteractTrigger;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.greenrobot.eventbus.Subscribe;

public class SuperAreaCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_super_area";

    public SuperAreaCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Super Área";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Ao quebrar um bloco, todos os blocos",
                "&7que estiverem próximos também serão",
                "&7destruídos."
        };
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
                CustomEnchantmentSlot.PICKAXE,
                CustomEnchantmentSlot.SPADE
        };
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    public Map<Player, BlockFace> lastClickedBlockFace = Maps.newHashMap();

    @Subscribe
    public void on(PlayerInteractTrigger trigger) {
        if (trigger.getEvent().getAction() == Action.LEFT_CLICK_BLOCK) {
            lastClickedBlockFace.put(trigger.getPlayer(), trigger.getEvent().getBlockFace());
        }
    }

    @Subscribe
    public void on(BlockBreakTrigger trigger) {
        if (trigger.getEvent() instanceof SuperAreaBlockBreakEvent) {
            return;
        }

        Player player = trigger.getPlayer();

        NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK);

        BlockFace face = lastClickedBlockFace.getOrDefault(player, BlockFace.SOUTH);
        ItemStack handItem = player.getItemInHand();

        getCuboid(trigger.getEvent().getBlock(), face, trigger.getLevel())
                .forEach(currentBlock -> {
                    Material type = currentBlock.getType();

                    if (!trigger.getEvent().getBlock().equals(currentBlock) && type == trigger.getEvent().getBlock().getType()) {
                        BlockBreakEvent breakEvent = new SuperAreaBlockBreakEvent(currentBlock, player);
                        Bukkit.getPluginManager().callEvent(breakEvent);

                        if (!breakEvent.isCancelled()) {
                            currentBlock.breakNaturally();

                            int damagePercent = (100 / (trigger.getLevel() + 1));

                            if ((CoreConstants.RANDOM.nextInt(100) + 1) <= damagePercent) {
                                handItem.setDurability((short) (handItem.getDurability() + 1));
                            }
                        }
                    }
                });

        NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK);
    }

    private WorldCuboid getCuboid(Block block, BlockFace face, int level) {

        Location blockLocation = block.getLocation();

        Location minLocation = blockLocation.clone();
        Location maxLocation = blockLocation.clone();

        level = Math.max(level, 1);
        level = Math.min(5, level);
        level = level - 1;

        switch (face) {
            case NORTH:
                minLocation.add(1, -1, level);
                maxLocation.add(-1, 1, 0);
                break;
            case SOUTH:
                minLocation.add(-1, -1, 0);
                maxLocation.add(1, 1, -level);
                break;
            case EAST:
                minLocation.add(0, -1, 1);
                maxLocation.add(-level, 1, -1);
                break;
            case WEST:
                minLocation.add(level, -1, -1);
                maxLocation.add(0, 1, 1);
                break;
            case UP:
                minLocation.add(1, -level, 1);
                maxLocation.add(-1, 0, -1);
                break;
            case DOWN:
                minLocation.add(-1, level, -1);
                maxLocation.add(1, 0, 1);
                break;
            default:
                break;
        }

        return new WorldCuboid(minLocation, maxLocation, blockLocation.getWorld());
    }

    public static class SuperAreaBlockBreakEvent extends BlockBreakEvent {

        public SuperAreaBlockBreakEvent(Block theBlock, Player player) {
            super(theBlock, player);
        }

    }
}
