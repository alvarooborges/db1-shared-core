package net.hyze.core.spigot.misc.mining;

import org.bukkit.Material;

public interface BaseTool {

    boolean isValidTool(Material tool);

    Material getMaterial();
}
