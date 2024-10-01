package net.hyze.core.spigot.misc.shop.module.currency;

import com.google.common.collect.Maps;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.shop.module.AbstractModule.State;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

class CurrencyConfirmInventory extends CustomInventory {

    private static final Map<Integer, Integer[]> SLOTS = Maps.newHashMap();

    static {
        SLOTS.put(1, new Integer[]{
            13
        });

        SLOTS.put(2, new Integer[]{
            12, 14
        });

        SLOTS.put(3, new Integer[]{
            11, 14, 15
        });

        SLOTS.put(4, new Integer[]{
            10, 12, 14, 16
        });
    }

    public CurrencyConfirmInventory(CurrencyModule module, Runnable callback, Function<User, Inventory> mainInventory, User user) {
        super(36, "Qual moeda deseja usar?");

        Integer[] slots = SLOTS.get(module.getPrices().size());

        if (slots == null) {
            // TODO mostrar erro
            return;
        }

        AtomicInteger index = new AtomicInteger();

        module.getPrices().forEach(price -> {
            ItemBuilder icon = price.buildIcon(user);

            setItem(slots[index.getAndIncrement()], icon.make(), event -> {
                Player player = (Player) event.getWhoClicked();

                if (price.state(user) == State.SUCCESS) {
                    if (price.transaction(user)) {
                        callback.run();
                        return;
                    }
                }

                Message.ERROR.send(player, "Algo de errado aconteceu.");
                player.closeInventory();
            });
        });

        backItem((InventoryClickEvent event) -> {
            event.getWhoClicked().openInventory(mainInventory.apply(user));
        });
    }
}
