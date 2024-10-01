package net.hyze.core.shared;

import net.hyze.client.protocol.AbstractProtocolHandler;
import net.md_5.bungee.api.chat.BaseComponent;

public class EmptyCoreWrapper extends CoreWrapper {

    @Override
    public void sendMessage(String player, String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendMessage(String player, BaseComponent... message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AbstractProtocolHandler getProtocolHandle() {
        return null;
    }
}
