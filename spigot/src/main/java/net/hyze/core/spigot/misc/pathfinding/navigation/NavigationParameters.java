package net.hyze.core.spigot.misc.pathfinding.navigation;

import java.util.HashMap;
import java.util.Map;

public class NavigationParameters {

    private boolean avoidWater = false;
    private double speed;
    private Map<String, Double> speedModifier = new HashMap<>();

    public NavigationParameters(double baseSpeed) {
        speed = baseSpeed;
    }

    public void avoidWater(boolean avoidWater) {
        this.avoidWater = avoidWater;
    }

    public boolean avoidWater() {
        return avoidWater;
    }

    public void speed(double speed) {
        this.speed = speed;
    }

    public double speed() {
        return speed;
    }

    public void addSpeedModifier(String id, double speedModifier) {
        this.speedModifier.put(id, speedModifier);
    }

    public void removeSpeedModifier(String id) {
        this.speedModifier.remove(id);
    }

    public double speedModifier() {
        double speedModifier = 0D;
        for (Double sm : this.speedModifier.values()) {
            speedModifier += sm;
        }
        return speedModifier;
    }

}

