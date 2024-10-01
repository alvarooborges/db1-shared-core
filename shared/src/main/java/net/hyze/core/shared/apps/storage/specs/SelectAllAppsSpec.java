package net.hyze.core.shared.apps.storage.specs;

import net.hyze.core.shared.CoreConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

public class SelectAllAppsSpec extends SelectAppsSpec {

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            return connection.prepareStatement(String.format(
                    "SELECT * FROM `%s`;",
                    CoreConstants.Databases.Mysql.Tables.APPS_TABLE_NAME
            ));
        };
    }
}
