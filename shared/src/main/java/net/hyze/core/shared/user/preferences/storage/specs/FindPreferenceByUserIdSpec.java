package net.hyze.core.shared.user.preferences.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.shared.user.preferences.UserPreference;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public class FindPreferenceByUserIdSpec extends SelectSqlSpec<UserPreference> {

    private final int id;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `user_id` = ?;",
                    CoreConstants.Databases.Mysql.Tables.PREFERENCE_USER_TABLE_NAME
            ));

            statement.setInt(1, this.id);

            return statement;
        };
    }

    @Override
    public ResultSetExtractor<UserPreference> getResultSetExtractor() {
        return (ResultSet result) -> {
            Table<Server, String, PreferenceStatus> serverPreferences = HashBasedTable.create();
            Map<String, PreferenceStatus> networkPreferences = Maps.newHashMap();

            try {
                while (result.next()) {

                    String serverRaw = result.getString("server");

                    if (serverRaw.equalsIgnoreCase(UserPreference.GENERIC_PREFERENCE_SERVER)) {
                        networkPreferences.put(
                                result.getString("preference"),
                                Enums.getIfPresent(PreferenceStatus.class, result.getString("status")).or(PreferenceStatus.UNSET)
                        );
                    } else {
                        Server server = Enums.getIfPresent(Server.class, serverRaw).orNull();

                        if (server != null) {
                            serverPreferences.put(
                                    server,
                                    result.getString("preference"),
                                    Enums.getIfPresent(PreferenceStatus.class, result.getString("status")).or(PreferenceStatus.UNSET)
                            );
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getGlobal().log(Level.SEVERE, null, ex);
            }

            return new UserPreference(serverPreferences, networkPreferences);
        };
    }
}
