package net.hyze.core.shared.echo.packets.party.leave;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.ServerPacket;
import net.hyze.core.shared.echo.packets.party.answer.AbstractPartyAnswerPacket;
import net.hyze.core.shared.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@ServerPacket
@NoArgsConstructor
public class RPartyLeavePacket extends AbstractPartyLeavePacket {

    @Getter
    private Set<Integer> members = Sets.newHashSet();

    @Getter
    private User newLeader;

    public RPartyLeavePacket(User leader, User target, Integer partyId, LeaveReason reason, User newLeader) {
        super(leader, target, partyId, reason);

        this.newLeader = newLeader;
    }

    @Override
    public void read(EchoBufferInput buffer) {
        super.read(buffer);

        int j = buffer.readInt();
        for(int i = 0; i < j; i++) {
            members.add(buffer.readInt());
        }

        this.newLeader = PacketSerializeUtil.readUser(buffer);
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        super.write(buffer);

        buffer.writeInt(members.size());
        for(int m : members) {
            buffer.writeInt(m);
        }

        PacketSerializeUtil.writeUser(buffer, newLeader);
    }
}
