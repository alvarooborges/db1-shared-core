package net.hyze.core.spigot.misc.enchantments.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentUtil;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.TriggerFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.greenrobot.eventbus.Subscribe;

public class DivineProtectionCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_divine_protection";

    private final HashBasedTable<Player, Player, Long> HIT_LOG = HashBasedTable.create();
    private final Set<Player> ACTIVATED = Sets.newHashSet();

    public DivineProtectionCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Proteção Divina";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Ao receber dano de vários inimigos",
            "&7ao mesmo tempo, fornece o efeito",
            "&7de Resistência ou o amplifica caso",
            "&7você já possua."
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

        if (player != victim) {
            return;
        }

        trigger.setStopPropagation(true);

        List<ItemStack> items = TriggerFactory.getPlayersItems(player);

        int totalLevel = 0;

        for (ItemStack item : items) {
            int level = CustomEnchantmentUtil.getEnchantmentLevel(item, this);
            totalLevel += level;
        }

        if (totalLevel == 0) {
            return;
        }

        HIT_LOG.put(victim, player, System.currentTimeMillis());

        Map<Player, Long> log = HIT_LOG.row(victim);

        Iterator<Map.Entry<Player, Long>> iterator = log.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Player, Long> entry = iterator.next();
            if (entry.getValue() < System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(5)) {
                iterator.remove();
            }
        }

        if (log.size() >= 1 - (totalLevel - 1)) {

            if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                for (PotionEffect effect : player.getActivePotionEffects()) {

                    if (effect.getType() == PotionEffectType.DAMAGE_RESISTANCE) {
                        int duration = effect.getDuration() / 20;

                        if (duration > 10) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.addPotionEffect(new PotionEffect(
                                            PotionEffectType.DAMAGE_RESISTANCE,
                                            (duration - 10) * 20,
                                            effect.getAmplifier()
                                    ), true);
                                }
                            }.runTaskLater(CoreSpigotPlugin.getInstance(), 10 * 20);
                        }
                    }
                }
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10 * 20, 2), true);
        }
    }

}
