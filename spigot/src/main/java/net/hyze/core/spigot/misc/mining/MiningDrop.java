package net.hyze.core.spigot.misc.mining;

import com.google.common.collect.Sets;
import net.hyze.core.shared.group.Group;
import java.util.HashSet;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

@Getter
public class MiningDrop extends Drop {

    protected final MiningTool tool;
    protected final int maximumHeight;
    protected final MiningXP xp;
    protected final HashSet<Biome> biomes = Sets.newHashSet();

    public MiningDrop(Material drop, MiningXP xp, MiningTool tool) {
        this(drop, xp, tool, 1);
    }

    public MiningDrop(Material drop, MiningXP xp, MiningTool tool, int dropWeight) {
        this(drop, 1, xp, tool, dropWeight);
    }

    public MiningDrop(Material drop, int amount, MiningXP xp, MiningTool tool, int dropWeight) {
        this(drop, amount, 256, xp, tool, dropWeight);
    }

    public MiningDrop(Material drop, int amount, int maximumHeight, MiningXP xp, MiningTool tool, int dropWeight) {
        this(drop, amount, maximumHeight, xp, tool, dropWeight, Group.DEFAULT);
    }

    public MiningDrop(Material drop, int amount, int maximumHeight, MiningXP xp, MiningTool tool, int dropWeight, Group group) {
        this(new ItemStack(drop, amount), maximumHeight, xp, tool, dropWeight, null, group);
    }

    public MiningDrop(Material drop, int amount, int maximumHeight, MiningXP xp, MiningTool tool, int dropWeight, HashSet<Biome> biomes) {
        this(new ItemStack(drop, amount), maximumHeight, xp, tool, dropWeight, biomes, Group.DEFAULT);
    }

    public MiningDrop(ItemStack is, int maximumHeight, MiningXP xp, MiningTool tool, int dropWeight) {
        this(is, maximumHeight, xp, tool, dropWeight, null, Group.DEFAULT);
    }

    public MiningDrop(ItemStack is, int maximumHeight, MiningXP xp, MiningTool tool, int dropWeight, HashSet<Biome> biomes) {
        this(is, maximumHeight, xp, tool, dropWeight, biomes, Group.DEFAULT);
    }

    public MiningDrop(ItemStack is, int maximumHeight, MiningXP xp, MiningTool tool, int dropWeight, HashSet<Biome> biomes, Group group) {
        super(is.getType(), is.getAmount(), group, dropWeight);

        this.xp = xp;
        this.tool = tool;
        this.itemStack = is;
        this.maximumHeight = maximumHeight;

        if (biomes != null) {
            this.biomes.addAll(biomes);
        }
    }

    public int getXP() {
        return xp.getXP();
    }

    public boolean isValidDrop(Location location, ItemStack inHand) {
        return getMaximumHeight() >= location.getBlockY() && tool.isValidTool(inHand.getType())
                && (biomes.isEmpty() || biomes.contains(location.getWorld().getBiome(location.getBlockX(), location.getBlockZ())));

    }
}
