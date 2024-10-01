package net.hyze.core.spigot.misc.enchantments;

import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.customitem.IllegalRegistryException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

public class CustomEnchantmentRegistry {

    private final static Map<String, CustomEnchantment> ENCHANTMENTS = new HashMap<>();
    private final static Map<String, EventBus> BUS = new HashMap<>();

    public static void registerCustomEnchantment(CustomEnchantment... enchantments) {
        for (CustomEnchantment enchantment : enchantments) {
            if (isRegistered(enchantment)) {
                throw new IllegalRegistryException("O encantamento " + enchantment.getKey() + " já está registrado.");
            }

            EventBus bus = EventBus.builder()
                    .logNoSubscriberMessages(false)
                    .throwSubscriberException(true)
                    .build();

            try {
                bus.register(enchantment);
            } catch (EventBusException exception) {

            }

            CoreSpigotPlugin.getInstance().getServer().getPluginManager().registerEvents(enchantment, CoreSpigotPlugin.getInstance());

            BUS.put(enchantment.getKey(), bus);
            ENCHANTMENTS.put(enchantment.getKey(), enchantment);
        }
    }

    public static EventBus getEventBus(CustomEnchantment enchantment) {
        return BUS.get(enchantment.getKey());
    }

    public static void unregisterCustomEnchantment(CustomEnchantment... enchantments) {
        for (CustomEnchantment enchantment : enchantments) {
            if (isRegistered(enchantment)) {
                ENCHANTMENTS.remove(enchantment.getKey(), enchantment);
            }
        }
    }

    public static boolean isRegistered(CustomEnchantment enchantment) {
        return enchantment != null && get(enchantment.getKey()) != null;
    }

    public static CustomEnchantment get(String key) {
        return ENCHANTMENTS.get(key);
    }

    public static Collection<CustomEnchantment> getItems() {
        return ENCHANTMENTS.values();
    }
}
