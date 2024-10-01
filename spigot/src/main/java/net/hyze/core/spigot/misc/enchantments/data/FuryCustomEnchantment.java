package net.hyze.core.spigot.misc.enchantments.data;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.greenrobot.eventbus.Subscribe;

public class FuryCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_fury";

    private final HashBasedTable<Player, Player, Long> HIT_LOG = HashBasedTable.create();
    private final Set<Player> ACTIVATED = Sets.newHashSet();
    private final Map<Player, BukkitTask> TASKS = Maps.newHashMap();

    @Getter
    private final String displayName = "Fúria";

    @Getter
    private final int maxLevel = 3;

    @Getter
    private final String[] description = new String[]{
        "&7Ao ser atacado por vários inimigos",
        "&7ao mesmo tempo, a sua arma",
        "&7começa a causar dano em área."
    };

    @Getter
    private final CustomEnchantmentSlot[] slots = new CustomEnchantmentSlot[]{
        CustomEnchantmentSlot.SWORD,
        CustomEnchantmentSlot.AXES
    };

    public FuryCustomEnchantment() {
        super(KEY);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                CoreSpigotPlugin.getInstance(),
                ListenerPriority.HIGHEST,
                Lists.newArrayList(PacketType.Play.Server.ENTITY_EQUIPMENT)
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketType type = event.getPacketType();

                if (type != PacketType.Play.Server.ENTITY_EQUIPMENT) {
                    return;
                }

                WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(event.getPacket());

                if (wrapper.getHandle().getIntegers().read(1) != 4) {
                    return;
                }

                List<Integer> activatedId = ACTIVATED.stream().map(Entity::getEntityId).collect(Collectors.toList());

                if (!activatedId.contains(wrapper.getEntityID())) {
                    return;
                }

                ItemStack stack = wrapper.getItem();

                if (stack == null || HeadTexture.FURY_ENCHANTMENT.getHead().isSimilar(stack)) {
                    return;
                }

                event.setCancelled(true);

                buildFakeHeadPacket(wrapper.getEntityID()).sendPacket(event.getPlayer());
            }
        });
    }

    @Subscribe
    public void on(EntityDamageByEntityTrigger trigger) {
        EntityDamageByEntityEvent event = trigger.getEvent();

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = trigger.getPlayer();
        Player victim = (Player) event.getEntity();

        /**
         * Se o jogador for a vitima o hit deve ser adicionado ao log.
         */
        if (player == victim) {
            HIT_LOG.put(victim, player, System.currentTimeMillis());

            Map<Player, Long> log = HIT_LOG.row(victim);

            /**
             * Removendo do log todos os hits que foram dados a mais de 2
             * segundos.
             */
            Iterator<Map.Entry<Player, Long>> iterator = log.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Player, Long> entry = iterator.next();
                if (entry.getValue() < System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(5)) {
                    iterator.remove();
                }
            }

            User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

            /**
             * Se a vitima atingiu a quantidade necessaria de hits, o efeito
             * será ativado.
             */
            if (UserCooldowns.hasEnded(user, "use-" + KEY) && !ACTIVATED.contains(player) && log.size() >= 2 - trigger.getLevel()) {
                UserCooldowns.start(user, "use-" + KEY, 30, TimeUnit.SECONDS);
                ACTIVATED.add(player);

                player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_SCREAM, 1.5f, 0);

                TASKS.put(player, Bukkit.getScheduler().runTaskTimer(CoreSpigotPlugin.getInstance(), () -> {
                    if (!player.isOnline()) {
                        return;
                    }

                    Location location = player.getLocation();

                    double radians = Math.toRadians(CoreConstants.RANDOM.nextInt(360));

                    double x = Math.cos(radians);
                    double z = Math.sin(radians);

                    location.add(x, .5, z);

                    location.getWorld().spigot().playEffect(location, Effect.FLAME, 0, 0, 0.5f, 0.5f, 0.5f, 0f, 1, 16);
                }, 0, 3));

                /**
                 * Enviando pacote fake
                 */
                WrapperPlayServerEntityEquipment fakeWrapper = buildFakeHeadPacket(player.getEntityId());

                player.getNearbyEntities(15, 15, 15)
                        .stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .forEach(target -> {
                            fakeWrapper.sendPacket(target);
                        });

                Bukkit.getScheduler().runTaskLater(CoreSpigotPlugin.getInstance(), () -> {
                    ACTIVATED.remove(player);

                    BukkitTask task = TASKS.remove(player);

                    if (task != null) {
                        task.cancel();
                    }

                    if (player.isOnline()) {
                        /**
                         * Enviando pacote original
                         */
                        WrapperPlayServerEntityEquipment originalWrapper = buildOriginalHeadPacket(player);

                        player.getNearbyEntities(15, 15, 15)
                                .stream()
                                .filter(entity -> entity instanceof Player)
                                .map(entity -> (Player) entity)
                                .forEach(target -> {
                                    originalWrapper.sendPacket(target);
                                });
                    }
                }, 20 * 10);
            }

            return;
        }

        if (ACTIVATED.contains(player)) {
            victim.getNearbyEntities(2, 2, 2)
                    .stream()
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> (Player) entity)
                    .filter(target -> !target.equals(player))
                    .filter(target -> !target.equals(victim))
                    .forEach(target -> {
                        target.damage(event.getDamage() * 0.6, player);
                    });
        }
    }

    private WrapperPlayServerEntityEquipment buildFakeHeadPacket(int entityId) {
        PacketContainer newContainer = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

        WrapperPlayServerEntityEquipment newWrapper = new WrapperPlayServerEntityEquipment(newContainer);

        newWrapper.setEntityID(entityId);
        newWrapper.setItem(HeadTexture.FURY_ENCHANTMENT.getHead());
        newWrapper.getHandle().getIntegers().write(1, 4);

        return newWrapper;
    }

    private WrapperPlayServerEntityEquipment buildOriginalHeadPacket(Player player) {
        PacketContainer newContainer = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

        WrapperPlayServerEntityEquipment newWrapper = new WrapperPlayServerEntityEquipment(newContainer);

        newWrapper.setEntityID(player.getEntityId());
        newWrapper.setItem(player.getInventory().getHelmet());
        newWrapper.getHandle().getIntegers().write(1, 4);

        return newWrapper;
    }
}
