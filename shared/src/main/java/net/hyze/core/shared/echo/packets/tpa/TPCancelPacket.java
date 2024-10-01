package net.hyze.core.shared.echo.packets.tpa;

import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.ServerPacket;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ServerPacket
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TPCancelPacket extends EchoPacket {

    private User requester;
    private User target;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.requester);
        PacketSerializeUtil.writeUser(buffer, this.target);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.requester = PacketSerializeUtil.readUser(buffer);
        this.target = PacketSerializeUtil.readUser(buffer);
    }
}
