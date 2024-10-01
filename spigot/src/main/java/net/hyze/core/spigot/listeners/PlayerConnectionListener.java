package net.hyze.core.spigot.listeners;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.echo.packets.UserAppQuitPacket;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(AsyncPlayerPreLoginEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "O servidor está reiniciando.");
            return;
        }

        if (event.getName() == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Tente novamente.");
            return;
        }

        App proxyApp = CoreProvider.Cache.Redis.USERS_STATUS.provide().getProxyApp(event.getName());

        if (proxyApp == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, String.format("Entre em nossa rede usando ip: %s", CoreConstants.Infos.IP));
            return;
        }

        try {
            /**
             * Removendo usuário do cache
             */
            CoreProvider.Cache.Local.USERS.provide().remove(event.getName());

            /**
             * Buscando/baixando usuário
             */
            User user = CoreProvider.Cache.Local.USERS.provide().get(event.getName());

            if (user != null) {
                /**
                 * Removendo cache de grupos
                 */
                CoreProvider.Cache.Local.USERS_GROUPS.provide().remove(user);

                /**
                 * Removendo cache de preferencias
                 */
                CoreProvider.Cache.Local.USERS_PREFERENCES.provide().remove(user);
                
            }

        } catch (Exception ex) {
            Logger.getLogger(PlayerConnectionListener.class.getName()).log(Level.SEVERE, null, ex);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "[ConnectionListener] Algo de errado aconteceu.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLowest(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(CoreSpigotPlugin.getInstance(), () -> {
            Title.clear(event.getPlayer());
        }, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMonitor(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        /**
         * Previnindo walk speed menor que o normal.
         */
        if (player.getWalkSpeed() < 0.2f) {
            player.setWalkSpeed(0.2f);
        }

        Bukkit.getScheduler().runTask(CoreSpigotPlugin.getInstance(), () -> {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.JUMP) && effect.getAmplifier() < 0) {
                    player.removePotionEffect(PotionEffectType.JUMP);
                    continue;
                }

                if (effect.getType().equals(PotionEffectType.SPEED) && effect.getDuration() >= 32767) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    continue;
                }
            }

            User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

            Predicate<User> allowGameMode = CoreSpigotConstants.ALLOW_GAMEMODE.getOrDefault(player.getGameMode(), Predicates.alwaysTrue());

            if (user == null || !allowGameMode.apply(user)) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        });

        event.setJoinMessage(null);

        CoreProvider.Cache.Redis.USERS_STATUS.provide().setBukkitApp(player.getName(), CoreProvider.getApp());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());
        CoreProvider.Redis.ECHO.provide().publish(new UserAppQuitPacket(user, CoreProvider.getApp()));
    }
}
