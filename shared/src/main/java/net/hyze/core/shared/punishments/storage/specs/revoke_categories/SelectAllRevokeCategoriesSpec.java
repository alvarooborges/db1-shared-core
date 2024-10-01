package net.hyze.core.shared.punishments.storage.specs.revoke_categories;

import net.hyze.core.shared.CoreConstants;
import java.sql.PreparedStatement;
import org.springframework.jdbc.core.PreparedStatementCreator;

public class SelectAllRevokeCategoriesSpec extends SelectRevokeCategoriesSpec {

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s`;",
                    CoreConstants.Databases.Mysql.Tables.PUNISHMENT_REVOKE_CATEGORIES_TABLE_NAME
            ));

            return statement;
        };

    }

}
