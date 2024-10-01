package net.hyze.core.shared.dungeon;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public class DungeonUserData {

    @Getter
    private final Integer userId;

    @Setter
    private Map<String, DungeonMapAccess> mapAccesses = Maps.newHashMap();

    @Getter
    private int ressurectionPotions = 0;

    public DungeonMapAccess getAccesses(BasicDungeonMap map) {
        return mapAccesses.get(map.getId());
    }

    public DungeonMapAccess giveAccess(BasicDungeonMap map, int amount, boolean defaultUnlock) {
        DungeonMapAccess accesses = getAccesses(map);
        if(accesses == null) {
            accesses = new DungeonMapAccess(map.getId(), 0, defaultUnlock, new Date(0));
            this.mapAccesses.put(map.getId(), accesses);
        }

        accesses.setAccesses(Math.max(accesses.getAccesses() + amount, 0));
        return accesses;
    }

    public void takeRessurectionPotions(int amount) {
        this.ressurectionPotions = Math.max(this.ressurectionPotions - 1, 0);
    }

    public void giveRessurectionPotions(int amount) {
        this.ressurectionPotions += amount;
    }
}
