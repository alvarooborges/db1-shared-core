package net.hyze.core.spigot;

import com.google.common.base.Enums;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatusManager;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.apps.ServerStatus;
import net.hyze.core.shared.echo.packets.app.AppStartedPacket;
import net.hyze.core.shared.exceptions.ApplicationAlreadyPreparedException;
import net.hyze.core.shared.exceptions.InvalidApplicationException;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.spigot.applications.SpigotStatus;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

@NoArgsConstructor
public abstract class CustomPlugin extends JavaPlugin {

    public boolean prepareProvider = false;

    private BukkitTask statusTask;

    //    private JarUpdater jarUpdater;
    public CustomPlugin(boolean prepareProvider) {
        this.prepareProvider = prepareProvider;
    }

    @Override
    public void onLoad() {
        if (this.prepareProvider) {
            try {
                String ip = Bukkit.getIp();
                int port = Bukkit.getPort();

                String rawId = System.getProperty("net.hyze.id");
                String rawServer = System.getProperty("net.hyze.server");
                String rawDisplayName = System.getProperty("net.hyze.name");
                String rawType = System.getProperty("net.hyze.type");

                App app;

                if (rawId != null && rawServer != null && rawDisplayName != null && rawType != null) {
                    Server server = Enums.getIfPresent(Server.class, rawServer).orNull();
                    AppType type = Enums.getIfPresent(AppType.class, rawType).orNull();

                    app = new App(rawId, rawDisplayName, type, new InetSocketAddress(ip, port), server);

                    CoreProvider.prepare(app);
                } else {
                    app = CoreProvider.prepare(port);
                }

                SpigotStatus status = new SpigotStatus(
                        app.getId(),
                        app.getType(),
                        app.getServer(),
                        app.getAddress(),
                        System.currentTimeMillis(), 0, 0, 0, false
                );

                app.setStatus(status);
            } catch (InvalidApplicationException | ApplicationAlreadyPreparedException ex) {
                Logger.getGlobal().log(Level.SEVERE, "Failed to prepare core provider", ex);
                Bukkit.shutdown();
            }
        }
    }

    @Override
    public void onEnable() {
        if (this.prepareProvider) {
            AppStatusManager manager = new AppStatusManager();

            App app = CoreProvider.getApp();

            statusTask = new BukkitRunnable() {
                @Override
                public void run() {
                    ((ServerStatus) app.getStatus())
                            .setOnlineCount(Bukkit.getOnlinePlayers().size());

                    manager.run();
                }
            }.runTaskTimer(this, 0, 20);

            Bukkit.getScheduler().runTaskLater(this, () -> {
                CoreProvider.Redis.ECHO.provide().publish(new AppStartedPacket(app));
            }, 10);
        }
    }

    @Override
    public void onDisable() {
        try {
            if (this.prepareProvider) {
                statusTask.cancel();

                CoreProvider.Cache.Redis.APPS_STATUS.provide().delete(CoreProvider.getApp());

                CoreProvider.shut();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
