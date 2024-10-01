package net.hyze.core.spigot.misc.customitem.data.compacted;

import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;

public class BlazeRodCompacted  extends CustomItem {

    public static String KEY = "blaze_rod_compacted";

    public BlazeRodCompacted() {
        super(KEY);
    }

    @Override
    protected ItemBuilder getItemBuilder() {
        return ItemBuilder.of(Material.BLAZE_ROD)
                .name(getDisplayName())
                .nbt(CoreSpigotConstants.NBTKeys.CUSTOM_AMOUNT, 9)
                .glowing(true);
    }

    @Override
    public String getDisplayName() {
        return "&bVara de Blaze Compactada";
    }
}