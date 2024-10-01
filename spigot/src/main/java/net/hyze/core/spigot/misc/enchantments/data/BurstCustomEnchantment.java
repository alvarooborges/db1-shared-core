package net.hyze.core.spigot.misc.enchantments.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityShootBowTrigger;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.ItemBow;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.greenrobot.eventbus.Subscribe;

public class BurstCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_burst";

    public BurstCustomEnchantment() {
        super(KEY);
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
            CustomEnchantmentSlot.BOW
        };
    }

    @Override
    public final int getMaxLevel() {
        return 2;
    }

    @Override
    public String getDisplayName() {
        return "Rajada";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Ao acertar flechas em seus",
            "&7inimigos, os seu próximos",
            "&7disparos lançam uma",
            "&7rajada."
        };
    }

    private final Map<Player, Integer> HIT_COUNTER = Maps.newHashMap();
    private final Set<Player> ACTIVATED = Sets.newHashSet();

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

        if (event.getDamager().hasMetadata(KEY)) {
            victim.setNoDamageTicks(0);
        }

        if (ACTIVATED.contains(player)) {
            return;
        }

        HIT_COUNTER.put(player, HIT_COUNTER.getOrDefault(player, 0) + 1);

        if (HIT_COUNTER.get(player) >= 4 - (trigger.getLevel() - 1)) {
            ACTIVATED.add(player);
            HIT_COUNTER.remove(player);
        }
    }

    @Subscribe
    public void on(EntityShootBowTrigger trigger) {
        EntityShootBowEvent event = trigger.getEvent();

        if (event.getProjectile().hasMetadata(KEY)) {
            return;
        }

        Player player = trigger.getPlayer();

        if (!ACTIVATED.contains(player)) {
            return;
        }

        HIT_COUNTER.put(player, HIT_COUNTER.getOrDefault(player, 0) + 1);

        if (HIT_COUNTER.get(player) > trigger.getLevel()) {
            ACTIVATED.remove(player);
            HIT_COUNTER.remove(player);
            return;
        }

        event.getProjectile().setMetadata(KEY, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), true));
        event.getProjectile().setMetadata("ignoreDamageTicks", new FixedMetadataValue(CoreSpigotPlugin.getInstance(), true));

        ItemBow bow = (ItemBow) CraftItemStack.asNMSCopy(trigger.getItem()).getItem();

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        World world = ((CraftWorld) event.getEntity().getWorld()).getHandle();

        int force = (int) (event.getForce() * 20f - bow.d(CraftItemStack.asNMSCopy(trigger.getItem()))) * -1;

        for (int i = 1; i <= 2; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                    }

                    bow.launch(CraftItemStack.asNMSCopy(trigger.getItem()), world, entityPlayer, force, arrow -> {
                        arrow.getBukkitEntity().setMetadata(KEY, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), true));
                        arrow.getBukkitEntity().setMetadata("ignoreDamageTicks", new FixedMetadataValue(CoreSpigotPlugin.getInstance(), true));
                    });
                }
            }.runTaskLater(CoreSpigotPlugin.getInstance(), 5L * i);
        }
    }
}
