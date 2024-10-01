package net.hyze.core.shared.punishments;

import com.google.common.base.Preconditions;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import net.hyze.core.shared.user.User;

@Getter
@Setter
public class Punishment {

    private Integer id;
    private String hardwareId;
    private final Integer userId; // 0 -> console
    private final Integer userSessionId; // null -> console or not available
    private final Integer applierId; // 0 -> console
    private final Integer applierSessionId; // null -> console or not available
    private Integer revokerId; // 0 -> console; null -> not revoked
    private Integer revokerSessionId; // null -> console or not available
    private final Date createdAt;
    private Date startedAt;
    private Date revokedAt;
    private final PunishmentCategory category;
    private final PunishmentLevel level;
    private final String reason;
    private final String proof;
    private PunishmentRevokeCategory revokeCategory;
    private String revokeReason;
    private String revokeProof;
    private boolean hidden;

    public Punishment(
            Integer id,
            String hardwareId,
            Integer userId,
            Integer userSessionId,
            Integer applierId,
            Integer applierSessionId,
            Integer revokerId,
            Integer revokerSessionId,
            Date createdAt,
            Date startedAt,
            Date revokedAt,
            PunishmentCategory category,
            PunishmentLevel level,
            String reason,
            String proof,
            PunishmentRevokeCategory revokeCategory,
            String revokeReason,
            String revokeProof,
            boolean hidden) {

        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(userId);
        Preconditions.checkNotNull(applierId);
        Preconditions.checkNotNull(createdAt);
        Preconditions.checkNotNull(level);

        this.id = id;
        this.hardwareId = hardwareId;
        this.userId = userId;
        this.userSessionId = userSessionId;
        this.applierId = applierId;
        this.applierSessionId = applierSessionId;
        this.revokerId = revokerId;
        this.revokerSessionId = revokerSessionId;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.revokedAt = revokedAt;
        this.category = category;
        this.level = level;
        this.reason = reason;
        this.proof = proof;
        this.revokeCategory = revokeCategory;
        this.revokeReason = revokeReason;
        this.revokeProof = revokeProof;
        this.hidden = hidden;
    }

    // mute, ban and punish constructor
    public Punishment(User user,
            String hardwareId,
            User applier,
            PunishmentCategory category,
            PunishmentLevel level,
            String reason,
            String proof) {

        this(0,
                hardwareId,
                user.getId(),
                null,
                applier.getId(),
                null,
                null,
                null,
                new Date(),
                null,
                null,
                category,
                level,
                reason,
                proof,
                null,
                null,
                null,
                false);

    }

    public PunishmentState getState() {
        Date endTime = getEndedAt();

        if (endTime == null) {
            return PunishmentState.PENDING;
        }

        if (endTime.after(new Date())) {
            return PunishmentState.ACTIVE;
        }

        return PunishmentState.ENDED;
    }

    public Date getEndedAt() {

        if (this.level.isPermanent()) {
            return new Date(Long.MAX_VALUE);
        }
        
        if (this.startedAt == null) {
            return null;
        }

        return new Date(this.startedAt.getTime() + this.level.getDuration());
    }

    public boolean isAppliedByConsole() {
        return this.applierId < 1;
    }

    public boolean isRevoked() {
        return this.revokerId != null;
    }

    public boolean isRevokedByConsole() {
        return isRevoked() && this.revokerId < 1;
    }

    public String getInternalDisplayRevokeReason() {
        return this.revokeReason != null ? this.revokeReason : (this.getRevokeCategory() != null ? this.getRevokeCategory().getDisplayName() : null);
    }

    public String getInternalDisplayReason() {
        return this.reason != null ? this.reason : (this.getCategory() != null ? this.getCategory().getDisplayName() : null);
    }

    public String getDisplayReason(String defaultReason) {
        String internalDisplayReason = getInternalDisplayReason();
        return (internalDisplayReason == null ? defaultReason : internalDisplayReason) + (this.proof == null ? "" : " - " + this.proof);
    }

}
