package net.hyze.core.spigot.misc.enchantments.data;

import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.PlayerItemDamageTrigger;
import org.greenrobot.eventbus.Subscribe;

public class ToughnessCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_toughness";

    public ToughnessCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Dureza";
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
            CustomEnchantmentSlot.ALL
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Possui uma chance de ignorar",
            "&7o dano que seria causado",
            "&7na sua armadura."
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Subscribe
    public void on(PlayerItemDamageTrigger trigger) {
        if (Math.random() * 100 < (1.5 * trigger.getLevel())) {
            trigger.getEvent().setCancelled(true);
        }
    }
}
