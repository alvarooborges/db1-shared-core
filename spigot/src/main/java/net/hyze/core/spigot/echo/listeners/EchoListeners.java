package net.hyze.core.spigot.echo.listeners;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.apps.ServerStatus;
import net.hyze.core.shared.echo.api.EchoListener;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.echo.packets.SendMessagePacket;
import net.hyze.core.shared.echo.packets.config.ConfigReloadPacket;
import net.hyze.core.shared.echo.packets.user.UserConnectPacket;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.echo.packets.RestartPacket;
import net.hyze.core.spigot.misc.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EchoListeners implements EchoListener {

    @Subscribe
    public void on(RestartPacket packet) {

        if (!packet.getApps().contains(CoreProvider.getApp().getId())) {
            return;
        }

        ((ServerStatus) CoreProvider.getApp().getStatus()).setRestarting(true);

        AtomicInteger countdown = new AtomicInteger(packet.getSeconds());
        String subTitle = "&e&lEM %s SEGUNDOS";

        new BukkitRunnable() {
            @Override
            public void run() {

                if (countdown.get() == packet.getSeconds() || (countdown.get() > 0 && countdown.get() <= 10)) {
                    Bukkit.broadcastMessage(
                            MessageUtils.translateColorCodes(String.format("\n&eEste servidor será reiniciado em %s segundos!\n ", countdown.get()))
                    );
                }

                int count_ = countdown.getAndDecrement();

                if (count_ == 0) {
                    /**
                     * Envia todo mundo para lobbies aleatórios.
                     */
                    List<App> lobbies = CoreProvider.Cache.Local.APPS.provide().get(AppType.LOBBY).stream()
                            .filter(app -> CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(app.getId(), AppStatus.class) != null)
                            .collect(Collectors.toList());

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        User targetUser = CoreProvider.Cache.Local.USERS.provide().get(player.getName());
                        Collections.shuffle(lobbies);
                        CoreProvider.Redis.ECHO.provide().publish(new UserConnectPacket(targetUser, lobbies.get(0), UserConnectPacket.Reason.PLUGIN));
                    });
                    return;
                }

                if (count_ <= -3) {
                    Bukkit.shutdown();
                    this.cancel();
                }

                if (count_ < 0) {
                    return;
                }

                Title title = new Title()
                        .stay(40)
                        .title("&e&lREINICIANDO")
                        .subTitle(String.format(subTitle, count_));

                Bukkit.getOnlinePlayers().forEach(player -> {
                    title.send(player);
                    player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
                });

            }
        }.runTaskTimer(CoreSpigotPlugin.getInstance(), 20L, 20L);

    }

    @Subscribe
    public void on(ConfigReloadPacket packet) {
        Printer.INFO.coloredPrint("&e[Config] Reloading...");
        CoreProvider.Cache.Local.CONFIG.provide().update(packet.getConfig());
        Printer.INFO.coloredPrint("&e[Config] Reloaded.");
    }

    @Subscribe
    public void on(SendMessagePacket packet) {
        packet.getUsers().stream()
                .map(user -> Bukkit.getPlayerExact(user.getNick()))
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .forEach(player -> {
                    player.spigot().sendMessage(packet.getComponents());
                });
    }

    @Subscribe
    public void on(BroadcastMessagePacket packet) {

        if (packet.getServer() != null) {
            if (!Objects.equals(packet.getServer(), CoreProvider.getApp().getServer())) {
                return;
            }
        }

        boolean empty = packet.getGroups().isEmpty();

        Bukkit.getOnlinePlayers().stream()
                .filter(target -> {
                    if (empty) {
                        return true;
                    }

                    User user = CoreProvider.Cache.Local.USERS.provide().get(target.getName());

                    return user != null && user.isLogged() && packet.getGroups().stream()
                            .anyMatch(group -> {
                                if (packet.isGroupStrict()) {
                                    return user.hasStrictGroup(group);
                                }

                                return user.hasGroup(group);
                            });
                })
                .forEach(target -> target.spigot().sendMessage(packet.getComponents()));
    }
}
