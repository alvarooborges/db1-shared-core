package net.hyze.core.shared.punishments.storage.specs.punishments;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class UpdatePunishmentHardwareIdSpec extends UpdateSqlSpec<Void> {

    private final Punishment punishment;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            PreparedStatement statement = connection.prepareStatement(String.format(
                    "UPDATE `%s` SET `hardware_id` = ? WHERE `id` = ?",
                    CoreConstants.Databases.Mysql.Tables.PUNISHMENTS_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, this.punishment.getHardwareId());
            statement.setInt(2, this.punishment.getId());
            
            return statement;

        };

    }

}
