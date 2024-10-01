package net.hyze.core.shared.punishments;

import net.hyze.core.shared.misc.utils.TimeCode;
import java.util.Objects;
import lombok.Getter;

@Getter
public class PunishmentLevel {

    private final Long duration;
    private final PunishmentType type;

    public PunishmentLevel(Long duration, PunishmentType type) {

//        Preconditions.checkArgument(duration > 0, "Duration is less than 1: %s", duration);
        this.duration = duration;
        this.type = type;

    }

    public PunishmentLevel(String duration, PunishmentType type) {
        this(TimeCode.parse(duration), type);
    }

    public Long getDuration() {
        return this.duration == null || this.duration <= 0L ? null : this.duration;
    }

    public boolean isPermanent() {
        return this.getDuration() == null;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof PunishmentLevel)) {
            return false;
        }

        PunishmentLevel level = (PunishmentLevel) obj;
        return obj instanceof PunishmentLevel && level.getType().equals(this.type) && level.getDuration().equals(this.duration);

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.duration);
        hash = 17 * hash + Objects.hashCode(this.type);
        return hash;
    }

}
