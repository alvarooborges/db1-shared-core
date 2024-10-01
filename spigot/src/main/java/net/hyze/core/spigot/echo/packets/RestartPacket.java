package net.hyze.core.spigot.echo.packets;

import com.google.common.collect.Sets;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestartPacket extends EchoPacket {

    private int seconds;
    private Set<String> apps = Sets.newHashSet();

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.seconds);

        buffer.writeInt(this.apps.size());
        this.apps.stream().forEach(m -> buffer.writeString(m));

    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.seconds = buffer.readInt();

        int j = buffer.readInt();

        for (int i = 0; i < j; i++) {
            this.apps.add(buffer.readString());
        }

    }

}
