package net.hyze.core.shared.dungeon.storage.specs.data;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class DecrementRessurectionPotionSpec extends UpdateSqlSpec<Void> {

    private final int userId;

    private final int amount;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("INSERT INTO %s (`user_id`,`ressurection_potions`) VALUES (?,0) ON DUPLICATE KEY UPDATE `ressurection_potions`=GREATEST(`ressurection_potions`-%d, 0);",
                            CoreConstants.Databases.Mysql.Tables.DUNGEONS_USER_DATA_TABLE_NAME, this.amount));
            statement.setInt(1, this.userId);
            return statement;
        };
    }

    @Override
    public Void parser(int affectedRows) {
        return null;
    }
}
