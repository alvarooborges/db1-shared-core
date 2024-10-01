package net.hyze.core.spigot.misc.mining;

import net.hyze.core.shared.group.Group;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@EqualsAndHashCode(of = {"drop", "amount", "group", "dropWeight"})
public class Drop {

    protected ItemStack itemStack;

    protected final Material drop;
    protected final int amount;
    protected final Group group;
    protected final int dropWeight;

    public Drop(Material drop) {
        this(drop, 1);
    }

    public Drop(Material drop, int amount) {
        this(drop, amount, Group.DEFAULT);
    }

    public Drop(Material drop, int amount, Group group) {
        this(drop, amount, group, 1);
    }

    public Drop(Material drop, int amount, Group group, int dropWeight) {
        this.drop = drop;
        this.amount = amount;
        this.group = group;
        this.dropWeight = dropWeight;

        itemStack = new ItemStack(drop, amount);
    }

    public boolean isValidDrop(Location location, ItemStack inHand) {
        return true;
    }
}
