package net.hyze.core.shared.providers;

import net.hyze.client.protocol.AbstractProtocolHandler;
import net.hyze.core.shared.CoreWrapper;
import net.hyze.core.shared.contracts.Provider;

public class ClientProtocolProvider implements Provider<AbstractProtocolHandler> {

    private AbstractProtocolHandler handle;

    @Override
    public void prepare() {
        this.handle = CoreWrapper.getWrapper().getProtocolHandle();
    }

    @Override
    public AbstractProtocolHandler provide() {
        return handle;
    }

    @Override
    public void shut() {
    }

}
