package net.hyze.core.spigot.misc.enchantments.data;

import com.google.common.collect.Maps;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import java.util.Map;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.greenrobot.eventbus.Subscribe;

public class ThirstBloodCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_thirst_blood";

    private final Map<Player, Double> DAMAGE_COUNTER = Maps.newHashMap();

    @Getter
    private final String displayName = "Sede de Sangue";

    @Getter
    private final int maxLevel = 3;

    @Getter
    private final String[] description = new String[]{
        "&7Sua vida é regenerada ",
        "&7ao causar dano em seus",
        "&7inimigos."
    };

    @Getter
    private final CustomEnchantmentSlot[] slots = new CustomEnchantmentSlot[]{
        CustomEnchantmentSlot.SWORD
    };

    public ThirstBloodCustomEnchantment() {
        super(KEY);
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

        DAMAGE_COUNTER.put(player, DAMAGE_COUNTER.getOrDefault(player, 0d) + event.getDamage());

        double totalDamage = DAMAGE_COUNTER.get(player);

        if (totalDamage >= 20 - (trigger.getLevel() * 3)) {
            DAMAGE_COUNTER.remove(player);
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 1));
        }
    }
}
