package net.hyze.core.shared.echo.packets;

import com.google.common.collect.Sets;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.servers.Server;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BroadcastMessagePacket extends EchoPacket {

    protected BaseComponent[] components;
    protected Set<Group> groups;
    protected boolean groupStrict;
    protected Server server;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeString(ComponentSerializer.toString(this.components));

        buffer.writeInt(groups.size());

        for (Group group : groups) {
            PacketSerializeUtil.writeEnum(buffer, group);
        }

        buffer.writeBoolean(groupStrict);
        PacketSerializeUtil.writeServer(buffer, server);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.components = ComponentSerializer.parse(buffer.readString());

        this.groups = Sets.newHashSet();

        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            Group group = PacketSerializeUtil.readEnum(buffer, Group.class);

            if (group != null) {
                groups.add(group);
            }
        }

        this.groupStrict = buffer.readBoolean();
        this.server = PacketSerializeUtil.readServer(buffer);
    }
    
    public static class BroadcastMessagePacketBuilder {
        private Set<Group> groups = Sets.newHashSet();
    }
}
