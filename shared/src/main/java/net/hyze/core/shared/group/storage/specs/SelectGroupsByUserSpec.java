package net.hyze.core.shared.group.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.user.User;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.EnumSet;
import java.util.List;

@RequiredArgsConstructor
public class SelectGroupsByUserSpec extends SelectSqlSpec<EnumSet<Group>> {

    private final User user;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `user_id` = ?;",
                    CoreConstants.Databases.Mysql.Tables.GROUP_USER_TABLE_NAME
            );

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, this.user.getId());

            return statement;
        };
    }

    @Override
    public ResultSetExtractor<EnumSet<Group>> getResultSetExtractor() {
        return (ResultSet rs) -> {

            EnumSet<Group> out = EnumSet.noneOf(Group.class);

            while (rs.next()) {
                String groupId = rs.getString("group_id");

                Group group = Enums.getIfPresent(Group.class, groupId).orNull();

                if (group != null) {
                    out.add(group);
                }
            }

            return out;
        };
    }
}
