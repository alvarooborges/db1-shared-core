package net.hyze.core.spigot.misc.customitem;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@Getter
@EqualsAndHashCode(of = {"key"})
@RequiredArgsConstructor
public abstract class CustomItem {


    @NonNull
    private final String key;

    protected abstract ItemBuilder getItemBuilder();

    public abstract String getDisplayName();

    public ItemStack asItemStack() {
        return this.asItemStack(1);
    }

    public ItemStack asItemStack(int amount) {
        ItemBuilder builder = getItemBuilder().clone();

        return asItemStack(builder.clone().amount(amount));
    }

    protected final ItemStack asItemStack(ItemBuilder itemBuilder) {
        if (!CustomItemRegistry.isRegistered(this)) {
            throw new IllegalRegistryException("O item " + key + " não está registrado.");
        }

        if (itemBuilder == null) {
            throw new IllegalRegistryException("O item " + key + " não possui um item builder.");
        }

        ItemStack out = itemBuilder.clone().make();

        CustomItemUtil.tagItem(this, out);

        return out;
    }
}
