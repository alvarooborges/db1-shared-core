package net.hyze.core.bungee;

import net.hyze.core.bungee.client.ProtocolHandler;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.api.Echo;
import java.util.function.Consumer;
import net.hyze.client.protocol.ProtocolReference;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;

public class CoreBungeePlugin extends CustomPlugin {

    public CoreBungeePlugin() {
        super(false);

        CoreBungeeWrapper.setWrapper(new CoreBungeeWrapper());
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ProxyServer proxy = ProxyServer.getInstance();

        PluginManager pluginManager = proxy.getPluginManager();

        Echo echo = CoreProvider.Redis.ECHO.provide();

        Consumer<Runnable> consumer = Runnable::run;

        echo.subscribe((packet, runnable) -> {
            consumer.accept(runnable);
        });
        
        BungeeCord.getInstance().getPluginManager().registerListener(this, new ProtocolHandler());
        BungeeCord.getInstance().registerChannel(ProtocolReference.CHANNEL);
    }
}
