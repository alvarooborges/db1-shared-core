package net.hyze.core.spigot.misc.pathfinding;

import net.hyze.core.spigot.misc.pathfinding.navigation.AbstractNavigation;
import net.minecraft.server.v1_8_R3.EntityInsentient;

public interface IPathEntity {

    PathGoalSelector getPathGoalSelector();

    EntityInsentient getEntity();

    AbstractNavigation getAbstractNavigation();

}
