package net.hyze.core.spigot.misc.modreq.echo.listeners;

import net.hyze.core.shared.echo.api.EchoListener;
import net.hyze.core.spigot.misc.modreq.ModreqManager;
import net.hyze.core.spigot.misc.modreq.echo.packets.ModreqRequestEchoPacket;
import org.greenrobot.eventbus.Subscribe;

public class ModreqEchoListeners implements EchoListener {

    @Subscribe
    public void on(ModreqRequestEchoPacket packet) {
        ModreqManager.REQUESTS.put(packet.getUser(), System.currentTimeMillis());

    }
}
