package net.hyze.core.spigot.misc.enchantments.data;

import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.PlayerInteractTrigger;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.greenrobot.eventbus.Subscribe;

public class BruteForceCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_brute_force";

    public BruteForceCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Força Bruta";
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
            CustomEnchantmentSlot.PICKAXE
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Picaretas com este encantamento",
            "&7podem quebrar Rochas Matriz",
            "&7instantaneamente."
        };
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Subscribe
    public void on(PlayerInteractTrigger trigger) {
        PlayerInteractEvent event = trigger.getEvent();

        if (event.isCancelled()) {
            return;
        }

        if (!event.hasBlock() || event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block.getType() != Material.BEDROCK) {
            return;
        }

        event.setCancelled(true);

        if (block.getLocation().getBlockY() == 0) {
            return;
        }

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, event.getPlayer()) {
            @Override
            public void setCancelled(boolean cancel) {

                if (cancel) {
                    Throwable throwable = new Throwable();

                    if (throwable.getStackTrace() != null && throwable.getStackTrace().length > 1) {
                        StackTraceElement element = throwable.getStackTrace()[1];

                        String ncpClassName = "fr.neatmonster.nocheatplus.checks.blockbreak.BlockBreakListener";

                        if (ncpClassName.equalsIgnoreCase(element.getClassName())) {
                            return;
                        }
                    }
                }

                super.setCancelled(cancel);
            }
        };

        blockBreakEvent.setCancelled(false);

        Bukkit.getPluginManager().callEvent(blockBreakEvent);

        if (blockBreakEvent.isCancelled()) {
            return;
        }

        block.setType(Material.AIR);

        Location location = block.getLocation();

        location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.BEDROCK.getId());
        location.getWorld().dropItemNaturally(location, new ItemStack(Material.BEDROCK));

        int damage = 40 - (10 * trigger.getLevel());

        int level = trigger.getItem().getEnchantmentLevel(Enchantment.DURABILITY);

        if (level > 0) {
            // -10% de dano para cada ponto de durabilidade
            damage = damage - (damage / 100 * (10 * level));
        }

        if (damage <= 0) {
            return;
        }

        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(trigger.getItem());
        nmsCopy.damage(damage, ((CraftPlayer) event.getPlayer()).getHandle());

        if (nmsCopy.count == 0) {;
            InventoryUtils.subtractOneOnHand(event.getPlayer());
        } else {
            trigger.getItem().setDurability((short) nmsCopy.h());
        }
    }
}
