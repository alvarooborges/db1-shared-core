package net.hyze.core.shared.punishments.storage.specs.punishments;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class InsertPunishmentSpec extends InsertSqlSpec<Punishment> {

    private final Punishment punishment;

    @Override
    public Punishment parser(int affectedRows, KeyHolder keyHolder) {

        if (affectedRows != 1) {
            return null;
        }

        this.punishment.setId(keyHolder.getKey().intValue());
        return this.punishment;

    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            System.out.println("INSERT");
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "INSERT INTO `%s` (`user_id`, `hardware_id`, `user_session_id`, `applier_user_id`, `applier_session_id`, `created_at`, `started_at`, `duration`, `category`, `type`, `reason`, `proof`) VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                    CoreConstants.Databases.Mysql.Tables.PUNISHMENTS_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, this.punishment.getUserId());

            if (this.punishment.getHardwareId() == null) {
                statement.setNull(2, Types.VARCHAR);
            } else {
                statement.setString(2, this.punishment.getHardwareId());
            }

            if (this.punishment.getUserSessionId() == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, this.punishment.getUserSessionId());
            }

            statement.setInt(4, this.punishment.getApplierId());

            if (this.punishment.getApplierSessionId() == null) {
                statement.setNull(5, Types.INTEGER);
            } else {
                statement.setInt(5, this.punishment.getApplierSessionId());
            }

            statement.setTimestamp(6, new Timestamp(this.punishment.getCreatedAt().getTime()));
            statement.setTimestamp(7, this.punishment.getStartedAt() == null ? null : new Timestamp(this.punishment.getStartedAt().getTime()));
            statement.setLong(8, this.punishment.getLevel().getDuration());
            statement.setString(9, this.punishment.getCategory() == null ? null : this.punishment.getCategory().getName());
            statement.setString(10, this.punishment.getLevel().getType().getName());
            statement.setString(11, this.punishment.getReason());
            statement.setString(12, this.punishment.getProof());

            return statement;

        };

    }

}
