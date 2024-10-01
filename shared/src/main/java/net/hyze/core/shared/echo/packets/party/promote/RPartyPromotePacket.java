package net.hyze.core.shared.echo.packets.party.promote;

import com.google.common.collect.Sets;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.ServerPacket;
import net.hyze.core.shared.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@ServerPacket
@NoArgsConstructor
public class RPartyPromotePacket extends AbstractPartyPromotePacket {

    @Getter
    private Set<Integer> members = Sets.newHashSet();

    public RPartyPromotePacket(User leader, Integer partyId) {
        super(leader, partyId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        super.read(buffer);

        int j = buffer.readInt();
        for(int i = 0; i < j; i++) {
            members.add(buffer.readInt());
        }
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        super.write(buffer);

        buffer.writeInt(members.size());
        for(int m : members) {
            buffer.writeInt(m);
        }
    }

}
