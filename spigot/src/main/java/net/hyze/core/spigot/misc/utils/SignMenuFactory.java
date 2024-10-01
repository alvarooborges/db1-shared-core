package net.hyze.core.spigot.misc.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SignMenuFactory {

    private static final int ACTION_INDEX = 9;
    private static final int SIGN_LINES = 4;
    private static Material SIGN = null;

    private static final String NBT_FORMAT = "{\"text\":\"%s\"}";
    private static final String NBT_BLOCK_ID = "minecraft:sign";

    private final Plugin plugin;

    private final Map<Player, Menu> inputReceivers;
    private final Map<Player, BlockPosition> signLocations;

    public SignMenuFactory(Plugin plugin) {
        this.plugin = plugin;
        this.inputReceivers = new HashMap<>();
        this.signLocations = new HashMap<>();
        this.listen();
        try {
            SIGN = Material.WALL_SIGN;
        } catch (NoSuchFieldError e) {
            SIGN = Material.matchMaterial("WALL_SIGN");
        }
    }

    public Menu newMenu(Player player, String[] defaultLines) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(defaultLines, "text");

        Menu menu = new Menu(player, defaultLines);
        menu.onOpen(blockPosition -> {
            this.signLocations.put(player, blockPosition);
            this.inputReceivers.putIfAbsent(player, menu);
        });
        return menu;
    }

    private void listen() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                WrappedChatComponent[] lines = packet.getChatComponentArrays().read(0);

                String[] input = new String[4];

                for (int i = 0; i < 4; i++) {
                    input[i] = lines[i].getJson().replace("\"", "");
                }

                Menu menu = inputReceivers.remove(player);
                BlockPosition blockPosition = signLocations.remove(player);

                if (menu == null || blockPosition == null) {
                    return;
                }
                event.setCancelled(true);

                if (menu.response != null) {
                    menu.response.accept(player, input);
                }

                player.sendBlockChange(blockPosition.toLocation(player.getWorld()), Material.AIR, (byte) 0);
            }
        });
    }

    public static final class Menu {

        private final Player player;

        private final String[] lines;
        private BiConsumer<Player, String[]> response;

        private Consumer<BlockPosition> onOpen;

        Menu(Player player, String[] lines) {
            this.player = player;
            this.lines = lines;
        }

        void onOpen(Consumer<BlockPosition> onOpen) {
            this.onOpen = onOpen;
        }

        public Menu response(BiConsumer<Player, String[]> response) {
            this.response = response;
            return this;
        }

        public void open() {
            Location location = this.player.getLocation();
            BlockPosition blockPosition = new BlockPosition(location.getBlockX(), 0, location.getBlockZ());

            player.sendBlockChange(blockPosition.toLocation(location.getWorld()), SIGN, (byte) 0);

            PacketContainer updateSing = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.UPDATE_SIGN);

            WrappedChatComponent[] value = new WrappedChatComponent[4];

            for (int i = 0; i < 4; i++) {
                value[i] = WrappedChatComponent.fromText(lines[i]);
            }

            updateSing.getBlockPositionModifier().write(0, blockPosition);
            updateSing.getChatComponentArrays().write(0, value);

            PacketContainer openSign = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
            openSign.getBlockPositionModifier().write(0, blockPosition);

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, updateSing);
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);
            } catch (InvocationTargetException exception) {
                exception.printStackTrace();
            }
            this.onOpen.accept(blockPosition);
        }
    }
}
