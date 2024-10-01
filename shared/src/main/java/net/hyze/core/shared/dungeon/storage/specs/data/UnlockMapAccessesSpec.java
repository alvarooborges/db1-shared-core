package net.hyze.core.shared.dungeon.storage.specs.data;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class UnlockMapAccessesSpec extends UpdateSqlSpec<Void> {

    private final int userId;

    private final String mapId;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("INSERT INTO %s (`user_id`,`map_id`,`accesses`,`unlocked`) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE `accesses`=`accesses`+VALUES(`accesses`), `unlocked`=VALUES(`unlocked`);",
                            CoreConstants.Databases.Mysql.Tables.DUNGEONS_USER_ACCESSES_TABLE_NAME));
            statement.setInt(1, this.userId);
            statement.setString(2, this.mapId);
            statement.setInt(3, 0);
            statement.setBoolean(4, true);
            return statement;
        };
    }

    @Override
    public Void parser(int affectedRows) {
        return null;
    }
}
