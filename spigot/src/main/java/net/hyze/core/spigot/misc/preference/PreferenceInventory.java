package net.hyze.core.spigot.misc.preference;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.shared.user.preferences.UserPreference;
import net.hyze.core.spigot.echo.packets.UserPreferenceUpdatePacket;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class PreferenceInventory extends CustomInventory {

    private final int[] slots = {1, 2, 3, 4, 5, 6, 7};

    public PreferenceInventory(User user) {
        super(3 * 9, "Preferências");

        ItemBuilder iconOn = ItemBuilder.of(Material.STAINED_GLASS_PANE)
                .durability(5)
                .lore("&fEstado: &aligado.");

        ItemBuilder iconOff = ItemBuilder.of(Material.STAINED_GLASS_PANE)
                .durability(14)
                .lore("&fEstado: &cdesligado.");

        UserPreference preferences = CoreProvider.Cache.Local.USERS_PREFERENCES.provide().get(user);
        AtomicInteger slotCount = new AtomicInteger();

        PreferenceInventoryRegistry.get().forEach((preferenceId, settings) -> {
            PreferenceStatus status = preferences.getPreference(preferenceId, settings.getRight());

            int currentSlot = this.slots[slotCount.getAndIncrement()];

            String name = (status.is(PreferenceStatus.ON) ? "&a" : "&c") + settings.getLeft().getName();

            Consumer<InventoryClickEvent> callback = event -> {
                Player player = (Player) event.getWhoClicked();

                User eventUser = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

                UserPreference eventPreferences = CoreProvider.Cache.Local.USERS_PREFERENCES.provide().get(eventUser);

                PreferenceStatus eventStatus = eventPreferences.getPreference(preferenceId, settings.getRight());
                PreferenceStatus newStatus = eventStatus.opposite();

                eventPreferences.setPreference(preferenceId, newStatus);
                CoreProvider.Repositories.USERS_PREFERENCES.provide().updateUserPreference(preferenceId, newStatus, eventUser);

                CoreProvider.Redis.ECHO.provide().publish(new UserPreferenceUpdatePacket(eventUser));

                player.openInventory(new PreferenceInventory(eventUser));

                settings.getLeft().getEvent().accept(event);
            };

            setItem(
                    currentSlot,
                    ItemBuilder.of(settings.getLeft().getIcon())
                            .name(name)
                            .lore(settings.getLeft().getDescription())
                            .make(),
                    callback
            );

            setItem(
                    currentSlot + 9,
                    status.is(PreferenceStatus.ON) ? iconOn.clone().name(name).make() : iconOff.clone().name(name).make(),
                    callback
            );

        });

    }

}
