package net.hyze.core.shared.echo.packets.party.leave;

import com.google.common.collect.Maps;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPartyLeavePacket extends EchoPacket {

    @Getter
    private User leader;

    @Getter
    private User target;

    @Getter
    private Integer partyId;

    @Getter
    private LeaveReason reason;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.leader);
        PacketSerializeUtil.writeUser(buffer, this.target);
        buffer.writeInt(partyId);
        buffer.writeInt(reason.index);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.leader = PacketSerializeUtil.readUser(buffer);
        this.target = PacketSerializeUtil.readUser(buffer);
        this.partyId = buffer.readInt();
        this.reason = LeaveReason.FROM_ID.getOrDefault(buffer.readInt(), LeaveReason.LEFT);
    }

    @RequiredArgsConstructor
    public static enum LeaveReason {
        LEFT(0, "&c%s saiu da party."),
        KICKED(1, "&c%s foi expulso da party."),
        INACTIVE(2, "&c%s foi expulso da party por inatividade.");

        private final int index;

        @Getter
        private final String message;

        private static final Map<Integer, LeaveReason> FROM_ID = Maps.newHashMap();
        static {
            for(LeaveReason reason : values()) {
                FROM_ID.put(reason.index, reason);
            }
        }
    }

}
