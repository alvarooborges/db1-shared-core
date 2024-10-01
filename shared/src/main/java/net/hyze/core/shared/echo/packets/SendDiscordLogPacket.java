package net.hyze.core.shared.echo.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendDiscordLogPacket extends EchoPacket {

    private String message;
    private String channel;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeString(buffer, message);
        PacketSerializeUtil.writeString(buffer, channel);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        message = PacketSerializeUtil.readString(buffer);
        channel = PacketSerializeUtil.readString(buffer);
    }
}
