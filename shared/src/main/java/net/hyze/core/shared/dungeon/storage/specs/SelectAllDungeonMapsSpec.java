package net.hyze.core.shared.dungeon.storage.specs;

import com.google.common.collect.Lists;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.dungeon.BasicDungeonMap;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.util.List;

@RequiredArgsConstructor
public class SelectAllDungeonMapsSpec<DM extends BasicDungeonMap> extends SelectSqlSpec<List<DM>> {

    private final Class<DM> clazz;

    @Override
    public ResultSetExtractor<List<DM>> getResultSetExtractor() {
        return (result) -> {
            List<DM> list = Lists.newArrayList();

            try {
                while(result.next()) {
                    list.add(clazz.getConstructor(ResultSet.class).newInstance(result));
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            return list;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            return connection.prepareStatement(String.format(
                    "SELECT * FROM `%s`;",
                    CoreConstants.Databases.Mysql.Tables.DUNGEONS_TABLE_NAME
            ));
        };
    }
}
