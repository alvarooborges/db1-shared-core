package net.hyze.core.spigot.misc.utils;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.StreamSerializer;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.google.common.base.Charsets;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.spigot.CoreSpigotPlugin;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomPayloadFixer {

    public static final Map<Player, Long> PACKET_USAGE = new ConcurrentHashMap<>();
    public static final Map<String, Long> PACKET_USAGE_PLACE = new ConcurrentHashMap<>();

    public static void checkCustomPayload(PacketEvent event) {
        Player player = event.getPlayer();
        long lastPacket = PACKET_USAGE.getOrDefault(player, -1L);

        // This fucker is already detected as an exploiter
        if (lastPacket == -2L) {
            event.setCancelled(true);
            return;
        }

        String name = event.getPacket().getStrings().readSafely(0);
        if (!"MC|BSign".equals(name) && !"MC|BEdit".equals(name) && !"REGISTER".equals(name)) {
            return;
        }

        try {
            if ("REGISTER".equals(name)) {
                checkChannels(event);
            } else {
                if (elapsed(lastPacket, 100L)) {
                    PACKET_USAGE.put(player, System.currentTimeMillis());
                } else {
                    throw new IOException("Packet flood");
                }

                checkNbtTags(event);
            }
        } catch (Throwable ex) {
            // Set last packet usage to -2 so we wouldn't mind checking him again
            PACKET_USAGE.put(player, -2L);
            CoreSpigotPlugin.getInstance().getLogger().warning(player.getName() + " tried to exploit CustomPayload packet");
            event.setCancelled(true);
        }
    }

    private static void checkNbtTags(PacketEvent event) throws IOException {
        PacketContainer container = event.getPacket();
        ByteBuf buffer = container.getSpecificModifier(ByteBuf.class).read(0).copy();

        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);

        DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes));
        ItemStack itemStack = StreamSerializer.getDefault().deserializeItemStack(input);

        try {
            if (itemStack == null) {
                throw new IOException("Unable to deserialize ItemStack");
            }

            NbtCompound root = (NbtCompound) NbtFactory.fromItemTag(itemStack);
            if (root == null) {
                throw new IOException("No NBT tag?!");
            } else if (!root.containsKey("pages")) {
                throw new IOException("No 'pages' NBT compound was found");
            } else {
                NbtList<String> pages = root.getList("pages");
                if (pages.size() > 50) {
                    throw new IOException("Too much pages");
                }

                // Here comes the funny part - Minecraft Wiki says that book allows to have only 256 symbols per page,
                // but in reality it actually can get up to 257. What a jerks. (tested on 1.8.9)
                for (String page : pages) {
                    if (page.length() > 257) {
                        throw new IOException("A very long page");
                    }
                }
            }
        } finally {
            input.close();
            buffer.release();
        }
    }

    private static void checkChannels(PacketEvent event) throws Exception {
        int channelsSize = event.getPlayer().getListeningPluginChannels().size();

        PacketContainer container = event.getPacket();
        ByteBuf buffer = container.getSpecificModifier(ByteBuf.class).read(0).copy();

        try {
            for (int i = 0; i < buffer.toString(Charsets.UTF_8).split("\0").length; i++) {
                if (++channelsSize > 124) {
                    throw new IOException("Too much channels");
                }
            }
        } finally {
            buffer.release();
        }
    }

    private static boolean elapsed(long from, long required) {
        return from == -1L || System.currentTimeMillis() - from > required;
    }

    public static void checkPlace(PacketEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        long lastPacket = PACKET_USAGE_PLACE.getOrDefault(player.getName(), -1L);
        // This fucker is already detected as an exploiter
        if (lastPacket == -2L) {
            event.setCancelled(true);
            return;
        }

        if (elapsed(lastPacket, 11L)) {
            PACKET_USAGE.put(player, System.currentTimeMillis());
        } else {
            event.setCancelled(true);
            return;
        }

        if (!player.isOnline()) {
            event.setCancelled(true);
            return;
        }

        try {
            ItemStack itemStack = null;
            try {
                itemStack = (ItemStack) event.getPacket().getItemModifier().readSafely(0);
            } catch (Exception ex) {
                throw new IOException(ex.getMessage());
            }

            if (itemStack == null) {
                return;
            }

            NbtCompound root;
            try {
                root = (NbtCompound) NbtFactory.fromItemTag(itemStack);
                if (root == null) {
                    return;
                }
            } catch (Exception e) {
                return;
            }

            if (!root.containsKey("pages")) {
                return;
            }

            if (root.containsKey("title") && root.getString("title").equals("Play with me.")) {
                throw new IOException("Usando o Crasher.jar");
            }

            //Livros "feitos" por nós são ignorados
            if (root.containsKey("author") && root.getString("author").equals("Rede Sky")) {
                return;
            }

            NbtList pages = root.getList("pages");

            if (pages.size() > 50) {
                throw new IOException("Too much pages");
            }

            for (Iterator localIterator = pages.iterator(); localIterator.hasNext();) {
                Object page = localIterator.next();
                if (((page instanceof String))
                        && (((String) page).length() > 257)) {
                    throw new IOException("Too much chars");
                }
            }
        } catch (Exception e) {
            PACKET_USAGE_PLACE.put(player.getName(), -2L);
            Printer.DANGER.coloredPrint("\n\n    &e" + e.getMessage() + "\n    &e" + player.getName() + " tried to exploit BlockPlace packet\n");
            event.getPacket().getItemModifier().write(0, null);
            event.setCancelled(true);
        }
    }
}
