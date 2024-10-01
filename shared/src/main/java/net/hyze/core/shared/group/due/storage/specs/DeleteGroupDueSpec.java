package net.hyze.core.shared.group.due.storage.specs;

import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class DeleteGroupDueSpec extends UpdateSqlSpec<Boolean> {

    private final User user;
    private final Group group;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows > 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = "DELETE FROM `user_groups_due` WHERE `user_id` = ? AND `group_id` = ?;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, user.getId());
            statement.setString(2, group.name());

            return statement;
        };
    }

}
