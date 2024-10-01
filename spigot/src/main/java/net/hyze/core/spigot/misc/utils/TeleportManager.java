package net.hyze.core.spigot.misc.utils;

import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.echo.packets.SendMessagePacket;
import net.hyze.core.shared.echo.packets.user.UserConnectPacket;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.echo.packets.user.connect.UserConnectToTypePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.events.PlayerTeleportManagerEvent;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeleportManager {

    public static Group AWAIT_BYPASS_GROUP = Group.VIP;
    private static Set<UserConnectPacket.Reason> REASONS_BYPASS = Sets.newHashSet(
            UserConnectPacket.Reason.PLUGIN,
            UserConnectPacket.Reason.JOIN,
            UserConnectPacket.Reason.RESPAWN
    );

    public static void teleport(User user, AppType type, ConnectReason reason, String message) {
        TeleportManager.teleport(user, () -> {
            CoreProvider.Redis.ECHO.provide().publish(new UserConnectToTypePacket(
                    user,
                    CoreProvider.getApp().getServer(),
                    type,
                    reason
            ));
        }, UserConnectPacket.Reason.WARP);
    }

    public static void teleport(User user, App app, UserConnectPacket.Reason reason, String message) {
        Runnable runnable;

        Optional<String> messageOptional = Optional.ofNullable(message)
                .map(MessageUtils::translateColorCodes);

        runnable = () -> {
            CoreProvider.Redis.ECHO.provide().publish(new UserConnectPacket(
                    user,
                    app,
                    reason,
                    messageOptional.orElse(null)
            ));
        };

        TeleportManager.teleport(user, runnable, reason);
    }

    public static void teleport(User user, User target, UserConnectPacket.Reason reason, String message) {
        Player player = Bukkit.getPlayerExact(user.getNick());
        Player targetPlayer = Bukkit.getPlayerExact(target.getNick());

        Runnable runnable;

        Optional<String> messageOptional = Optional.ofNullable(message)
                .map(MessageUtils::translateColorCodes);

        if (player != null && player.isOnline() && targetPlayer != null && targetPlayer.isOnline()) {
            runnable = () -> {
                if (player.isOnline()) {
                    player.teleport(targetPlayer.getLocation());
                    player.teleport(targetPlayer.getLocation());

                    if (messageOptional.isPresent()) {
                        Message.EMPTY.send(player, messageOptional.get());
                    }
                }
            };
        } else {
            runnable = () -> {
                CoreProvider.Redis.ECHO.provide().publish(new UserConnectPacket(
                        user,
                        target,
                        reason,
                        messageOptional.orElse(null)
                ));
            };
        }

        TeleportManager.teleport(user, runnable, reason);
    }

    public static void teleport(User user, SerializedLocation location, UserConnectPacket.Reason reason, String message) {
        teleport(user, location, reason, message, reason.isAllowSplit());
    }

    public static void teleport(User user, SerializedLocation location, UserConnectPacket.Reason reason, String message, boolean allowSplit) {

        Player player = Bukkit.getPlayerExact(user.getNick());

        Runnable runnable;

        Optional<String> messageOptional = Optional.ofNullable(message)
                .map(MessageUtils::translateColorCodes);

        if (player != null && player.isOnline() && CoreProvider.getApp().getId().equals(location.getAppId())) {
            runnable = () -> {
                if (player.isOnline()) {
                    player.teleport(location.parser(new BukkitLocationParser()));
                    player.teleport(location.parser(new BukkitLocationParser()));

                    messageOptional.ifPresent(s -> Message.EMPTY.send(player, s));
                }
            };
        } else {
            runnable = () -> {
                UserConnectPacket packet = new UserConnectPacket(
                        user,
                        location,
                        reason,
                        messageOptional.orElse(null)
                );

                packet.setAllowSplit(allowSplit);

                CoreProvider.Redis.ECHO.provide().publish(packet);
            };
        }

        TeleportManager.teleport(user, runnable, reason);
    }

    private static void teleport(User user, Runnable runnable, UserConnectPacket.Reason reason) {

        Player player = Bukkit.getPlayerExact(user.getNick());

        if (player != null && player.isOnline()) {
            if (CombatManager.isTagged(user)) {
                Message.ERROR.sendDefault(player, net.hyze.core.shared.misc.utils.DefaultMessage.COMBAT_TELEPORT_ERROR);
                return;
            }
        }

        if (REASONS_BYPASS.contains(reason) || user.hasGroup(AWAIT_BYPASS_GROUP)) {

            if (player != null && player.isOnline()) {
                Bukkit.getPluginManager().callEvent(new PlayerTeleportManagerEvent(player));
            }

            runnable.run();
        } else {
            ComponentBuilder builder = new ComponentBuilder("\nVocê será teleportado em 3 segundos.\n")
                    .color(ChatColor.YELLOW)
                    .append(" * VIPs não precisam aguardar para serem teleportado.\n");

            SendMessagePacket sendMessagePacket = new SendMessagePacket(Collections.singleton(user), builder.create());

            CoreProvider.Redis.ECHO.provide().publish(sendMessagePacket);

            AtomicInteger times = new AtomicInteger();

            new BukkitRunnable() {
                @Override
                public void run() {

                    if (times.get() == 3) {
                        if (player != null && player.isOnline()) {
                            Title.clear(player);
                            Bukkit.getPluginManager().callEvent(new PlayerTeleportManagerEvent(player));
                        }

                        runnable.run();
                        cancel();
                        return;
                    } else {
                        if (player != null && player.isOnline()) {
                            try {
                                Title title = new Title()
                                        .title(ChatColor.YELLOW + "" + (3 - times.get()))
                                        .subTitle(ChatColor.YELLOW + " VIPs não precisam esperar." + StringUtils.repeat(".", times.get()))
                                        .stay(30 * 20);

                                title.send(player);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    times.incrementAndGet();
                }
            }.runTaskTimer(CoreSpigotPlugin.getInstance(), 0, 20);
        }
    }
}
