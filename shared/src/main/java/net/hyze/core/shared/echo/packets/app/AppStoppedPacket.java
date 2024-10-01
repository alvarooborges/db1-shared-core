package net.hyze.core.shared.echo.packets.app;

import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.echo.api.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppStoppedPacket extends EchoPacket {

    private App app;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeApp(buffer, this.app);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.app = PacketSerializeUtil.readApp(buffer);
    }

}
