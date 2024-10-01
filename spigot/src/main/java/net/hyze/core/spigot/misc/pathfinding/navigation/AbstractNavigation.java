package net.hyze.core.spigot.misc.pathfinding.navigation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@AllArgsConstructor
public abstract class AbstractNavigation {

    protected EntityInsentient entity;

    @Getter
    protected NavigationParameters parameters;

    public AbstractNavigation(EntityInsentient entity, double walkSpeed) {
        this(entity, new NavigationParameters(walkSpeed));
    }

    //

    public boolean navigateTo(Location loc) {
        return navigateTo(loc.getX(), loc.getY(), loc.getZ());
    }

    public boolean navigateTo(Entity entity) {
        return navigateTo(entity.getLocation());
    }

    public abstract boolean hasFinished();

    public abstract void tick();

    public abstract void stop();

    public abstract boolean navigateTo(double x, double y, double z);

    public abstract void applyParameters();

}
