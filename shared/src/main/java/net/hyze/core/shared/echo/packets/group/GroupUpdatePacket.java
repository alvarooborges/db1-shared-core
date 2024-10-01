package net.hyze.core.shared.echo.packets.group;

import com.google.common.base.Enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdatePacket extends EchoPacket {

    private User user;
    private User userTarget;
    private Group group;
    private GroupUpdateAction action;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
        PacketSerializeUtil.writeUser(buffer, this.userTarget);
        buffer.writeString(this.group.name());
        buffer.writeString(this.action.name());
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
        this.userTarget = PacketSerializeUtil.readUser(buffer);
        this.group = Enums.getIfPresent(Group.class, buffer.readString()).get();
        this.action = Enums.getIfPresent(GroupUpdateAction.class, buffer.readString()).get();
    }

}
