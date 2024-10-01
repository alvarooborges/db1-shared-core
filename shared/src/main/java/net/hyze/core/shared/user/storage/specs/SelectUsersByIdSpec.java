package net.hyze.core.shared.user.storage.specs;

import net.hyze.core.shared.CoreConstants;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class SelectUsersByIdSpec extends SelectUsersSpec {

    private final Integer[] ids;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `id` in (?);",
                    CoreConstants.Databases.Mysql.Tables.USERS_TABLE_NAME
            ));

            statement.setArray(1, connection.createArrayOf("int", ids));

            return statement;
        };
    }
}
