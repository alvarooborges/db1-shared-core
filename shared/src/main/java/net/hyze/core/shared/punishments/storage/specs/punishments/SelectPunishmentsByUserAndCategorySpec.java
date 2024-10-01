package net.hyze.core.shared.punishments.storage.specs.punishments;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.punishments.PunishmentCategory;
import net.hyze.core.shared.user.User;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class SelectPunishmentsByUserAndCategorySpec extends SelectPunishmentsSpec {

    private final User user;
    private final PunishmentCategory category;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return connection -> {
            PreparedStatement statement;
            if (this.category != null) {
                statement = connection.prepareStatement(String.format(
                        "SELECT * FROM `%s` WHERE `user_id` = ? AND `category` = ?;",
                        CoreConstants.Databases.Mysql.Tables.PUNISHMENTS_TABLE_NAME
                ));

                statement.setInt(1, this.user.getId());
                statement.setString(2, this.category.getName());
            } else {
                statement = connection.prepareStatement(String.format(
                        "SELECT * FROM `%s` WHERE `user_id` = ?;",
                        CoreConstants.Databases.Mysql.Tables.PUNISHMENTS_TABLE_NAME
                ));

                statement.setInt(1, this.user.getId());
            }

            return statement;
        };

    }

}
