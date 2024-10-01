package net.hyze.core.shared.echo.packets.dungeon.request;

import com.google.common.collect.Sets;
import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@NoArgsConstructor
@ServerPacket
@ExternalPacket(channel="dungeon")
@AllArgsConstructor
public class DungeonRequestPacket extends EchoPacket implements Respondable<DungeonRequestPacket.RequestCallback> {

    @Getter
    private User leader;

    @Getter
    private Collection<User> members;

    @Getter
    private String mapId;

    @Setter
    @Getter
    private RequestCallback response;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.leader);
        buffer.writeInt(members.size());

        for(User user : members) {
            PacketSerializeUtil.writeUser(buffer, user);
        }

        buffer.writeString(this.mapId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.leader = PacketSerializeUtil.readUser(buffer);
        this.members = Sets.newHashSet();

        int size = buffer.readInt();
        for(int i = 0; i < size; i++) {
            this.members.add(PacketSerializeUtil.readUser(buffer));
        }

        this.mapId = buffer.readString();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestCallback extends Response {

        @Getter
        private boolean success;

        @Getter
        private String dungeon;

        @Override
        public void write(EchoBufferOutput buffer) {
            buffer.writeBoolean(success);
            buffer.writeString(dungeon);
        }

        @Override
        public void read(EchoBufferInput buffer) {
            this.success = buffer.readBoolean();
            this.dungeon = buffer.readString();
        }
    }

}
