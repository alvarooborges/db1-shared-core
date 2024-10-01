package net.hyze.core.shared.user.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.user.User;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class SelectAssociateUsersByUserSpec extends SelectUsersSpec {

    private final User user;
    private final int days;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = String.format(
                    "SELECT `users`.* FROM `user_sessions` " +
                            "JOIN `users` ON `users`.`id` = `user_sessions`.`user_id` " +
                            "WHERE `logged` = 1 " +
                            "AND `started_at` >= DATE_SUB(NOW(), INTERVAL 24 * %s HOUR) " +
                            "AND `ip` IN (SELECT `ip` FROM `user_sessions` WHERE `user_id` = ? AND `logged` = 1) " +
                            "GROUP BY `user_id`;",
                    days
            );

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, user.getId());

            return statement;
        };
    }
}
