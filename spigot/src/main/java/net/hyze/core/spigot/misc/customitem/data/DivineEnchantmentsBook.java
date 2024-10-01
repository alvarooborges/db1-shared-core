package net.hyze.core.spigot.misc.customitem.data;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.shared.misc.utils.RandomList;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentRegistry;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.ItemStackUtils;
import net.hyze.core.spigot.misc.utils.Title;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.greenrobot.eventbus.Subscribe;

public class DivineEnchantmentsBook extends CustomItem {

    public static String KEY = "custom_item_divine_enchantments_book";

    @Getter
    private final ItemBuilder itemBuilder;

    private final List<Player> ACTIVES = Lists.newArrayList();
    private final Map<Pair<Enchantment, Integer>, Double> enchantments;
    private final Map<Pair<CustomEnchantment, Integer>, Double> customEnchantments;

    public DivineEnchantmentsBook(Map<Pair<Enchantment, Integer>, Double> enchantments,
            Map<Pair<CustomEnchantment, Integer>, Double> customEnchantments) {
        super(KEY);

        this.enchantments = enchantments;
        this.customEnchantments = customEnchantments;

        RandomList<Pair<?, Integer>> randomList = new RandomList();

        this.enchantments.forEach((pair, weight) -> {
            randomList.add(pair, weight);
        });

        this.customEnchantments.forEach((pair, weight) -> {
            randomList.add(pair, weight);
        });

        this.itemBuilder = ItemBuilder.of(Material.BOOK)
                .name("&bLivro de Encantamentos Divinos")
                .glowing(true)
                .lore(
                        "&7Ao abrir este livro, você receberá um",
                        "&7dos Livros de Encantamento que",
                        "&7estão listados abaixo!",
                        "",
                        "&8\u25AA &fLista de Possíveis Encantamentos:"
                );

        NBTTagList nbtEnchantments = new NBTTagList();
        NBTTagList nbtCustomEnchantments = new NBTTagList();

        randomList.stream()
                .sorted((r1, r2) -> Double.compare(r2.getChance(), r1.getChance()))
                .forEach(random -> {
                    String title = null;
                    Pair<?, Integer> pair = random.getObject();

                    int level = pair.getRight();

                    if (pair.getLeft() instanceof Enchantment) {
                        Enchantment enchantment = (Enchantment) pair.getLeft();

                        this.itemBuilder.lore(String.format(
                                " &8\u25AA &7%s %s",
                                CoreSpigotConstants.TRANSLATE_ITEM.get(enchantment),
                                NumberUtils.toRoman(level)
                        ));

                        NBTTagCompound compound = new NBTTagCompound();

                        compound.setString("enchantment", enchantment.getName());
                        compound.setInt("level", level);
                        compound.setDouble("weight", random.getWeight());

                        nbtEnchantments.add(compound);

                    } else if (pair.getLeft() instanceof CustomEnchantment) {
                        CustomEnchantment enchantment = (CustomEnchantment) pair.getLeft();

                        String color = "&b";

                        if (random.getChance() < 10) {
                            color = "&6";
                        }

                        this.itemBuilder.lore(String.format(
                                " &8\u25AA %s%s",
                                color,
                                enchantment.getDisplayName(level)
                        ));

                        NBTTagCompound compound = new NBTTagCompound();

                        compound.setString("enchantment", enchantment.getKey());
                        compound.setInt("level", level);
                        compound.setDouble("weight", random.getWeight());

                        nbtCustomEnchantments.add(compound);
                    }
                });

        this.itemBuilder.nbt("divine_enchantments", nbtEnchantments);
        this.itemBuilder.nbt("divine_custom_enchantments", nbtCustomEnchantments);
    }

    @Override
    public String getDisplayName() {
        return "Livro de Encantamentos Divinos";
    }

