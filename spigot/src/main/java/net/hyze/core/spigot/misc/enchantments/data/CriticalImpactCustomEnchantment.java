package net.hyze.core.spigot.misc.enchantments.data;

import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.greenrobot.eventbus.Subscribe;

public class CriticalImpactCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_critical_impact";
    private final String PLAYER_METADATA = "custom_enchantment_critical_impact_accumulated_armor_damage";

    public CriticalImpactCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Impacto Crítico";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Ao causar dano em um jogador",
            "&7com um ataque crítico, ele receberá",
            "&7dano adicional em sua armadura."
        };
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
            CustomEnchantmentSlot.AXES
        };
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Subscribe
    public void on(EntityDamageByEntityTrigger trigger) {
        EntityDamageByEntityEvent event = trigger.getEvent();

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = trigger.getPlayer();
        Player victim = (Player) event.getEntity();

        if (player == victim) {
            return;
        }

        if (player.isOnGround()) {
            return;
        }

        float armorDamage = (float) (event.getDamage() + event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) + event.getDamage(EntityDamageEvent.DamageModifier.HARD_HAT));

        float armorDamageBonus = (armorDamage / 100) * (6 + (2 * trigger.getLevel()));

        if (player.hasMetadata(PLAYER_METADATA)) {
            armorDamageBonus += player.getMetadata(PLAYER_METADATA).get(0).asFloat();
            player.removeMetadata(PLAYER_METADATA, CoreSpigotPlugin.getInstance());
        }

        float newArmorDamage = armorDamage + armorDamageBonus;

        if ((int) (newArmorDamage / 4f) > (int) (armorDamage / 4f)) {
            event.setNoDamageArmor(true);

            try {
                Method m = EntityLiving.class.getDeclaredMethod("damageArmor", float.class);
                m.setAccessible(true);

                m.invoke(((CraftPlayer) victim).getHandle(), newArmorDamage);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(CriticalImpactCustomEnchantment.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            player.setMetadata(
                    PLAYER_METADATA,
                    new FixedMetadataValue(CoreSpigotPlugin.getInstance(), armorDamageBonus)
            );
        }
    }
}
