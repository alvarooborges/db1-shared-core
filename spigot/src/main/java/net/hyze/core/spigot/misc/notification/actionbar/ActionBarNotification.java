package net.hyze.core.spigot.misc.notification.actionbar;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.Setter;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.utils.ActionBarMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarNotification {

    private static final Map<Player, BukkitTask> TASKS = Maps.newConcurrentMap();

    private static final int DELAY = 10;

    @Setter
    @NonNull
    private Supplier<ActionBarDefaultMessage> defaultMessageSupplier = () -> new ActionBarDefaultMessage(null);

    private String currentNotificationText;

    private int currentNotificationTicks;

    public ActionBarNotification(final Player player) {

        if (TASKS.containsKey(player)) {
            TASKS.get(player).cancel();
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }

                    if (!player.isValid()) {
                        return;
                    }

                    ActionBarDefaultMessage defaultMessage = defaultMessageSupplier.get();

                    if (currentNotificationText != null && (defaultMessage == null || !defaultMessage.isPreventNotification())) {
                        if (currentNotificationTicks < 20 * 3) {
                            new ActionBarMessage()
                                    .text(currentNotificationText)
                                    .send(player);
                            currentNotificationTicks += DELAY;
                            return;
                        }
                    }

                    currentNotificationText = null;
                    currentNotificationTicks = 0;

                    if (defaultMessage != null) {
                        String message = defaultMessage.getMessage();

                        if (message != null) {
                            new ActionBarMessage()
                                    .text(message)
                                    .send(player);
                        }
                    }
                } catch (Exception e) {
                    cancel();
                }
            }
        }.runTaskTimer(CoreSpigotPlugin.getInstance(), 20, DELAY);

        TASKS.put(player, task);
    }

    public void sendNotification(String message) {
        this.currentNotificationText = message;
        this.currentNotificationTicks = 0;
    }
}
