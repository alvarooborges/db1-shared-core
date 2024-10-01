package net.hyze.core.shared.echo.packets;

import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAppQuitPacket extends EchoPacket {

    private User user;
    private App app;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
        PacketSerializeUtil.writeApp(buffer, this.app);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
        this.app = PacketSerializeUtil.readApp(buffer);
    }
}
