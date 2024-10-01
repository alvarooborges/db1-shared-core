package net.hyze.core.shared.echo.packets.party.answer;

import com.google.common.collect.Maps;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPartyAnswerPacket extends EchoPacket {

    @Getter
    private User requester;

    @Getter
    private User target;

    @Getter
    private Integer partyId, targetPartyId;

    @Getter
    private EnumAnswer answer;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.requester);
        PacketSerializeUtil.writeUser(buffer, this.target);
        buffer.writeInt(partyId);
        buffer.writeInt(targetPartyId);
        buffer.writeInt(answer.index);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.requester = PacketSerializeUtil.readUser(buffer);
        this.target = PacketSerializeUtil.readUser(buffer);
        this.partyId = buffer.readInt();
        this.targetPartyId = buffer.readInt();
        this.answer = EnumAnswer.FROM_ID.getOrDefault(buffer.readInt(), EnumAnswer.REJECTED);
    }

    @RequiredArgsConstructor
    public static enum EnumAnswer {

        REJECTED(0, "&c%s negou o convite para entrar na sua party.", "&cVocê negou o convite de party de %s."),
        ACCEPTED(1, "&6%s &aentrou na sua party!", "&aVocê entrou na party de &6%s&a!"),
        IGNORED(2, "&c%s ignorou o convite para entrar na sua party.", "&cVocê ignorou o pedido de party de %s.");

        private final Integer index;

        @Getter
        private final String receiverMessage, transmitterMessage;

        private static final Map<Integer, EnumAnswer> FROM_ID = Maps.newHashMap();
        static {
            for(EnumAnswer response : values()) {
                FROM_ID.put(response.index, response);
            }
        }

    }

}
