package net.hyze.core.bungee.client;

import net.hyze.client.protocol.AbstractProtocolHandler;
import net.hyze.client.protocol.ProtocolReference;
import net.hyze.client.protocol.api.AbstractPacket;
import net.hyze.client.protocol.api.BufferInput;
import net.hyze.client.protocol.api.BufferOutput;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProtocolHandler extends AbstractProtocolHandler<ProxiedPlayer> implements Listener {

    @Override
    public void callPacket(AbstractPacket ap) {
    }

    @Override
    public void sendPacket(ProxiedPlayer player, BufferOutput output) {

    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase(ProtocolReference.CHANNEL)) {
            return;
        }

        byte[] message = event.getData();

        receivePacket(new BufferInput(message));
    }

}
