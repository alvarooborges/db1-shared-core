package net.hyze.core.spigot.misc.utils;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerNMS {

    public static void openBook(Player player, ItemStack book) {

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, book);

        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte) 0);
        buf.writerIndex(1);

        PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(buf));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        player.getInventory().setItem(slot, old);

    }

    public static void setFlying(Player player, boolean value) {
        EntityPlayer nmsPlayer = NMS.getPlayer(player);

        boolean needsUpdate = nmsPlayer.abilities.isFlying != value;
        if (!player.getAllowFlight() && value) {
            throw new IllegalArgumentException("Cannot make player fly if getAllowFlight() is false");
        }

        if (needsUpdate) {
            nmsPlayer.abilities.isFlying = value;
            nmsPlayer.updateAbilities();
        }
    }

    public static Player loadOfflinePlayer(UUID uuid) {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (player == null || !player.hasPlayedBefore()) {
                return null;
            }

            GameProfile profile = new GameProfile(uuid, player.getName());
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), profile, new PlayerInteractManager(server.getWorldServer(0)));

            Player target = entity.getBukkitEntity();
            if (target != null) {
                target.loadData();
                return target;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}