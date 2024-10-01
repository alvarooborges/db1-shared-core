package net.hyze.core.shared.user.storage.specs;

import net.hyze.core.shared.CoreConstants;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class SelectUserByEmailSpec extends SelectUserSpec {

    private final String email;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `email` = ? LIMIT 1;",
                    CoreConstants.Databases.Mysql.Tables.USERS_TABLE_NAME
            ));

            statement.setString(1, this.email);

            return statement;
        };

    }
}
