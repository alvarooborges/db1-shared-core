package net.hyze.core.shared.sessions.storage.specs;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.sessions.UserSessionStatus;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class UpdateSessionLoggedSpec extends InsertSqlSpec<Void> {

    private final int id;
    private final boolean logged;
    
    
    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            PreparedStatement statement = connection.prepareStatement(String.format(
                    "UPDATE `%s` SET `logged` = ? WHERE `id` = ?;",
                    CoreConstants.Databases.Mysql.Tables.SESSIONS
            ), Statement.RETURN_GENERATED_KEYS);
            
            statement.setBoolean(1, this.logged);
            statement.setInt(2, this.id);
            
            return statement;

        };

    }

}
