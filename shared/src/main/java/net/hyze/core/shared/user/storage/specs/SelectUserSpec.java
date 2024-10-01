package net.hyze.core.shared.user.storage.specs;

import net.hyze.core.shared.user.User;
import java.sql.ResultSet;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import java.sql.Timestamp;
import java.util.UUID;
import org.springframework.jdbc.core.ResultSetExtractor;

public abstract class SelectUserSpec extends SelectSqlSpec<User> {

    @Override
    public ResultSetExtractor<User> getResultSetExtractor() {
        return (ResultSet result) -> {

            if (result.next()) {
                int id = result.getInt("id");
                String nick = result.getString("nick");
                String uuid = result.getString("uuid");
                String password = result.getString("password");
                String email = result.getString("email");
                Timestamp emailVerifiedAt = result.getTimestamp("email_verified_at");
                Timestamp createdAt = result.getTimestamp("created_at");

                User user = new User(
                        id,
                        nick,
                        uuid == null ? null : UUID.fromString(uuid),
                        password,
                        email,
                        emailVerifiedAt,
                        createdAt
                );

                return user;
            }

            return null;
        };
    }
}
