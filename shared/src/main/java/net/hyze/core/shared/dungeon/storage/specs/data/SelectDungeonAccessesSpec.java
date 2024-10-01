package net.hyze.core.shared.dungeon.storage.specs.data;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.dungeon.DungeonMapAccess;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.util.Map;

@RequiredArgsConstructor
public class SelectDungeonAccessesSpec extends SelectSqlSpec<Map<String, DungeonMapAccess>> {

    private final int userId;

    @Override
    public ResultSetExtractor<Map<String, DungeonMapAccess>> getResultSetExtractor() {
        return result -> {
            Map<String, DungeonMapAccess> userData = Maps.newHashMap();

            while (result.next()) {
                DungeonMapAccess access = new DungeonMapAccess(result.getString("map_id"), result.getInt("accesses"), result.getBoolean("unlocked"), result.getDate("updated_at"));
                userData.put(access.getMapId(), access);
            }

            return userData;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("SELECT * FROM %s WHERE `user_id`=?;",
                            CoreConstants.Databases.Mysql.Tables.DUNGEONS_USER_ACCESSES_TABLE_NAME));
            statement.setInt(1, this.userId);
            return statement;
        };
    }

}
