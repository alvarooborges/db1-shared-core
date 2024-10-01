package net.hyze.core.spigot.misc.enchantments.data;

import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.greenrobot.eventbus.Subscribe;

public class ExecutionCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_execution";

    public ExecutionCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Execução";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Ao causar dano suficiente para",
            "&7deixar o seu inimigo com pouca",
            "&7vida, ele é executado."
        };
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
            CustomEnchantmentSlot.SWORD,
            CustomEnchantmentSlot.AXES
        };
    }

    @Override
    public int getMaxLevel() {
        return 1;
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

        if (Math.round(victim.getHealth() - event.getFinalDamage()) <= Math.min(trigger.getLevel(), 5)) {

            for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
                if (event.isApplicable(modifier)) {
                    event.setDamage(modifier, 0);
                }
            }

            event.setDamage(EntityDamageEvent.DamageModifier.BASE, victim.getHealth());

            startDeathAnimation(victim);

            Message.INFO.send(player, String.format(
                    "%s foi executado pelo encantamento %s.",
                    victim.getName(),
                    getDisplayName()
            ));
        }
    }

    private void startDeathAnimation(Player target) {

    }
}
