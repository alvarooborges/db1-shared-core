package net.hyze.core.spigot.misc.enchantments.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import net.hyze.core.spigot.misc.message.Message;

import java.util.Map;
import java.util.Set;

import org.greenrobot.eventbus.Subscribe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

public class BattleImpetusCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_battler_impetus";

    public BattleImpetusCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Ímpeto da Batalha";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Ao combar um inimigo, os seus",
                "&7ataques causam dano adicional."
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
        return 4;
    }

    private final HashBasedTable<Player, Player, Integer> HIT_COUNTER = HashBasedTable.create();
    private final Set<Player> ACTIVATED = Sets.newHashSet();
    private final Map<Player, BukkitTask> TASKS = Maps.newHashMap();

    @Subscribe
    public void on(EntityDamageByEntityTrigger trigger) {
        EntityDamageByEntityEvent event = trigger.getEvent();

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = trigger.getPlayer();
        Player victim = (Player) event.getEntity();
        int level = trigger.getLevel();

        /**
         * Runnable responsável por resetar o contador de hit, desativar o
         * efeito caso esteja ativo e cancelar a task se ela existir.
         */
        Runnable clearJob = () -> {
            if (ACTIVATED.contains(player)) {
                if (HIT_COUNTER.containsRow(player)) {
                    HIT_COUNTER.row(player).clear();
                }

                ACTIVATED.remove(player);

                BukkitTask task = TASKS.remove(player);
                if (task != null) {
                    task.cancel();
                }

                Message.EMPTY.send(player, "&cÍmpeto da Batalha desativado!");
            }
        };

        /**
         * Se o jogador que diparou o trigger for a vitima, o contador deve ser
         * zerado e o efeito removido.
         */
        if (player == victim) {
            clearJob.run();
            return;
        }

        Integer counter = HIT_COUNTER.get(player, victim);

        if (counter == null) {
            counter = 0;
        }

        counter += 1;

        HIT_COUNTER.put(player, victim, counter);

        int objective = 7 - (level - 1);

        if (counter >= objective) {
            int diff = counter - objective;

            double additional = Math.min(0.35 * level, diff * (level * 0.25));

            event.setDamage(event.getDamage() + additional);

            if (!ACTIVATED.contains(player)) {

                ACTIVATED.add(player);

                Message.EMPTY.send(player, "&eÍmpeto da Batalha ativado!");

                /**
                 * Task para desativar o efeito.
                 */
                TASKS.put(player, Bukkit.getScheduler().runTaskLater(CoreSpigotPlugin.getInstance(), () -> {
                    clearJob.run();
                }, 20 * 5));
            }
        }
    }
}
