package net.hyze.core.shared.dungeon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class DungeonMapAccess {

    private final String mapId;

    @Setter
    private int accesses;

    private boolean unlocked;

    private Date updatedAt;


}
