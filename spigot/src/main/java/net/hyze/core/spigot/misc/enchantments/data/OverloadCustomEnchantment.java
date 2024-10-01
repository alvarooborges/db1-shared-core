package net.hyze.core.spigot.misc.enchantments.data;

import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.greenrobot.eventbus.Subscribe;

public class OverloadCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_overload";
    private final String PLAYER_METADATA = "custom_enchantment_overload_damage";

    public OverloadCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Sobrecarga";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Após causar uma certa quantidade",
            "&7de dano com a sua espada, um raio",
            "&7será lançado no próximo inimigo que",
            "&7for atacado."
        };
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
            CustomEnchantmentSlot.SWORD
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

        double damage = 0;

        if (player.hasMetadata(PLAYER_METADATA)) {
            damage = player.getMetadata(PLAYER_METADATA).get(0).asDouble();
            player.removeMetadata(PLAYER_METADATA, CoreSpigotPlugin.getInstance());
        }

        damage += event.getFinalDamage();

        if (damage >= 130 - (10 * trigger.getLevel())) {
            LightningStrike strike = victim.getWorld().strikeLightning(victim.getLocation());

            strike.setMetadata(
                    CoreSpigotConstants.NBTKeys.ENTITY_TRUE_DAMAGE,
                    new FixedMetadataValue(CoreSpigotPlugin.getInstance(), 2 + trigger.getLevel())
            );

            strike.setMetadata(
                    CoreSpigotConstants.NBTKeys.ENTITY_OWNER_DAMAGE,
                    new FixedMetadataValue(CoreSpigotPlugin.getInstance(), player)
            );
        } else {
            player.setMetadata(PLAYER_METADATA, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), damage));
        }
    }
}
