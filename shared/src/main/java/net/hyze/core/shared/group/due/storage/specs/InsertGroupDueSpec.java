package net.hyze.core.shared.group.due.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.user.User;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class InsertGroupDueSpec extends UpdateSqlSpec<Boolean> {

    private final User user;
    private final Group group;
    private final int days;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows > 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = "INSERT INTO `user_groups_due` (`user_id`, `group_id`, `due_at`) "
                    + "VALUES (?, ?, NOW() + INTERVAL ? DAY) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "`due_at` = `due_at` + INTERVAL ? DAY;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, user.getId());
            statement.setString(2, group.name());
            statement.setInt(3, days);
            statement.setInt(4, days);

            return statement;
        };
    }

}
