package net.hyze.core.spigot.voidworld;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class VoidChunkGenerator extends ChunkGenerator {

    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.emptyList();
    }

    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
        return new byte[32768];
    }

    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 128, 0);
    }

}