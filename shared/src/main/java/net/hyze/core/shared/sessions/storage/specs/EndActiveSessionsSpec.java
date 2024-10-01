package net.hyze.core.shared.sessions.storage.specs;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.sessions.UserSessionStatus;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class EndActiveSessionsSpec extends InsertSqlSpec<Void> {

    private final User user;
    
    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            PreparedStatement statement = connection.prepareStatement(String.format(
                    "UPDATE `%s` SET `status` = ?, `ended_at` = NOW() WHERE `user_id`= ? AND `status` = ?;",
                    CoreConstants.Databases.Mysql.Tables.SESSIONS
            ), Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, UserSessionStatus.FAILED.toString());
            statement.setInt(2, this.user.getId());
            statement.setString(3, UserSessionStatus.ACTIVE.toString());
            
            return statement;

        };

    }

}
