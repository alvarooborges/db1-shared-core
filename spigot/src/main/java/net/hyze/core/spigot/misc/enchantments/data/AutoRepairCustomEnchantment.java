package net.hyze.core.spigot.misc.enchantments.data;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.misc.cooldowns.Cooldowns;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.PlayerItemDamageTrigger;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.TranslateItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.greenrobot.eventbus.Subscribe;

public class AutoRepairCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_auto_repair";

    public AutoRepairCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Auto Reparação";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Itens com este encantamento são",
            "&7reparados automaticamente ao",
            "&7gastar sua durabilidade.",
            "",
            "&7É necessário possuir os materiais",
            "&7para reparar o item no seu inventário."
        };
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
            CustomEnchantmentSlot.TOOLS,
            CustomEnchantmentSlot.WEAPON,
            CustomEnchantmentSlot.BOW
        };
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Subscribe
    public void on(PlayerItemDamageTrigger trigger) {
        ItemStack stack = trigger.getItem();

        Player player = trigger.getPlayer();

        NORMAL_REPAIR:
        {
            if (hasDurability(stack, 75)) {
                break NORMAL_REPAIR;
            }

            Optional<Input> optional = Input.getByItem(stack);

            if (!optional.isPresent()) {
                break NORMAL_REPAIR;
            }

            Input input = optional.get();

            float currentDamage = (float) stack.getDurability();
            float maxDur = (float) stack.getType().getMaxDurability();

            boolean subtract = InventoryUtils.subtractOne(player, new ItemStack(input.material));

            if (subtract) {
                float newDamage = Math.max(0, currentDamage - ((maxDur / 100) * 25));
                player.getItemInHand().setDurability((short) newDamage);
                Message.SUCCESS.send(player, "Seu item foi reparado automaticamente.");
            } else {
                String cooldownKey = "auto_repair_alert_" + trigger.getPlayer().getName();

                if (!Cooldowns.hasEnded(cooldownKey)) {
                    return;
                }

                Cooldowns.start(cooldownKey, 5, TimeUnit.SECONDS);

                Message.ERROR.send(player, String.format(
                        "Você não possui %s para reparar seu item automaticamente.",
                        CoreSpigotConstants.TRANSLATE_ITEM.get(new ItemStack(input.material))
                ));
            }

            return;
        }
    }

    private boolean hasDurability(ItemStack stack, int percentage) {
        if (stack == null || stack.getType() == Material.AIR) {
            return false;
        }

        float currentDur = (float) stack.getDurability();
        float maxDur = (float) stack.getType().getMaxDurability();

        return ((maxDur - currentDur) / maxDur) * 100f >= percentage;
    }

    @RequiredArgsConstructor
    private enum Input {
        DIAMOND(Material.DIAMOND, new Material[]{
            Material.DIAMOND_AXE,
            Material.DIAMOND_PICKAXE,
            Material.DIAMOND_SPADE,
            Material.DIAMOND_SWORD,
            Material.DIAMOND_HOE
        }),
        GOLD(Material.GOLD_INGOT, new Material[]{
            Material.GOLD_AXE,
            Material.GOLD_PICKAXE,
            Material.GOLD_SPADE,
            Material.GOLD_SWORD,
            Material.GOLD_HOE
        }),
        IRON(Material.IRON_INGOT, new Material[]{
            Material.IRON_AXE,
            Material.IRON_PICKAXE,
            Material.IRON_SPADE,
            Material.IRON_SWORD,
            Material.IRON_HOE
        }),
        STRING(Material.STRING, new Material[]{
            Material.BOW
        });

        private final Material material;
        private final Material[] items;

        public static Optional<Input> getByItem(ItemStack stack) {
            for (Input input : values()) {
                for (Material type : input.items) {
                    if (Objects.equals(type, stack.getType())) {
                        return Optional.of(input);
                    }
                }
            }

            return Optional.empty();
        }
    }
}
