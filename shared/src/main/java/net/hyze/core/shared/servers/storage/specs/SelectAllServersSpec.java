package net.hyze.core.shared.servers.storage.specs;

import net.hyze.core.shared.CoreConstants;
import java.sql.PreparedStatement;
import org.springframework.jdbc.core.PreparedStatementCreator;

public class SelectAllServersSpec extends SelectServersSpec {

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s`;",
                    CoreConstants.Databases.Mysql.Tables.SERVERS_TABLE_NAME
            ));

            return statement;
        };
    }
}
