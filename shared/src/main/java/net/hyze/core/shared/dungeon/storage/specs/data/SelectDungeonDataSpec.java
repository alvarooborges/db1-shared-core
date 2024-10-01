package net.hyze.core.shared.dungeon.storage.specs.data;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.dungeon.DungeonUserData;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.util.Optional;

@RequiredArgsConstructor
public class SelectDungeonDataSpec extends SelectSqlSpec<DungeonUserData> {

    private final int userId;

    @Override
    public ResultSetExtractor<DungeonUserData> getResultSetExtractor() {
        return result -> {
            DungeonUserData userData = new DungeonUserData(this.userId);

            if (result.next()) {
                int potions = Optional.of(result.getInt("ressurection_potions")).orElse(0);
                userData.giveRessurectionPotions(potions);
            }

            return userData;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("SELECT * FROM %s WHERE `user_id`=?;",
                            CoreConstants.Databases.Mysql.Tables.DUNGEONS_USER_DATA_TABLE_NAME));
            statement.setInt(1, this.userId);
            return statement;
        };
    }

}
