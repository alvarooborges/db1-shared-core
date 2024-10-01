package net.hyze.core.shared.skins.storage.specs;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.skins.SkinRecord;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class InsertOrUpdateSkinRecordSpec extends InsertSqlSpec<SkinRecord> {

    private final User user;
    private final SkinRecord skinRecord;

    @Override
    public SkinRecord parser(int affectedRows, KeyHolder keyHolder) {

        if (affectedRows != 1) {
            return null;
        }

        this.skinRecord.setId(keyHolder.getKey().intValue());
        return this.skinRecord;

    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(String.format(
                    "INSERT INTO `%s` (`user_id`, `nick`, `skin_value`, `skin_signature`, `record_type`, `updated_at`) VALUE (?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "`nick` = VALUES(`nick`), `skin_value` = VALUES(`skin_value`), `skin_signature` = VALUES(`skin_signature`), `record_type` = VALUES(`record_type`), `updated_at` = VALUES(`updated_at`);",
                    CoreConstants.Databases.Mysql.Tables.SKINS
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, this.user.getId());
            statement.setString(2, this.skinRecord.getNick());
            statement.setString(3, this.skinRecord.getSkin().getValue());
            statement.setString(4, this.skinRecord.getSkin().getSignature());
            statement.setString(5, this.skinRecord.getType().name());
            statement.setTimestamp(6, new Timestamp(this.skinRecord.getUpdatedAt().getTime()));

            return statement;
        };

    }

}
