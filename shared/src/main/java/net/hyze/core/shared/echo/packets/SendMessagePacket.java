package net.hyze.core.shared.echo.packets;

import net.hyze.core.shared.echo.api.EchoPacket;
import com.google.common.collect.Sets;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.user.User;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessagePacket extends EchoPacket {

    protected Set<User> users;
    protected BaseComponent[] components;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(Optional.ofNullable(this.users).map(Set::size).orElse(0));

        if (this.users != null) {
            this.users.forEach(user -> {
                PacketSerializeUtil.writeUser(buffer, user);
            });
        }
        buffer.writeString(TextComponent.toLegacyText(this.components));
    }

    @Override
    public void read(EchoBufferInput buffer) {

        this.users = Sets.newHashSet();

        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            User user = PacketSerializeUtil.readUser(buffer);

            if (user != null) {
                this.users.add(user);
            }
        }

        this.components = TextComponent.fromLegacyText(buffer.readString());
    }
}
