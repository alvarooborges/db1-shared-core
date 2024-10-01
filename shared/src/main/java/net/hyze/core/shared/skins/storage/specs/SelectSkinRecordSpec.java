package net.hyze.core.shared.skins.storage.specs;

import com.google.common.base.Enums;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.skins.Skin;
import net.hyze.core.shared.skins.SkinRecord;
import net.hyze.core.shared.skins.SkinRecordType;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public class SelectSkinRecordSpec extends SelectSqlSpec<SkinRecord> {

    private final User user;

    @Override
    public ResultSetExtractor<SkinRecord> getResultSetExtractor() {

        return (ResultSet result) -> {

            while (result.next()) {

                int id = result.getInt("id");
                int userId = result.getInt("user_id");

                String nick = result.getString("nick");
                String skinValue = result.getString("skin_value");
                String skinSignature = result.getString("skin_signature");
                SkinRecordType recordType = (SkinRecordType) Enums.getIfPresent(SkinRecordType.class, result.getString("record_type")).or(SkinRecordType.CUSTOM);
                Date updatedAt = result.getDate("updated_at");

                return new SkinRecord(id, nick, new Skin(skinValue, skinSignature), recordType, updatedAt);

            }

            return null;

        };

    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `user_id` = ?;",
                    CoreConstants.Databases.Mysql.Tables.SKINS
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, this.user.getId());

            return statement;

        };

    }

}
