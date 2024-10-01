package net.hyze.core.shared.skins.storage.specs;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.skins.SkinRecord;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class DeleteSkinRecordSpec extends InsertSqlSpec<Void> {

    private final User user;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(String.format(
                    "DELETE FROM `%s` WHERE `user_id` = ?;",
                    CoreConstants.Databases.Mysql.Tables.SKINS
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, this.user.getId());

            return statement;
        };

    }

}
