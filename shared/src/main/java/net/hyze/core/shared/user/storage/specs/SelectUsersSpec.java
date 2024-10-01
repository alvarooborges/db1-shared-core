package net.hyze.core.shared.user.storage.specs;

import com.google.common.collect.Lists;
import net.hyze.core.shared.user.User;
import java.sql.ResultSet;
import java.util.List;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import java.sql.Timestamp;
import java.util.UUID;
import org.springframework.jdbc.core.ResultSetExtractor;

public abstract class SelectUsersSpec extends SelectSqlSpec<List<User>> {

    @Override
    public ResultSetExtractor<List<User>> getResultSetExtractor() {
        return (ResultSet result) -> {
            List<User> out = Lists.newArrayList();

            while (result.next()) {

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

                out.add(user);
            }

            return out;
        };
    }
}
