package net.hyze.core.spigot.client;

import io.netty.buffer.Unpooled;
import net.hyze.client.protocol.AbstractProtocolHandler;
import net.hyze.client.protocol.ProtocolReference;
import net.hyze.client.protocol.api.AbstractPacket;
import net.hyze.client.protocol.api.BufferInput;
import net.hyze.client.protocol.api.BufferOutput;
import net.hyze.client.protocol.packets.client.PacketResponseScreenshot;
import net.hyze.core.spigot.commands.impl.AdminCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ProtocolHandler extends AbstractProtocolHandler<Player> implements PluginMessageListener {

    @Override
    public void callPacket(AbstractPacket ap) {
        // TODO

        if(ap instanceof PacketResponseScreenshot) {
            PacketResponseScreenshot packet = (PacketResponseScreenshot) ap;

            UUID id = packet.getId();
            BufferedImage image = packet.getImage();

            File file = new File("screenshots");
            file.mkdir();

            Player sender = AdminCommand.SCREENSHOT_CACHE.remove(id);

            try {
                ImageIO.write(image, "jpg", new File(file, id.toString()));

                if(sender != null) {
                    Message.SUCCESS.send(sender, "Sucesso. ID: " + id.toString());
                }
            } catch(IOException ex) {
                ex.printStackTrace();
                Message.ERROR.send(sender, "Ocorreu um erro.");
            }
        }
    }

    @Override
    public void sendPacket(Player player, BufferOutput output) {
        PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(ProtocolReference.CHANNEL, new PacketDataSerializer(Unpooled.wrappedBuffer(output.toByteArray())));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase(ProtocolReference.CHANNEL)) {
            return;
        }

        receivePacket(new BufferInput(message));
    }
}
