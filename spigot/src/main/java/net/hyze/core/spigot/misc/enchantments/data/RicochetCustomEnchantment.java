package net.hyze.core.spigot.misc.enchantments.data;

import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.greenrobot.eventbus.Subscribe;

public class RicochetCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_ricochet";

    @Getter
    private final String displayName = "Ricochete";

    @Getter
    private final int maxLevel = 1;

    @Getter
    private final String[] description = new String[]{
        "&7Ao acertar uma flecha em um",
        "&7inimigo, ela possui uma chance",
        "&7de ricochetear e atingir algum",
        "&7outro inimigo próximo."
    };

    @Getter
    private final CustomEnchantmentSlot[] slots = new CustomEnchantmentSlot[]{
        CustomEnchantmentSlot.BOW
    };

    public RicochetCustomEnchantment() {
        super(KEY);
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

        if (CoreConstants.RANDOM.nextInt(100) > (30 * trigger.getLevel())) {
            return;
        }

        Arrow arrow = (Arrow) event.getDamager();

        List<Player> nearbyPLayers = victim.getNearbyEntities(4, 4, 4)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .filter(target -> target != victim && target != player)
                .collect(Collectors.toList());

        if (!nearbyPLayers.isEmpty()) {
            nearbyPLayers.get(0).damage(event.getDamage() * 1.2, arrow);
        }
    }
}
