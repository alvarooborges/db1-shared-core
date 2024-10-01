package net.hyze.core.shared.misc.youtube.storage.spec.youtuber;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class InsertYoutuberSpec extends InsertSqlSpec<Boolean> {

    private final int userId;
    private final String channelId;

    @Override
    public Boolean parser(int affectedRows, KeyHolder keyHolder) {
        return affectedRows != 1;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "INSERT INTO `%s`(`user_id`, `channel_id`) VALUES (?, ?);";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(
                            query,
                            CoreConstants.Databases.Mysql.Tables.YOUTUBERS_TABLE_NAME
                    ),
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setInt(1, this.userId);
            statement.setString(2, this.channelId);

            return statement;
        };
    }

}
