package net.hyze.core.shared.config.storage.specs;

import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

public class SelectAllKeysSpec extends SelectSqlSpec<Map<String, String>> {

    @Override
    public ResultSetExtractor<Map<String, String>> getResultSetExtractor() {

        return (ResultSet result) -> {

            Map<String, String> output = Maps.newHashMap();
            
            while (result.next()) {

                int id = result.getInt("id");
                String key = result.getString("key");
                String value = Optional.ofNullable(result.getString("value")).orElse(result.getString("default_value"));

                output.put(key, value);
                
            }

            return output;

        };

    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {
            return connection.prepareStatement(String.format("SELECT * FROM `%s`;", CoreConstants.Databases.Mysql.Tables.CONFIG_TABLE_NAME));
        };

    }

}
