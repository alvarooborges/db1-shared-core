package net.hyze.core.shared.sessions.storage.specs;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.sessions.UserSession;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class InsertSessionSpec extends InsertSqlSpec<UserSession> {

    private final User user;
    private final UserSession session;
    private final boolean logged;
    
    
    @Override
    public UserSession parser(int affectedRows, KeyHolder keyHolder) {

        if (affectedRows != 1) {
            return null;
        }

        this.session.setId(keyHolder.getKey().intValue());
        return this.session;

    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            PreparedStatement statement = connection.prepareStatement(String.format(
                    "INSERT INTO `%s` (`user_id`, `ip`, `version`, `started_at`, `logged`) VALUE (?, ?, ?, ?, ?)",
                    CoreConstants.Databases.Mysql.Tables.SESSIONS
            ), Statement.RETURN_GENERATED_KEYS);
            
            statement.setInt(1, this.user.getId());
            statement.setString(2, this.session.getIp());
            statement.setInt(3, this.session.getVersion());
            statement.setTimestamp(4, new Timestamp(this.session.getStartedAt().getTime()));
            statement.setBoolean(5, this.logged);
            
            return statement;

        };

    }

}
