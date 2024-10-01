package net.hyze.core.spigot.misc.pathfinding.goals;

import net.hyze.core.spigot.misc.pathfinding.IPathEntity;
import net.hyze.core.spigot.misc.pathfinding.PathGoal;
import net.hyze.core.spigot.misc.pathfinding.navigation.AbstractNavigation;
import net.minecraft.server.v1_8_R3.*;

public abstract class PathGoalFollowOwner extends PathGoal  {

    private EntityInsentient entity;
    private AbstractNavigation navigation;

    private int timer = 0;

    private double startDistance;
    private double stopDistance;
    private double teleportDistance;

    public PathGoalFollowOwner(IPathEntity entity, double startDistance, double stopDistance, double teleportDistance) {
        this.entity = entity.getEntity();
        this.navigation = entity.getAbstractNavigation();

        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.teleportDistance = teleportDistance;
    }

    public abstract Entity getOwner();

    @Override
    public boolean shouldStart() {
        if (this.entity.h(getOwner()) < this.startDistance) {
            return false;
        }

        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (this.entity.h(getOwner()) < this.stopDistance) {
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        this.timer = 0;
    }

    @Override
    public void finish() {
        this.navigation.stop();
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();

        this.entity.getControllerLook().a(owner, 10.0F, (float) this.entity.bQ());

        if (--this.timer <= 0) {
            this.timer = 10;

            if (this.entity.h(owner) > this.teleportDistance * this.teleportDistance && owner.onGround) {
                this.entity.setPositionRotation(owner.locX, owner.locY, owner.locZ, entity.yaw, entity.pitch);
                return;
            }

            this.navigation.navigateTo(owner.getBukkitEntity());
        }
    }

}
