package net.hyze.core.shared.apps.storage.specs;

import net.hyze.core.shared.CoreConstants;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class SelectAppByIdSpec extends SelectAppSpec {

    private final String id;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `id` = ?;",
                    CoreConstants.Databases.Mysql.Tables.APPS_TABLE_NAME
            ));

            statement.setString(1, id);

            return statement;
        };
    }
}
