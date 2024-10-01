package net.hyze.core.spigot.misc.pathfinding.navigation;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.Navigation;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;

public class VanillaNavigation extends AbstractNavigation {

    private Navigation navigation;

    public VanillaNavigation(EntityInsentient entity, double walkSpeed) {
        super(entity, walkSpeed);

        this.navigation = (Navigation) entity.getNavigation();
    }

    public VanillaNavigation(EntityInsentient entity, NavigationParameters parameters) {
        super(entity, parameters);

        this.navigation = (Navigation) entity.getNavigation();
    }

    @Override
    public void tick() {
        this.navigation.k();
    }

    @Override
    public void stop() {
        this.navigation.n();
    }

    @Override
    public boolean navigateTo(double x, double y, double z) {
        if (this.navigation.a(x, y, z, 1.D)) {
            applyParameters();
            return true;
        }

        return false;
    }

    @Override
    public boolean navigateTo(Entity entity) {
        return navigateTo(((CraftLivingEntity) entity).getHandle());
    }

    @Override
    public boolean hasFinished() {
        return this.navigation.m();
    }

    public boolean navigateTo(net.minecraft.server.v1_8_R3.Entity entity) {
        if (this.navigation.a(entity, 1.0D)) {
            applyParameters();
            return true;
        }
        return false;
    }

    @Override
    public void applyParameters() {
        this.navigation.a(parameters.avoidWater());
        this.entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(parameters.speed() + parameters.speedModifier());
    }
}

