package net.hyze.core.spigot.misc.enchantments.data;

import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.events.ArmorEquipEvent;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.triggers.ArmorEquipTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.PlayerMoveTrigger;
import net.hyze.core.spigot.misc.utils.PlayerUtils;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.greenrobot.eventbus.Subscribe;

public class TirelessPursuitCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_tireless_pursuit";

    private static final String PLAYER_METADATA = "custom_enchantment_tireless_pursuit_old_speed";
    private final Map<Player, BukkitTask> tasks = Maps.newHashMap();

    public TirelessPursuitCustomEnchantment() {
        super(KEY);
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
            CustomEnchantmentSlot.ARMOR_FEET
        };
    }

    @Override
    public final int getMaxLevel() {
        return 2;
    }

    @Override
    public String getDisplayName() {
        return "Perseguição Incansável";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "&7Correr atrás de um inimigo que",
            "&7esteja com a armadura muito",
            "&7danificada lhe fornece o efeito",
            "&7de Velocidade 3."
        };
    }

    private void clearEffect(Player player) {
        clearEffect(player, false);
    }

    private void clearEffect(Player player, boolean instant) {
        BukkitTask task = tasks.get(player);

        if (task != null) {
            if (!instant) {
                return;
            }

            task.cancel();
        }

        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            task = new BukkitRunnable() {
                @Override
                public void run() {

                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        if (effect.getType().equals(PotionEffectType.SPEED) && effect.getDuration() >= 32767) {
                            player.removePotionEffect(PotionEffectType.SPEED);
                        }
                    }

                    if (player.hasMetadata(PLAYER_METADATA)) {
                        PotionEffect oldEffect = (PotionEffect) player.getMetadata(PLAYER_METADATA).get(0).value();

                        player.removeMetadata(PLAYER_METADATA, CoreSpigotPlugin.getInstance());

                        player.addPotionEffect(oldEffect, true);
                    }

                    tasks.remove(player);
                }

            }.runTaskLater(CoreSpigotPlugin.getInstance(), instant ? 1 : 20 * 2);

            tasks.put(player, task);
        }
    }

    private void applyEffect(Player player) {
        BukkitTask task = tasks.remove(player);

        if (task != null) {
            task.cancel();
        }

        if (!player.hasMetadata(PLAYER_METADATA) && player.hasPotionEffect(PotionEffectType.SPEED)) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED) && effect.getDuration() < 10000) {

                    player.setMetadata(PLAYER_METADATA,
                            new FixedMetadataValue(CoreSpigotPlugin.getInstance(), new PotionEffect(
                                    effect.getType(),
                                    effect.getDuration(),
                                    effect.getAmplifier(),
                                    effect.isAmbient()
                            ))
                    );

                    break;
                }
            }
        }

        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED)) {
                    if (effect.getDuration() >= 32767) { // Se o jogador já tem a opção infinita
                        return;
                    }
                }
            }
        }

        player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1.3f, 2f);
        player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 1.3f, 1f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
    }

    @Subscribe
    public void on(ArmorEquipTrigger trigger) {
        ArmorEquipEvent event = trigger.getEvent();

        if (trigger.getItem().equals(event.getOldArmorPiece())) {
            clearEffect(trigger.getPlayer(), true);
        }
    }

    @Subscribe
    public void on(PlayerMoveTrigger trigger) {
        Player player = trigger.getPlayer();

        if (!trigger.getItem().equals(player.getInventory().getArmorContents()[0])) {
            return;
        }

        Player target = PlayerUtils.getLookingAt(player, 5, 15);

        if (target == null || !CoreSpigotConstants.IS_HOSTILE.apply(player, target)) {
            clearEffect(player);
            return;
        }

        User targetUser = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

        if (targetUser == null || !CombatManager.isTagged(targetUser)) {
            clearEffect(player);
            return;
        }

        Function<ItemStack, Boolean> isBroken = stack -> {
            if (stack == null || stack.getType() == Material.AIR) {
                return false;
            }

            float currentDur = (float) stack.getDurability();
            float maxDur = (float) stack.getType().getMaxDurability();

            return ((maxDur - currentDur) / maxDur) * 100f <= 25 + (5 * trigger.getLevel());
        };

        Supplier<Boolean> hasBrokenArmor = () -> {
            return Arrays.stream(target.getInventory().getArmorContents())
                    .anyMatch(isBroken::apply);
        };

        if (!hasBrokenArmor.get()) {
            clearEffect(player);
            return;
        }

        applyEffect(player);
    }
}
