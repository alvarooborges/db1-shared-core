package net.hyze.core.spigot.misc.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class PathGoalSelector {

    private Map<String, PathGoal> pathGoalMap = Maps.newHashMap();
    private List<PathGoal> pathGoalList = Lists.newLinkedList();
    private List<PathGoal> activePathGoalList = Lists.newLinkedList();

    public void addGoal(String name, PathGoal goal) {
        if (pathGoalMap.containsKey(name)) {
            return;
        }
        pathGoalMap.put(name, goal);
        pathGoalList.add(goal);
    }

    public void addGoal(String name, int pos, PathGoal goal) {
        if (pathGoalMap.containsKey(name)) {
            return;
        }
        pathGoalMap.put(name, goal);
        pathGoalList.add(pos, goal);
    }

    public void replaceGoal(String name, PathGoal goal) {
        if (pathGoalMap.containsKey(name)) {
            PathGoal oldGoal = pathGoalMap.get(name);
            if (activePathGoalList.contains(oldGoal)) {
                activePathGoalList.remove(oldGoal);
                oldGoal.finish();
            }
            int index = pathGoalList.indexOf(oldGoal);
            pathGoalList.add(index, goal);
            pathGoalList.remove(oldGoal);
            pathGoalMap.put(name, goal);
        } else {
            addGoal(name, goal);
        }
    }

    public void removeGoal(String name) {
        if (pathGoalMap.containsKey(name)) {
            PathGoal goal = pathGoalMap.get(name);
            pathGoalList.remove(goal);
            pathGoalMap.remove(name);
            if (activePathGoalList.contains(goal)) {
                goal.finish();
            }
            activePathGoalList.remove(goal);
        }
    }

    public boolean hasGoal(String name) {
        return pathGoalMap.containsKey(name);
    }

    public PathGoal getGoal(String name) {
        return pathGoalMap.get(name);
    }

    public void clearGoals() {
        pathGoalList.clear();
        pathGoalMap.clear();
        for (PathGoal goal : activePathGoalList) {
            goal.finish();
        }
        activePathGoalList.clear();
    }

    public void finish() {
        for (PathGoal goal : activePathGoalList) {
            goal.finish();
        }
        activePathGoalList.clear();
    }

    public void tick() {
        // add goals
        ListIterator iterator = pathGoalList.listIterator();
        while (iterator.hasNext()) {
            PathGoal goal = (PathGoal) iterator.next();
            if (!activePathGoalList.contains(goal)) {
                if (goal.shouldStart()) {
                    goal.start();
                    activePathGoalList.add(goal);
                }
            }
        }

        // remove goals
        iterator = activePathGoalList.listIterator();
        while (iterator.hasNext()) {
            PathGoal goal = (PathGoal) iterator.next();
            if (!goal.shouldContinue()) {
                goal.finish();
                iterator.remove();
            }
        }

        // tick goals
        iterator = activePathGoalList.listIterator();
        while (iterator.hasNext()) {
            PathGoal goal = (PathGoal) iterator.next();
            goal.tick();
        }
    }

}
