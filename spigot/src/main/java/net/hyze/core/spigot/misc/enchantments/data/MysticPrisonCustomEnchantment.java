package net.hyze.core.spigot.misc.enchantments.data;

import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import java.util.Set;
import lombok.Getter;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.greenrobot.eventbus.Subscribe;

public class MysticPrisonCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_mystic_prison";

    private final Set<Player> ACTIVATED = Sets.newHashSet();

    @Getter
    private final CustomEnchantmentSlot[] slots = new CustomEnchantmentSlot[]{
        CustomEnchantmentSlot.BOW
    };

    public MysticPrisonCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Ao acertar uma flecha em um",
            "&7inimigo, ela possui uma chance",
            "&7de deixar o jogador paralisado."
        };
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public String getDisplayName() {
        return "Prisão Mística";
    }

    @Subscribe
    public void on(EntityDamageByEntityTrigger trigger) {
        EntityDamageByEntityEvent event = trigger.getEvent();

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Arrow)) {
            return;
        }

        Player player = trigger.getPlayer();
        Player victim = (Player) event.getEntity();

        if (player == victim) {
            return;
        }

        if (ACTIVATED.contains(victim)) {
            return;
        }

        if (CoreConstants.RANDOM.nextInt(100) > (5 * trigger.getLevel())) {
            return;
        }

        final float walkSpeed = victim.getWalkSpeed();

        long time = 40;

        for (PotionEffect effect : victim.getActivePotionEffects()) {
            if (effect.getType() == PotionEffectType.JUMP) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (victim.isOnline()) {
                            victim.addPotionEffect(effect, true);
                        }
                    }
                }.runTaskLater(CoreSpigotPlugin.getInstance(), time + 5);
                break;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (victim.isOnline()) {
                    ACTIVATED.remove(victim);
                    victim.setWalkSpeed(walkSpeed);
                }
            }
        }.runTaskLater(CoreSpigotPlugin.getInstance(), time);

        victim.setWalkSpeed(0);
        victim.setSprinting(false);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (int) time, -20), true);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) time * 2, 1), true);

        ACTIVATED.add(victim);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (ACTIVATED.contains(player)) {
                player.setVelocity(new Vector(0, -2, 0).normalize());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerToggleSprintEvent event) {
        if (ACTIVATED.contains(event.getPlayer()) && event.isSprinting()) {
            event.getPlayer().setSprinting(false);
        }
    }
}
