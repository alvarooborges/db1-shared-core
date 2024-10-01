package net.hyze.core.shared.user.storage.specs.cash;

import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public class SelectCashByUserSpec extends SelectSqlSpec<Integer> {

    private final User user;

    @Override
    public ResultSetExtractor<Integer> getResultSetExtractor() {
        return result -> {
            if (result.next()) {
                return result.getInt("cash");
            }

            return 0;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "SELECT `cash` FROM `users` WHERE `id` = ? LIMIT 1;"
            );

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, user.getId());

            return statement;
        };
    }

}
