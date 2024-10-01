package net.hyze.core.shared.punishments.storage.specs.punishments;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class UpdatePunishmentRevokeSpec extends UpdateSqlSpec<Void> {

    private final Punishment punishment;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            PreparedStatement statement = connection.prepareStatement(String.format(
                    "UPDATE `%s` SET `revoker_user_id` = ?, `revoker_session_id` = ?, `revoked_at` = ?, `revoke_reason` = ?, `revoke_category` = ?, `revoke_proof` = ? WHERE `id` = ?",
                    CoreConstants.Databases.Mysql.Tables.PUNISHMENTS_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            if (this.punishment.getRevokerId() == null) {
                statement.setNull(1, Types.INTEGER);
            } else {
                statement.setInt(1, this.punishment.getRevokerId());
            }

            if (this.punishment.getRevokerSessionId() == null) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, this.punishment.getRevokerSessionId());
            }

            statement.setTimestamp(3, new Timestamp(this.punishment.getRevokedAt().getTime()));
            statement.setString(4, this.punishment.getRevokeReason());
            statement.setString(5, this.punishment.getRevokeCategory() == null ? null : this.punishment.getRevokeCategory().getName());
            statement.setString(6, this.punishment.getRevokeProof());
            statement.setInt(7, this.punishment.getId());

            return statement;

        };

    }

}
