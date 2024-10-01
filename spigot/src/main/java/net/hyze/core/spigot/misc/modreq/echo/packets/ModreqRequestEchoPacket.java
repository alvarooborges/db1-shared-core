package net.hyze.core.spigot.misc.modreq.echo.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.user.User;

@Getter
@ServerPacket
@AllArgsConstructor
@NoArgsConstructor
public class ModreqRequestEchoPacket extends EchoPacket {

    private User user;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
    }
}
