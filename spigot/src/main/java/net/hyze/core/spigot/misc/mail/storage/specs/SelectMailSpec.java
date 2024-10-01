package net.hyze.core.spigot.misc.mail.storage.specs;

import com.google.common.collect.Lists;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.spigot.misc.mail.Mail;
import net.hyze.core.spigot.misc.mail.MailConstants;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public class SelectMailSpec extends SelectSqlSpec<LinkedList<Mail>> {

    private final int userId;
    private final String type;

    @Override
    public ResultSetExtractor<LinkedList<Mail>> getResultSetExtractor() {
        return (ResultSet result) -> {

            LinkedList<Mail> mails = Lists.newLinkedList();

            while (result.next()) {

                Mail mail = new Mail(
                        result.getInt("receiver_id"), 
                        result.getString("mail_type"), 
                        result.getTimestamp("created_at").getTime(), 
                        InventoryUtils.deserializeContents(result.getString("item"))[0], 
                        result.getString("sender"), 
                        result.getString("message")
                );
                                
                mail.setId(result.getInt("id"));
                
                mails.add(mail);

            }

            return mails;

        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "SELECT * FROM `%s` WHERE `receiver_id`=? AND `mail_type`=?;";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(
                            query,
                            MailConstants.Databases.Mysql.Tables.MAIL_TABLE_NAME
                    )
            );

            statement.setInt(1, this.userId);
            statement.setString(2, this.type);
            return statement;
        };
    }

}
