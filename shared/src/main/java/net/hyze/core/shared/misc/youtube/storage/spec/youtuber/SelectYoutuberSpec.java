package net.hyze.core.shared.misc.youtube.storage.spec.youtuber;

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
public class SelectYoutuberSpec extends  SelectSqlSpec<String> {

    private final int userId;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "SELECT * FROM `%s` WHERE `user_id` = ?;";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(
                            query,
                            CoreConstants.Databases.Mysql.Tables.YOUTUBERS_TABLE_NAME
                    ),
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setInt(1, this.userId);

            return statement;
        };
    }

    @Override
    public ResultSetExtractor<String> getResultSetExtractor() {
        return (ResultSet result) -> {
            
            if(result.next()){
                return result.getString("channel_id");
            }
            
            return null;
        };
    }

}
