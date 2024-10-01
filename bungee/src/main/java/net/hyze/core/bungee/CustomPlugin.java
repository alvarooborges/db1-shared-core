package net.hyze.core.bungee;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.apps.AppStatusManager;
import net.hyze.core.shared.apps.ServerStatus;
import net.hyze.core.shared.echo.packets.app.AppStartedPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.conf.YamlConfig;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public abstract class CustomPlugin<T extends ServerStatus> extends Plugin {

    public final boolean prepareProvider;

    @Getter
    private App<T> app;

    @Getter
    private InetSocketAddress address;

    @Override
    public void onLoad() {
        YamlConfig yamlConfig = new YamlConfig();
        yamlConfig.load();

        if (this.prepareProvider) {
            try {

                for (ListenerInfo info : yamlConfig.getListeners()) {

                    InetSocketAddress address = info.getHost();

                    if (address != null) {
                        this.address = address;
                        break;
                    }
                }

                int port = this.address.getPort();

                app = CoreProvider.prepare(port);

                app.setStatus((T) new ServerStatus(
                        app.getId(),
                        app.getType(),
                        app.getServer(),
                        app.getAddress(),
                        System.currentTimeMillis(), 0, 0, 0, false
                ));

            } catch (Exception ex) {
                Logger.getGlobal().log(Level.SEVERE, "Failed to prepare core provider", ex);
                ProxyServer.getInstance().stop();
            }
        }
    }


    @Override
    public void onEnable() {
        if (this.prepareProvider) {
            AppStatusManager manager = new AppStatusManager();

            App app = CoreProvider.getApp();

            getProxy().getScheduler().schedule(this, () -> {
                Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
                ((ServerStatus) app.getStatus()).setOnlineCount(players.size());

                manager.run();
            }, 0, 1, TimeUnit.SECONDS);

            getProxy().getScheduler().schedule(this, () -> {
                CoreProvider.Redis.ECHO.provide().publish(new AppStartedPacket(app));
            }, 2, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onDisable() {
        if (this.prepareProvider) {
            CoreProvider.shut();
        }
    }

    public void registerServerApp(AppStatus status) {

        if (status.getAppId().equalsIgnoreCase(getApp().getId())) {
            System.out.println(String.format("O app %s é o mesmo app que o proxy", status.getAppId()));
            return;
        }

        if (!status.getType().isAllowProxyRegistry()) {
            System.out.println(String.format(
                    "O app %s não pode ser registrado.",
                    status.getAppId()
            ));
            return;
        }

        System.out.println(String.format("Registrando servidor %s", status.getAppId()));

        getProxy().getConfig().getServers().put(
                status.getAppId(),
                getProxy().constructServerInfo(status.getAppId(), status.getAddress(), status.getAppId(), false)
        );
    }
}
