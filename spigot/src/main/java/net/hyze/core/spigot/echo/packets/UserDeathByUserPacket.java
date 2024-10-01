package net.hyze.core.spigot.echo.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.user.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ServerPacket
public class UserDeathByUserPacket extends EchoPacket {

    private User user;
    private User killer;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
        PacketSerializeUtil.writeUser(buffer, this.killer);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
        this.killer = PacketSerializeUtil.readUser(buffer);
    }
}
