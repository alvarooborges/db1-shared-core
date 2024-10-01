package net.hyze.core.shared.punishments.storage.specs.punishments;

import net.hyze.core.shared.CoreConstants;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class SelectPunishmentByIdSpec extends SelectPunishmentsSpec {

    private final int id;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `id` = ?;",
                    CoreConstants.Databases.Mysql.Tables.PUNISHMENTS_TABLE_NAME
            ));

            statement.setInt(1, this.id);
            return statement;
        };

    }

}
