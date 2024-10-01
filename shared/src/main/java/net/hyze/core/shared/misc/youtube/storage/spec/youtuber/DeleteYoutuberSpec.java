package net.hyze.core.shared.misc.youtube.storage.spec.youtuber;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.DeleteSqlSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class DeleteYoutuberSpec extends DeleteSqlSpec<Boolean> {

    private final int userId;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows == 1;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "DELETE FROM `%s` WHERE `user_id` = ?;";

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

}
