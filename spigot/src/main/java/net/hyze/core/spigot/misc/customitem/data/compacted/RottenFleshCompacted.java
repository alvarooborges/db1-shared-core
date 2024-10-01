package net.hyze.core.spigot.misc.customitem.data.compacted;

import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;

public class RottenFleshCompacted extends CustomItem {

    public static String KEY = "rotten_flesh_compacted";

    public RottenFleshCompacted() {
        super(KEY);
    }

    @Override
    protected ItemBuilder getItemBuilder() {
        return ItemBuilder.of(Material.ROTTEN_FLESH)
                .name(getDisplayName())
                .nbt(CoreSpigotConstants.NBTKeys.CUSTOM_AMOUNT, 9)
                .glowing(true);
    }

    @Override
    public String getDisplayName() {
        return "&bCarne Podre Compactada";
    }
}