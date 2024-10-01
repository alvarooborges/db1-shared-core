package net.hyze.core.spigot.misc.mining;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.RandomUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentRegistry;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentUtil;
import net.hyze.core.spigot.misc.enchantments.data.SuperAreaCustomEnchantment;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.mining.events.CustomMineEvent;
import net.hyze.core.spigot.misc.report.AutoReport;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomMining {

    public static McMMODrops mcMMODrops;
    public static McMMOQuartz mcMMOQuartz;

    protected static final Multimap<MaterialData, Drop> customMining = ArrayListMultimap.create();

    public static void insertMiningDrop(MaterialData onBreak, Drop drop) {
        customMining.put(onBreak, drop);
    }

    public static Collection<Drop> getMiningDrops(MaterialData onBreak) {
        return customMining.get(onBreak);
    }

    public static Collection<Drop> getMiningDrops(Material onBreak, byte damage) {
        return getMiningDrops(new MaterialData(onBreak, damage));
    }

    public static void execute(Player player, User user, BlockBreakEvent event) {
        execute(player, user, event, true);
    }

    public static void execute(Player player, User user, BlockBreakEvent event, boolean addItemsOnInventory) {
        execute(player, user, event, true, Sets.newHashSet());
    }

    public static void execute(Player player, User user, BlockBreakEvent event, boolean addItemsOnInventory,
                               Set<MaterialData> dropsBlackList) {

        Block block = event.getBlock();
        Material material = block.getType();

        ItemStack inHand = player.getItemInHand() != null ? player.getItemInHand() : new ItemStack(Material.AIR);
        MaterialData data = new MaterialData(material, block.getData());

        if (event.isCancelled() || !player.getGameMode().equals(GameMode.SURVIVAL) || !customMining.containsKey(data)) {

            return;
        }

        Group group = user.getHighestGroup();

        int digSpeedLevel = EnchantmentManager.getDigSpeedEnchantmentLevel(((CraftPlayer) player).getHandle());

        boolean isSuperAreaBreak = event instanceof SuperAreaCustomEnchantment.SuperAreaBlockBreakEvent;

        if (!group.isSameOrHigher(Group.MODERATOR)
                && block.getType().equals(Material.STONE)
                && !player.hasPotionEffect(PotionEffectType.FAST_DIGGING)
                && !isSuperAreaBreak
                && digSpeedLevel <= 5) {
            AutoReport.breakBlock(player);
        }

        Set<Drop> drops = getMiningDrops(data).stream()
                .filter(md -> md.isValidDrop(block.getLocation(), inHand))
                .collect(Collectors.toSet());

//        if (!drops.isEmpty()) {
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
        event.getBlock().getState().update();
        Bukkit.getScheduler().runTask(CoreSpigotPlugin.getInstance(), () -> event.getBlock().getState().update());
//        }

        drops = drops.stream().filter(md -> user.hasGroup(md.getGroup())).collect(Collectors.toSet());

        if (mcMMOQuartz != null) {

            MiningDrop drop = mcMMOQuartz.execute(user);
            if (drop != null) {

                drops.add(drop);
            }
        }

        if (AutoReport.get(player) > AutoReport.getValue()) {
            return;
        }

        if (drops.isEmpty()) {
            return;
        }

        Location location = event.getBlock().getLocation().clone();

        wearOutTool(player);

        if (event.getClass().equals(BlockBreakEvent.class)) {

            handleReward(player, user, addItemsOnInventory, location, drops, dropsBlackList);

            CustomEnchantment superArea = CustomEnchantmentRegistry.get(SuperAreaCustomEnchantment.KEY);

            if (superArea != null) {
                int superAreaLevel = CustomEnchantmentUtil.getEnchantmentLevel(inHand, superArea);

                for (int i = 0; i < superAreaLevel; i++) {
                    handleReward(player, user, addItemsOnInventory, location, drops, dropsBlackList);
                }
            }
        }
    }

    private static void handleReward(Player player, User user, boolean addItemsOnInventory, Location location, Set<Drop> drops, Set<MaterialData> dropBlackList) {
        Drop drop = getRandomDrop(drops.stream().collect(Collectors.toList()));

        if (drop == null) {
            return;
        }

        ItemStack itemReward = drop.getItemStack().clone();
        itemReward.setAmount(calculateDrop(player, drop));

        Bukkit.getPluginManager().callEvent(new CustomMineEvent(player, itemReward));

        if (!dropBlackList.contains(itemReward.getData())) {

            if (addItemsOnInventory && isOre(getOre(itemReward.getType()))) {

                if (InventoryUtils.fits(player.getInventory(), itemReward)) {

                    player.getInventory().addItem(itemReward);
                } else {
                    Message.ERROR.send(player, "Seu inventário está cheio.");
                }

            } else {
                location.getWorld().dropItemNaturally(LocationUtils.center(location), itemReward);
            }
        }

        if (drop instanceof MiningDrop) {

            MiningDrop miningDrop = (MiningDrop) drop;
            int xp = miningDrop.getXP();

            if (xp > 0) {
                player.giveExp(xp);
            }
        }

        /*--- McMMO ---*/
        if (mcMMODrops != null) {
            mcMMODrops.execute(user, drop, location, itemReward);
        }
    }

    public static void wearOutTool(Player player) {
        ItemStack hand = player.getItemInHand();
        if (hand == null || !MiningTool.isTool(hand.getType())
                || (hand.hasItemMeta() && hand.getItemMeta().spigot().isUnbreakable())) {
            return;
        }

        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(hand.clone());

        nmsCopy.damage(1, ((CraftPlayer) player).getHandle());

        if (nmsCopy.count == 0) {
            InventoryUtils.subtractOneOnHand(player);
        } else {
            hand.setDurability((short) nmsCopy.h());
        }

//        boolean hasDurability = hand.containsEnchantment(Enchantment.DURABILITY);
//        int durability = hasDurability ? hand.getEnchantmentLevel(Enchantment.DURABILITY) : 0;
//
//        int reduce = (int) (100 / (durability + 1D));
//        int random = CoreConstants.RANDOM.nextInt(100) + 1;
//
//        if (random <= reduce) {
//            short dur = hand.getDurability();
//            if (dur + 1 > hand.getType().getMaxDurability()) {
//                player.setItemInHand(new ItemStack(Material.AIR));
//                player.getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK, 2, 2);
//                // player.getWorld().playEffect(player.getLocation(), Effect.ITEM_BREAK,
//                // hand.getTypeId());
//                return;
//            }
//            hand.setDurability((short) (dur + 1));
//        }
    }

    private static int calculateDrop(Player player, Drop drop) {
        /*
         * level I gives a 33% chance to multiply drops by 2 (averaging 33% increase),
         * level II gives a chance to multiply drops by 2 or 3 (25% chance each,
         * averaging 75% increase), and level III gives a chance to multiply drops by 2,
         * 3, or 4 (20% chance each, averaging 120% increase). 1 drop has a weight of 2,
         * and each number of extra drops has a weight of 1.
         */
        ItemStack hand = player.getItemInHand();
        int amount = drop.getAmount();
        boolean hasFortune = hand != null && hand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
        int fortune = hasFortune ? hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0;

        if (isOre(drop.getDrop())) {
            return amount;
        }

        if (fortune == 1) {
            if (RandomUtils.randomInt(0, 3) == 0) {
                amount *= 2;
            }
        } else if (fortune == 2) {
            int rand = RandomUtils.randomInt(0, 4);
            if (rand == 0) {
                amount *= 2;
            } else if (rand == 1) {
                amount *= 3;
            }
        } else if (fortune >= 3) {
            int rand = RandomUtils.randomInt(0, 5);
            if (rand == 0) {
                amount *= 2;
            } else if (rand == 1) {
                amount *= 3;
            } else if (rand == 2) {
                amount *= 4;
            }
        }
        return amount;
    }

    public static Material getOre(Material drop) {
        switch (drop) {
            case COAL: {
                return Material.COAL_ORE;
            }
            case IRON_INGOT: {
                return Material.IRON_ORE;
            }
            case GOLD_INGOT: {
                return Material.GOLD_ORE;
            }
            case DIAMOND: {
                return Material.DIAMOND_ORE;
            }
            case EMERALD: {
                return Material.EMERALD_ORE;
            }
            case REDSTONE: {
                return Material.REDSTONE_ORE;
            }
            case INK_SACK: {
                return Material.LAPIS_ORE;
            }
            case COBBLESTONE: {
                return Material.STONE;
            }
        }
        return drop;
    }

    private static Drop getRandomDrop(List<Drop> drops) {

        if (drops.isEmpty()) {
            return null;
        }
        if (drops.size() == 1) {
            return drops.get(0);
        }

        int prob = 0, min = 1, max = getMaxRandom(drops);
        int random = RandomUtils.randomInt(min, max);

        for (Drop md : drops) {
            prob += md.getDropWeight();
            if (random <= prob) {
                // System.out.println("random[1|" + max + "]|" + random + " - drop: " +
                // md.getDrop().name());
                return md;
            }
        }
        return null;
    }

    private static int getMaxRandom(List<Drop> drops) {
        int prob = 0;
        for (Drop md : drops) {
            prob += md.getDropWeight();
        }
        return prob;
    }

    public static boolean isOre(Material drop) {
        return drop.name().endsWith("_ORE");
    }

    public static interface McMMODrops {

        public abstract void execute(User user, Drop drop, Location location, ItemStack isDrop);

    }

    public static interface McMMOQuartz {

        public abstract MiningDrop execute(User user);

    }

}
