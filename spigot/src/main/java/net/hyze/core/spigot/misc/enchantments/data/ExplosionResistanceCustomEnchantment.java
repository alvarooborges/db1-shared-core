package net.hyze.core.spigot.misc.enchantments.data;

import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EnchantmentTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageTrigger;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.greenrobot.eventbus.Subscribe;

public class ExplosionResistanceCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_explosion_resistance";

    public ExplosionResistanceCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Resistência à Explosões";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Ignora parte do dano causado",
            "&7por explosões."
        };
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
            CustomEnchantmentSlot.ARMOR
        };
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Subscribe
    public void on(EntityDamageTrigger trigger) {
        EntityDamageEvent event = trigger.getEvent();

        if (event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return;
        }

        if (event.getDamage() > 0) {
            int level = trigger.getLevel();

            float protection = 6.25f * level;

            event.setDamage(Math.max(event.getDamage() - ((trigger.getRawDamage() / 100) * protection), 0));
        }
    }

    @Subscribe
    public void on(EntityDamageByEntityTrigger trigger) {
        EntityDamageByEntityEvent event = trigger.getEvent();

        if (event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return;
        }

        if (trigger.getPlayer() != event.getEntity()) {
            return;
        }

        if (event.getDamage() > 0) {
            int level = trigger.getLevel();

            float protection = 6.25f * level;

            event.setDamage(Math.max(event.getDamage() - ((trigger.getRawDamage() / 100) * protection), 0));
        }
    }
}
