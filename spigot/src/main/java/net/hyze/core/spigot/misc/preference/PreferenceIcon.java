package net.hyze.core.spigot.misc.preference;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class PreferenceIcon {

    private final String name;
    private final String[] description;
    private final ItemStack icon;
    private final Consumer<InventoryClickEvent> event;

    public PreferenceIcon(String name, String[] description, ItemStack icon) {
        this(
                name,
                description,
                icon,
                event -> {
                }
        );
    }

}
