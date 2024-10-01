package net.hyze.core.shared.group.due.storage.specs;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.group.due.GroupDue;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.user.User;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
public class SelectAllGroupDueSpec extends SelectSqlSpec<List<GroupDue>> {

    private final User user;

    @Override
    public ResultSetExtractor<List<GroupDue>> getResultSetExtractor() {
        return result -> {
            List<GroupDue> out = Lists.newArrayList();

            while (result.next()) {
                String groupId = result.getString("group_id");
                Timestamp dueAt = result.getTimestamp("due_at");

                if (Group.getById(groupId).isPresent()) {

                    out.add(new GroupDue(
                            user.getId(),
                            Group.getById(groupId).get(),
                            dueAt
                    ));
                }

            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = "SELECT * FROM `user_groups_due` WHERE `user_id` = ?;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, user.getId());

            return statement;
        };
    }

}
