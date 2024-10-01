package net.hyze.core.shared.group.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.group.Group;
import java.sql.ResultSet;
import java.util.List;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ChatColor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

public class SelectAllGroupsSpec extends SelectSqlSpec<List<Group>> {

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s`;",
                    CoreConstants.Databases.Mysql.Tables.GROUPS_TABLE_NAME
            ));

            return statement;
        };
    }

    @Override
    public ResultSetExtractor<List<Group>> getResultSetExtractor() {
        return (ResultSet result) -> {
            List<Group> out = Lists.newArrayList();

            while (result.next()) {
                String groupId = result.getString("id");

                Group group = Enums.getIfPresent(Group.class, groupId).orNull();

                if (group != null) {
                    group.setDisplayNameRaw(result.getString("display_name"));
                    group.setTagRaw(result.getString("tag"));
                    group.setPriority(result.getInt("priority"));
                    group.setColor(Enums.getIfPresent(ChatColor.class, result.getString("color")).or(ChatColor.GRAY));
                }
            }

            return out.stream()
                    .sorted((Group o1, Group o2) -> Ints.compare(o2.getPriority(), o1.getPriority()))
                    .collect(Collectors.toCollection(LinkedList::new));
        };
    }
}
