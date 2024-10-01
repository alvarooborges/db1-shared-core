package net.hyze.core.shared.punishments;

import java.util.LinkedList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.hyze.core.shared.group.Group;

@Getter
@ToString
@RequiredArgsConstructor
public class PunishmentCategory {

    private final int id;

    private final String name;

    private final String displayName;

    private final Group group;

    private final String[] description;

    private final LinkedList<PunishmentLevel> levels;

    private final boolean enabled;

    public PunishmentLevel getLevelByPunishmentsAmount(int amount) {
        return amount >= this.levels.size() ? getHighestLevel() : this.levels.get(amount);
    }

    public PunishmentLevel getHighestLevel() {
        return this.levels.getLast();
    }

}
