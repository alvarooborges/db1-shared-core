package net.hyze.core.shared.misc.youtube.storage.spec.videos;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public class SelectVideoSpec extends SelectSqlSpec<Boolean> {

    private final String videoId;

    @Override
    public ResultSetExtractor<Boolean> getResultSetExtractor() {
        return (ResultSet result) -> {
            return result.next();
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "SELECT * FROM `%s` WHERE `video_id`=?";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(
                            query,
                            CoreConstants.Databases.Mysql.Tables.YOUTUBERS_VIDEOS_TABLE_NAME
                    ),
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, this.videoId);

            return statement;
        };
    }

}
