package net.hyze.core.shared.punishments.storage.specs.punishments;

import com.google.common.collect.Sets;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Set;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.punishments.PunishmentCategory;
import net.hyze.core.shared.punishments.PunishmentLevel;
import net.hyze.core.shared.punishments.PunishmentRevokeCategory;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import org.springframework.jdbc.core.ResultSetExtractor;

public abstract class SelectPunishmentsSpec extends SelectSqlSpec<Set<Punishment>> {

    @Override
    public ResultSetExtractor<Set<Punishment>> getResultSetExtractor() {

        return (ResultSet result) -> {

            Set<Punishment> output = Sets.newHashSet();

            while (result.next()) {

                int id = result.getInt("id");
                
                String hardwareId = result.getString("hardware_id");
                if(result.wasNull()){
                    hardwareId = null;
                }

                int userId = result.getInt("user_id");
                Integer userSessionId = result.getInt("user_session_id");

                if (result.wasNull()) {
                    userSessionId = null;
                }

                int applierUserId = result.getInt("applier_user_id");
                Integer applierSessionId = result.getInt("applier_session_id");

                if (result.wasNull()) {
                    userSessionId = null;
                }

                Integer revokerUserId = result.getInt("revoker_user_id");

                if (result.wasNull()) {
                    revokerUserId = null;
                }

                Integer revokerSessionId = result.getInt("revoker_session_id");

                if (result.wasNull()) {
                    revokerSessionId = null;
                }

                Date createdAt = result.getDate("created_at");
                Date startedAt = result.getDate("started_at");
                Date revokedAt = result.getDate("revoked_at");

                PunishmentCategory category = result.getString("category") == null ? null : CoreProvider.Cache.Local.PUNISHMENTS.provide().getCategory(result.getString("category"));
                PunishmentLevel level = new PunishmentLevel(result.getLong("duration"), CoreProvider.Cache.Local.PUNISHMENTS.provide().getType(result.getString("type")));

                String reason = result.getString("reason");
                String proof = result.getString("proof");

                PunishmentRevokeCategory revokeCategory = result.getString("revoke_category") == null ? null : CoreProvider.Cache.Local.PUNISHMENTS.provide().getRevokeCategory(result.getString("revoke_category"));
                String revokeReason = result.getString("revoke_reason");
                String revokeProof = result.getString("revoke_proof");

                boolean hidden = result.getBoolean("hidden");

                output.add(new Punishment(
                        id,
                        hardwareId,
                        userId,
                        userSessionId,
                        applierUserId,
                        applierSessionId,
                        revokerUserId,
                        revokerSessionId,
                        createdAt,
                        startedAt,
                        revokedAt,
                        category,
                        level,
                        reason,
                        proof,
                        revokeCategory,
                        revokeReason,
                        revokeProof,
                        hidden)
                );

            }

            return output;
        };

    }

}
