package net.hyze.core.shared.echo.packets.user.connect;

import lombok.*;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.user.User;

import javax.annotation.Nullable;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserConnectHandShakeErrorPacket extends EchoPacket {

    @NonNull
    private User user;

    @Nullable
    private String appId;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
        PacketSerializeUtil.writeString(buffer, this.appId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
        this.appId = PacketSerializeUtil.readString(buffer);
    }
}
