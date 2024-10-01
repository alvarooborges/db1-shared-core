package net.hyze.core.shared.dungeon.storage.specs.data;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class DecrementMapAccessesSpec extends UpdateSqlSpec<Void> {

    private final int userId;

    private final String mapId;

    private final int amount;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("UPDATE %s SET `accesses`=GREATEST(`accesses`-?, 0) WHERE `user_id`=? AND `map_id`=?;",
                            CoreConstants.Databases.Mysql.Tables.DUNGEONS_USER_ACCESSES_TABLE_NAME));
            statement.setInt(1, this.amount);
            statement.setInt(2, this.userId);
            statement.setString(3, this.mapId);
            return statement;
        };
    }

    @Override
    public Void parser(int affectedRows) {
        return null;
    }
}
