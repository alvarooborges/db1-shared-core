package net.hyze.core.shared.user.storage.specs;

import net.hyze.core.shared.CoreConstants;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class SelectUsersByEmailSpec extends SelectUsersSpec {

    private final String[] emails;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `email` in (?);",
                    CoreConstants.Databases.Mysql.Tables.USERS_TABLE_NAME
            ));

            statement.setArray(1, connection.createArrayOf("text", emails));

            return statement;
        };
    }
}
