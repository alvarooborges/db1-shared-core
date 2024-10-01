package net.hyze.core.shared.group.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.user.User;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class InsertGroupSpec extends UpdateSqlSpec<Boolean> {

    private final User user;
    private final Group group;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows > 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = "INSERT INTO `user_groups` "
                    + "(`user_id`, `group_id`) VALUES (?, ?) "
                    + "ON DUPLICATE KEY UPDATE `user_id` = `user_id`;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, user.getId());
            statement.setString(2, group.name());

            return statement;
        };
    }

}
