package net.hyze.core.shared.echo.packets.config;

import com.google.common.collect.Maps;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigReloadPacket extends EchoPacket {

    private Map<String, String> config;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.config.size());
        this.config.entrySet().forEach(entry -> {
            PacketSerializeUtil.writeString(buffer, entry.getKey());
            PacketSerializeUtil.writeString(buffer, entry.getValue());
        });
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.config = Maps.newHashMap();

        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            this.config.put(PacketSerializeUtil.readString(buffer), PacketSerializeUtil.readString(buffer));
        }
    }

}
