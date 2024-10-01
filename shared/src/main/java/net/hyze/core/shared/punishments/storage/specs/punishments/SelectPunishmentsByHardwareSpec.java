package net.hyze.core.shared.punishments.storage.specs.punishments;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.punishments.PunishmentType;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class SelectPunishmentsByHardwareSpec extends SelectPunishmentsSpec {

    private final String hardwareId;
    private final PunishmentType type;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `hardware_id` = ? AND `type` = ? AND `revoker_user_id` IS NULL ORDER BY `created_at` DESC LIMIT 1;",
                    CoreConstants.Databases.Mysql.Tables.PUNISHMENTS_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, this.hardwareId);
            statement.setString(2, this.type.getName());

            return statement;

        };

    }

}
