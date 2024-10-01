package net.hyze.core.spigot.misc.enchantments.merchant;

import net.hyze.core.spigot.CoreSpigotPlugin;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Field;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.MerchantRecipeList;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;

public class MerchantUtil {

    public static void openTrade(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        Merchant imerchant = new Merchant("Bigorna", entityPlayer, CoreSpigotPlugin.getInstance());

        Container container = CraftEventFactory.callInventoryOpenEvent(entityPlayer,
                new ContainerMerchant(entityPlayer.inventory, imerchant, entityPlayer.world)
        );

        if (container == null) {
            return;
        }

        // CraftBukkit end
        entityPlayer.nextContainerCounter();
        entityPlayer.activeContainer = container; // CraftBukkit

        int containerCounter = getContainerCounter(entityPlayer);

        entityPlayer.activeContainer.windowId = containerCounter;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
        net.minecraft.server.v1_8_R3.InventoryMerchant inventorymerchant = ((net.minecraft.server.v1_8_R3.ContainerMerchant) entityPlayer.activeContainer).e();
        IChatBaseComponent ichatbasecomponent = imerchant.getScoreboardDisplayName();

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, "minecraft:villager", ichatbasecomponent, inventorymerchant.getSize()));
        MerchantRecipeList merchantrecipelist = imerchant.getOffers(entityPlayer);

        if (merchantrecipelist != null) {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

            packetdataserializer.writeInt(containerCounter);
            merchantrecipelist.a(packetdataserializer);
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", packetdataserializer));
        }
    }

    private static int getContainerCounter(EntityPlayer entityPlayer) {
        try {
            Field field = entityPlayer.getClass().getDeclaredField("containerCounter");
            field.setAccessible(true);
            Integer containerCounter = (Integer) field.get(entityPlayer);

            return containerCounter;
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return -1;
    }
}
