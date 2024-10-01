package net.hyze.core.shared.dungeon.storage.specs.data;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Date;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class IncrementMapAccessesSpec extends UpdateSqlSpec<Void> {

    private final int userId;

    private final String mapId;

    private final int amount;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("INSERT INTO %s (`user_id`,`map_id`,`accesses`,`unlocked`,`updated_at`) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE `accesses`=`accesses`+VALUES(`accesses`), `updated_at`=VALUES(`updated_at`);",
                            CoreConstants.Databases.Mysql.Tables.DUNGEONS_USER_ACCESSES_TABLE_NAME));
            statement.setInt(1, this.userId);
            statement.setString(2, this.mapId);
            statement.setInt(3, this.amount);
            statement.setBoolean(4, false);
            statement.setDate(5, new Date(System.currentTimeMillis()));
            return statement;
        };
    }

    @Override
    public Void parser(int affectedRows) {
        return null;
    }
}
