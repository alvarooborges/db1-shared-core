package net.hyze.core.bungee;

import net.hyze.core.bungee.client.ProtocolHandler;
import net.hyze.core.shared.CoreWrapper;
import net.hyze.client.protocol.AbstractProtocolHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CoreBungeeWrapper extends CoreWrapper {

    private final ProtocolHandler clientProtocolHandler;

    public CoreBungeeWrapper() {
        this.clientProtocolHandler = new ProtocolHandler();
    }

    @Override
    public void sendMessage(String playerName, String message) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        if (player != null) {
            player.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    @Override
    public void sendMessage(String playerName, BaseComponent... message) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public AbstractProtocolHandler getProtocolHandle() {
        return this.clientProtocolHandler;
    }
}
