package net.hyze.core.shared.echo.packets;

import com.google.common.collect.Sets;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendTitlePacket extends EchoPacket {

    private String title = ChatColor.RESET.toString();
    private String subTitle = ChatColor.RESET.toString();
    private int fadeIn = 0;
    private int fadeOut = 1;
    private int stay = 0;

    protected Set<User> users = Sets.newHashSet();
    protected Server server = null;

    public static SendTitlePacketBuilder builder() {
        return new SendTitlePacketBuilder();
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeString(buffer, title);
        PacketSerializeUtil.writeString(buffer, subTitle);
        buffer.writeInt(fadeIn);
        buffer.writeInt(fadeOut);
        buffer.writeInt(stay);

        buffer.writeInt(Optional.ofNullable(this.users).map(Set::size).orElse(0));

        if (this.users != null) {
            this.users.forEach(user -> {
                PacketSerializeUtil.writeUser(buffer, user);
            });
        }

        PacketSerializeUtil.writeEnum(buffer, server);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        title = PacketSerializeUtil.readString(buffer);
        subTitle = PacketSerializeUtil.readString(buffer);
        fadeIn = buffer.readInt();
        fadeOut = buffer.readInt();
        stay = buffer.readInt();

        this.users = Sets.newHashSet();

        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            User user = PacketSerializeUtil.readUser(buffer);

            if (user != null) {
                this.users.add(user);
            }
        }

        server = PacketSerializeUtil.readEnum(buffer, Server.class);
    }

    public static class SendTitlePacketBuilder {

        private String title = ChatColor.RESET.toString();
        private String subTitle = ChatColor.RESET.toString();
        private int fadeIn = 0;
        private int fadeOut = 1;
        private int stay = 0;

        protected Set<User> users = Sets.newHashSet();
        protected Server server = null;

        public SendTitlePacketBuilder title(String title) {
            this.title = title;
            return this;
        }

        public SendTitlePacketBuilder subTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public SendTitlePacketBuilder fadeIn(int fadeIn) {
            this.fadeIn = fadeIn;
            return this;
        }

        public SendTitlePacketBuilder fadeOut(int fadeOut) {
            this.fadeOut = fadeOut;
            return this;
        }

        public SendTitlePacketBuilder stay(int stay) {
            this.stay = stay;
            return this;
        }

        public SendTitlePacketBuilder user(Set<User> users) {
            this.users = users;
            return this;
        }

        public SendTitlePacketBuilder server(Server server) {
            this.server = server;
            return this;
        }

        public SendTitlePacket build() {
            return new SendTitlePacket(title, subTitle, fadeIn, fadeOut, stay, users, server);
        }
    }
}
