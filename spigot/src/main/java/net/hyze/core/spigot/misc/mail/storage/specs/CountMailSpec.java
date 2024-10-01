package net.hyze.core.spigot.misc.mail.storage.specs;

import java.sql.PreparedStatement;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.spigot.misc.mail.MailConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public class CountMailSpec extends SelectSqlSpec<Map<String, Integer>> {

    private final int userId;
    private final String type;

    @Override
    public ResultSetExtractor<Map<String, Integer>> getResultSetExtractor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connetion -> {
            String query = "SELECT `sender`, COUNT(*) AS 'count' FROM `%s` WHERE `receiver_id` = ? AND `mail_type` = ? GROUP BY `sender`;";

            PreparedStatement statement = connetion.prepareStatement(String.format(
                    query,
                    MailConstants.Databases.Mysql.Tables.MAIL_TABLE_NAME
            ));

            statement.setInt(1, this.userId);
            statement.setString(2, this.type);

            return statement;
        };
    }

}
