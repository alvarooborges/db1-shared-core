package net.hyze.core.spigot.misc.customitem;

import net.hyze.core.spigot.misc.utils.ItemBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

public class CustomItemRegistry {

    private final static Map<String, CustomItem> ITEMS = new HashMap<>();
    private final static Map<String, EventBus> BUS = new HashMap<>();

    public static final String NBT_KEY = "custom_item_key";

    public static void registerCustomItem(CustomItem... items) {
        for (CustomItem item : items) {
            if (isRegistered(item)) {
                throw new IllegalRegistryException("O item " + item.getKey() + " já está registrado.");
            }

            EventBus bus = EventBus.builder()
                    .logNoSubscriberMessages(false)
                    .throwSubscriberException(true)
                    .build();

            try {
                bus.register(item);
            } catch (EventBusException exception) {

            }

            ITEMS.put(item.getKey(), item);
            BUS.put(item.getKey(), bus);
        }
    }

    public static EventBus getEventBus(CustomItem item) {
        return BUS.get(item.getKey());
    }

    public static boolean isRegistered(CustomItem item) {
        return item != null && getItem(item.getKey()) != null;
    }

    public static CustomItem getItem(String id) {
        return CustomItemRegistry.ITEMS.get(id);
    }

    public static Collection<CustomItem> getItems() {
        return CustomItemRegistry.ITEMS.values();
    }

    public static CustomItem getByItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        ItemBuilder itemBuilder = ItemBuilder.of(itemStack);

        return CustomItemRegistry.getItem(itemBuilder.nbtString(CustomItemRegistry.NBT_KEY));
    }

    public static boolean isCustomItem(ItemStack itemStack) {
        return itemStack != null && getByItemStack(itemStack) != null;
    }
}
