package net.hyze.core.shared.user.preferences.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.shared.user.preferences.UserPreference;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class InsertOrUpdatePreferenceByUserIdSpec extends UpdateSqlSpec<Boolean> {

    private final String preferenceId;
    private final PreferenceStatus status;
    private final int userId;
    
    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement;

            statement = connection.prepareStatement(String.format(
                    "INSERT INTO `%s` (`status`, `preference`, `server`, `user_id`) "
                    + "VALUES(?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE"
                    + "`status` = VALUES(`status`) ",
                    CoreConstants.Databases.Mysql.Tables.PREFERENCE_USER_TABLE_NAME
            ));

            statement.setString(1, this.status.name());
            statement.setString(2, this.preferenceId);
            
            Server server = CoreProvider.getApp().getServer();

            if (server == null) {
                statement.setString(3, UserPreference.GENERIC_PREFERENCE_SERVER);
            } else {
                statement.setString(3, server.name());
            }

            statement.setInt(4, this.userId);

            return statement;
        };
    }

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows > 0;
    }
}