    private String getRewardName(Pair<?, Integer> pair) {
        if (pair.getLeft() instanceof Enchantment) {

            Enchantment enchantment = (Enchantment) pair.getLeft();
            return CoreSpigotConstants.TRANSLATE_ITEM.get(enchantment) + " " + NumberUtils.toRoman(pair.getRight());

        } else if (pair.getLeft() instanceof CustomEnchantment) {

            CustomEnchantment enchantment = (CustomEnchantment) pair.getLeft();
            return enchantment.getDisplayName(pair.getRight());
        }

        return null;
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        RandomList<Pair<?, Integer>> randomList = new RandomList();

        ItemBuilder builder = ItemBuilder.of(event.getItem());

        NBTTagList nbtEnchantments = builder.nbtList("divine_enchantments");
        for (int i = 0; i < nbtEnchantments.size(); i++) {
            NBTTagCompound compound = nbtEnchantments.get(i);

            Enchantment enchantment = Enchantment.getByName(compound.getString("enchantment"));

            if (enchantment != null) {
                randomList.add(new Pair(enchantment, compound.getInt("level")), compound.getDouble("weight"));
            }
        }

        NBTTagList nbtCustomEnchantments = builder.nbtList("divine_custom_enchantments");
        for (int i = 0; i < nbtCustomEnchantments.size(); i++) {
            NBTTagCompound compound = nbtCustomEnchantments.get(i);

            CustomEnchantment customEnchantment = CustomEnchantmentRegistry.get(compound.getString("enchantment"));

            if (customEnchantment != null) {
                randomList.add(new Pair(customEnchantment, compound.getInt("level")), compound.getDouble("weight"));
            }
        }

        Player player = event.getPlayer();

        if (randomList.size() < 5) {
            Message.ERROR.send(player, " OPS! Não achamos os encantamentos :(");
            return;
        }

        if (ACTIVES.contains(player)) {
            Message.ERROR.send(player, "Você só pode abrir um livro por vez.");
            return;
        }

        ItemStack itemChecker = ItemBuilder.of(Material.BARRIER).name(UUID.randomUUID().toString()).make();

        if (!InventoryUtils.fits(player.getInventory(), itemChecker)) {
            Message.ERROR.send(player, "Seu inventário está cheio.");
            return;
        }

        Pair<?, Integer> pair = randomList.raffle();

        ItemStack reward;

        if (pair.getLeft() instanceof Enchantment) {
            Enchantment enchantment = (Enchantment) pair.getLeft();

            reward = new ItemStack(Material.ENCHANTED_BOOK);

            ItemStackUtils.addBookEnchantment(reward, enchantment, pair.getRight());

        } else if (pair.getLeft() instanceof CustomEnchantment) {
            CustomEnchantment enchantment = (CustomEnchantment) pair.getLeft();

            reward = enchantment.getBook(pair.getRight(), 1);
        } else {
            Message.ERROR.send(player, "Algo de errado aconteceu! Entre em contato com algum staff.");
            return;
        }

        String rewardName = getRewardName(pair);

        ItemStack hand = player.getItemInHand();

        int delay = 1;

        List<String> names = randomList.stream().map(random -> {
            return getRewardName(random.getObject());
        }).collect(Collectors.toList());

        Collections.shuffle(names);

        ACTIVES.add(player);

        new BukkitRunnable() {
            float run = 0;
            int speed = 2;
            int nameIndex = 0;

            @Override
            public void cancel() {
                super.cancel();
                ACTIVES.remove(player);
            }

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (run / 20 == 5) {
                    boolean success = false;

                    if (InventoryUtils.fits(player.getInventory(), itemChecker)) {
                        boolean subtract = InventoryUtils.subtractOne(player, hand);

                        if (subtract) {
                            player.getInventory().addItem(reward);

                            try {
                                new Title()
                                        .fadeIn(0)
                                        .subTitle("&b" + rewardName)
                                        .stay(20 * 3)
                                        .fadeOut(20 * 3)
                                        .send(player);
                            } catch (Exception e) {

                            }

                            Message.SUCCESS.send(player, "Você ganhou 1x Livro " + rewardName + ".");
                            success = true;

                            player.getWorld().playSound(player.getLocation(), Sound.FIREWORK_BLAST, 5, 0);

                            Bukkit.getScheduler().runTaskAsynchronously(CoreSpigotPlugin.getInstance(), () -> {
                                for (int i = 0; i < 50; i++) {
                                    player.getWorld().playEffect(player.getLocation().clone().add(0, 1, 0), Effect.FIREWORKS_SPARK, 1, 5);
                                }
                            });
                        }
                    }

                    if (!success) {
                        Message.ERROR.send(player, "Algo de errado aconteceu!");
                    }

                    cancel();
                    return;
                }

                if (run % speed == 0) {
                    if (nameIndex >= names.size()) {
                        nameIndex = 0;
                    }

                    try {
                        new Title()
                                .fadeIn(0)
                                .stay(20 * 1)
                                .fadeOut(0)
                                .subTitle("&b" + names.get(nameIndex))
                                .send(player);
                    } catch (Exception e) {
                    }

                    nameIndex++;

                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                    speed += 2;
                }

                run += delay;
            }
        }.runTaskTimer(CoreSpigotPlugin.getInstance(), 0, delay);
    }
}
