package net.hyze.core.spigot.misc.enchantments.merchant;

import io.netty.buffer.Unpooled;
import java.lang.reflect.Field;
import net.hyze.core.spigot.misc.enchantments.merchant.events.MerchantTradeEvent;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.ContainerMerchant;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IMerchant;
import net.minecraft.server.v1_8_R3.MerchantRecipe;
import net.minecraft.server.v1_8_R3.MerchantRecipeList;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Merchant implements IMerchant, Listener {

    private final String name;
    private EntityHuman human;

    private static final ItemStack DEFAULT_RESULT_ITEM = new ItemBuilder(Material.BARRIER)
            .clearFlags()
            .name("&cNenhum resultado")
            .make();

    private static final ItemStack DEFAULT_SECOND_ITEM = new ItemBuilder(Material.MAP)
            .clearFlags()
            .name("&aMaterial")
            .lore("&7Cloque um item no segundo slot", "&7para que ele seja fundido ao", "&7primeiro item!")
            .make();

    public Merchant(String name, EntityHuman human, JavaPlugin plugin) {
        this.name = name;
        this.human = human;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void a_(EntityHuman human) {
        this.human = human;
        if (this.human == null) {
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    public EntityHuman v_() {
        return human;
    }

    @Override
    public MerchantRecipeList getOffers(EntityHuman entityHuman) {
//        System.out.println("getOffers");

        Inventory inventory = entityHuman.activeContainer.getBukkitView().getTopInventory();

        ItemStack first = inventory.getItem(0);
        ItemStack second = inventory.getItem(1);

        MerchantRecipeList list = new MerchantRecipeList();

        if (first != null) {
            if (second == null) {
                list.add(new MerchantRecipe(
                        CraftItemStack.asNMSCopy(first),
                        CraftItemStack.asNMSCopy(DEFAULT_SECOND_ITEM),
                        CraftItemStack.asNMSCopy(DEFAULT_RESULT_ITEM)
                ));
            } else {
                MerchantRecipeEvent event = new MerchantRecipeEvent((Player) entityHuman.getBukkitEntity(), first, second);

                Bukkit.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled() || event.getResult() == null) {
                    list.add(new MerchantRecipe(
                            CraftItemStack.asNMSCopy(first),
                            CraftItemStack.asNMSCopy(DEFAULT_SECOND_ITEM),
                            CraftItemStack.asNMSCopy(DEFAULT_RESULT_ITEM)
                    ));
                } else {
                    ItemStack second0 = second.clone();
                    second0.setAmount(1);
                    list.add(new MerchantRecipe(
                            CraftItemStack.asNMSCopy(first),
                            CraftItemStack.asNMSCopy(second0),
                            CraftItemStack.asNMSCopy(event.getResult())
                    ));
                }
            }
        }

        return list;
    }

    @Override
    public void a(MerchantRecipe mr) {
//        System.out.println("a MerchantRecipe mr");
    }

    @Override
    public void a_(net.minecraft.server.v1_8_R3.ItemStack is) {
//        System.out.println("a_ " + is);
        MerchantRecipeList list = getOffers(human);
        updateOffers(list);
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(name);
    }

    public void updateOffers(MerchantRecipeList list) {
//        System.out.println("updateOffers");

        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

        packetdataserializer.writeInt(human.activeContainer.windowId);
        list.a(packetdataserializer);
        ((EntityPlayer) human).playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", packetdataserializer));

        ((EntityPlayer) human).updateInventory(human.activeContainer);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent event) {
        EntityPlayer entityPlayer = ((CraftPlayer) event.getWhoClicked()).getHandle();

        if (!(entityPlayer.activeContainer instanceof ContainerMerchant)) {
            return;
        }

        try {
            Field field = entityPlayer.activeContainer.getClass().getSuperclass().getDeclaredField("merchant");
            field.setAccessible(true);
            IMerchant merchant = (IMerchant) field.get(entityPlayer.activeContainer);

            if (merchant != this) {
                return;
            }

            Inventory inventory = event.getView().getTopInventory();

            ItemStack result = inventory.getItem(2);

            if (event.getRawSlot() == 2 && (result == null || result.getType().equals(Material.BARRIER))) {
                event.setCancelled(true);
                return;
            }

            if (event.getRawSlot() == 2) {
                Bukkit.getPluginManager().callEvent(new MerchantTradeEvent((Player) event.getWhoClicked(), result));
            }

        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
            event.setCancelled(true);
        }
    }
}
