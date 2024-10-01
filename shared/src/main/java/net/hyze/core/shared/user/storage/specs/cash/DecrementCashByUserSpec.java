package net.hyze.core.shared.user.storage.specs.cash;

import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class DecrementCashByUserSpec extends UpdateSqlSpec<Boolean> {

    private final User user;
    private final int amount;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows > 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "UPDATE `users` SET `cash` = `cash` - ? WHERE `id` = ? AND `cash` >= ? LIMIT 1;"
            );

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, amount);
            statement.setInt(2, user.getId());
            statement.setInt(3, amount);

            return statement;
        };
    }

}
