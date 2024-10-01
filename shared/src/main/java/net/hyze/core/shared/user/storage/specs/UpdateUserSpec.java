package net.hyze.core.shared.user.storage.specs;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class UpdateUserSpec extends UpdateSqlSpec<Void> {

    private final int id;
    private final UUID uuid;
    private final String password;
    private String email;

    public UpdateUserSpec(User user) {
        this.id = user.getId();
        this.uuid = user.getUuid();
        this.password = user.getPassword();
        this.email = user.getEmail();
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(String.format(
                    "UPDATE `%s` SET `uuid` = ?, `password` = ?, `email` = ? WHERE `id` = ?;",
                    CoreConstants.Databases.Mysql.Tables.USERS_TABLE_NAME
            ));

            statement.setString(1, this.uuid == null ? null : this.uuid.toString());
            statement.setString(2, this.password);
            statement.setString(3, this.email);
            statement.setInt(4, this.id);

            return statement;
        };
    }

    @Override
    public Void parser(int affectedRows) {
        return null;
    }
}
