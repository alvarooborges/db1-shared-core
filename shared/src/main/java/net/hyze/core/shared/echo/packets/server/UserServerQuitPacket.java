package net.hyze.core.shared.echo.packets.server;

import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserServerQuitPacket extends EchoPacket {

    private User user;
    private Server server;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
        PacketSerializeUtil.writeString(buffer, this.server.getId());
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
        this.server = Server.getById(buffer.readString()).orNull();
    }

}
