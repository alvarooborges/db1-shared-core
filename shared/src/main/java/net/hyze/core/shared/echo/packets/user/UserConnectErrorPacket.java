package net.hyze.core.shared.echo.packets.user;

import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.user.User;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserConnectErrorPacket extends EchoPacket {

    @NonNull
    private User user;

    @Nullable
    private App toApp;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
        PacketSerializeUtil.writeApp(buffer, this.toApp);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
        this.toApp = PacketSerializeUtil.readApp(buffer);
    }
}
