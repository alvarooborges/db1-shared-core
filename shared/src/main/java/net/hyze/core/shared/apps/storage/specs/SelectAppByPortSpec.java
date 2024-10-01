package net.hyze.core.shared.apps.storage.specs;

import net.hyze.core.shared.CoreConstants;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class SelectAppByPortSpec extends SelectAppSpec {

    private final int port;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `port` = ?;",
                    CoreConstants.Databases.Mysql.Tables.APPS_TABLE_NAME
            ));

            statement.setInt(1, port);

            return statement;
        };
    }
}
