package net.hyze.core.shared.echo.packets.user;

import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserTellPacket extends EchoPacket {

    private User sender;
    private User target;
    private String message;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.sender);
        PacketSerializeUtil.writeUser(buffer, this.target);
        PacketSerializeUtil.writeString(buffer, this.message);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.sender = PacketSerializeUtil.readUser(buffer);
        this.target = PacketSerializeUtil.readUser(buffer);
        this.message = PacketSerializeUtil.readString(buffer);
    }
}
