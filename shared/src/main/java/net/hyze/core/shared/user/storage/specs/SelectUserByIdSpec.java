package net.hyze.core.shared.user.storage.specs;

import net.hyze.core.shared.CoreConstants;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class SelectUserByIdSpec extends SelectUserSpec {

    private final Integer id;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `id` = ? LIMIT 1;",
                    CoreConstants.Databases.Mysql.Tables.USERS_TABLE_NAME
            ));

            statement.setInt(1, this.id);

            return statement;
        };
    }
}
