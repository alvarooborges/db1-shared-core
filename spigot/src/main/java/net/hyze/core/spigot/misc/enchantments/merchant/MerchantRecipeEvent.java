package net.hyze.core.spigot.misc.enchantments.merchant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class MerchantRecipeEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Setter
    protected boolean cancelled;

    @Setter
    private ItemStack result;

    private final Player player;
    private final ItemStack first;
    private final ItemStack second;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
