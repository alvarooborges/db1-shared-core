package net.hyze.core.shared.echo.packets.app;

import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppStartedPacket extends EchoPacket {

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
