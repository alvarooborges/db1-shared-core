package net.hyze.core.spigot.misc.pathfinding;

public abstract class PathGoal {

    public abstract boolean shouldStart(); //a

    public boolean shouldContinue() { //b
        return shouldStart();
    }

    public void start() { //c
    }

    public void finish() { //d
    }

    public abstract void tick(); //e

}