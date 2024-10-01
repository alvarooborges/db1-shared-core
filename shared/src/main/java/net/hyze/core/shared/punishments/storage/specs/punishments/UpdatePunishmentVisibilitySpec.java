package net.hyze.core.shared.punishments.storage.specs.punishments;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class UpdatePunishmentVisibilitySpec extends UpdateSqlSpec<Void> {

    private final Punishment punishment;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            PreparedStatement statement = connection.prepareStatement(String.format(
                    "UPDATE `%s` SET `hidden` = ? WHERE `id` = ?",
                    CoreConstants.Databases.Mysql.Tables.PUNISHMENTS_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setBoolean(1, this.punishment.isHidden());
            statement.setInt(2, this.punishment.getId());
            
            return statement;

        };

    }

}
