package net.hyze.core.spigot.commands.impl.restart;

import com.google.common.collect.Sets;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.spigot.echo.packets.RestartPacket;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.CustomSound;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RestartInventory extends CustomInventory {

    private static final Set<String> SELECTED_APPS = Sets.newHashSet();

    private static final Integer[] SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
    };

    public RestartInventory() {
        super(54, "Selecione os Apps");

        AtomicInteger appSlot = new AtomicInteger();

        CoreProvider.Cache.Local.APPS.provide().get().stream()
                .filter(app -> Objects.equals(app.getServer(), CoreProvider.getApp().getServer()))
                .filter(app -> CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(app.getId(), AppStatus.class) != null)
                .forEach(app -> {
                    boolean same = CoreProvider.getApp().getId().equalsIgnoreCase(app.getId());
                    update(SLOTS[appSlot.getAndIncrement()], same, app);
                });

        AtomicInteger menuSlot = new AtomicInteger(48);

        BiConsumer<Integer, Integer> menu = (seconds, durability) -> {
            setItem(
                    menuSlot.getAndIncrement(),
                    ItemBuilder.of(Material.STAINED_GLASS_PANE)
                    .durability(durability)
                    .name(String.format("&e%s segundos", seconds))
                    .lore(
                            "&7Clique para reiniciar o(s)",
                            String.format("servidor(es) em %s segundos.", seconds)
                    )
                    .make(),
                    event -> {

                        if (SELECTED_APPS.isEmpty()) {
                            CustomSound.BAD.play((Player) event.getWhoClicked());
                            return;
                        }

                        ConfirmInventory confirmInventory = ConfirmInventory.of(
                                confirmEvent -> {
                                    CoreProvider.Redis.ECHO.provide().publish(
                                            new RestartPacket(seconds, SELECTED_APPS)
                                    );

                                    Message.SUCCESS.send(confirmEvent.getWhoClicked(), "Pacote de reinicio enviado! :D");
                                },
                                denyEvent -> {
                                    Message.ERROR.send(denyEvent.getWhoClicked(), "Pronto, cancelado. :D");
                                },
                                null
                        );

                        event.getWhoClicked().openInventory(confirmInventory.make("Tem certeza?"));
                    }
            );
        };

        menu.accept(60, 4);
        menu.accept(30, 5);
        menu.accept(15, 14);

    }

    public void update(int slot, boolean same, App app) {
        setItem(
                slot,
                ItemBuilder.of(Material.STAINED_CLAY)
                .name((same ? "&a" : "&7") + app.getDisplayName())
                .lore("&8" + app.getId())
                .durability(same ? 5 : 9)
                .glowing(SELECTED_APPS.contains(app.getId()))
                .make(),
                event -> {

                    if (SELECTED_APPS.contains(app.getId())) {
                        SELECTED_APPS.remove(app.getId());
                    } else {
                        SELECTED_APPS.add(app.getId());
                    }

                    CustomSound.GOOD.play((Player) event.getWhoClicked());
                    update(slot, same, app);

                }
        );
    }

}
