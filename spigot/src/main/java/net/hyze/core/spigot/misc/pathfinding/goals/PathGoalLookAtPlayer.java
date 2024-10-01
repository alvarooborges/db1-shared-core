package net.hyze.core.spigot.misc.pathfinding.goals;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.spigot.misc.pathfinding.PathGoal;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;

public class PathGoalLookAtPlayer extends PathGoal {

    private EntityInsentient entity;

    private Entity player;

    private float range;
    private int ticksLeft;

    private float chance;
    private Class clazz;

    public PathGoalLookAtPlayer(EntityInsentient entity, Class c) {
        this.entity = entity;
        this.range = 8.0F;
        this.chance = 0.02F;
        this.clazz = c;
    }

    public PathGoalLookAtPlayer(EntityInsentient entity, Class c, float f, float f1) {
        this.entity = entity;
        this.range = f;
        this.chance = f1;
        this.clazz = c;
    }

    @Override
    public boolean shouldStart() {
        if (CoreConstants.RANDOM.nextFloat() >= this.chance) {
            return false;
        }

        if (this.entity.passenger != null) {
            return false;
        }

        if (this.clazz == EntityHuman.class) {
            this.player = this.entity.world.findNearbyPlayer(this.entity, (double) this.range);
        } else {
            this.player = this.entity.world.a(this.clazz, this.entity.getBoundingBox().grow((double) this.range, 3.0D, (double) this.range), this.entity);
        }

        return this.player != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.player.isAlive() && (this.entity.h(this.player) <= (double) (this.range * this.range) && this.ticksLeft > 0);
    }

    @Override
    public void start() {
        this.ticksLeft = 40 + CoreConstants.RANDOM.nextInt(40);
    }

    @Override
    public void finish() {
        this.player = null;
    }

    @Override
    public void tick() {
        this.entity.getControllerLook().a(this.player.locX, this.player.locY + (double) this.player.getHeadHeight(), this.player.locZ, 10.0F, (float) this.entity.bQ());
        --this.ticksLeft;
    }
}
