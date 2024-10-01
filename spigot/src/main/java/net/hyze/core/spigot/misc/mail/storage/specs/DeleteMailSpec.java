package net.hyze.core.spigot.misc.mail.storage.specs;

import net.hyze.core.shared.storage.repositories.specs.DeleteSqlSpec;
import net.hyze.core.spigot.misc.mail.MailConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class DeleteMailSpec extends DeleteSqlSpec<Boolean> {

    private final int id;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows != 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "DELETE FROM `%s` WHERE `id` = ?;";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(
                            query,
                            MailConstants.Databases.Mysql.Tables.MAIL_TABLE_NAME
                    )
            );

            statement.setInt(1, this.id);

            return statement;
        };
    }

}
