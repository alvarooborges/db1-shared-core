package net.hyze.core.spigot.misc.mail.storage.specs;

import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.core.spigot.misc.mail.Mail;
import net.hyze.core.spigot.misc.mail.MailConstants;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class InsertMailSpec extends InsertSqlSpec<Void> {
    
    private final Mail mail;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {        
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format(
                "INSERT INTO `%s` (`receiver_id`, `mail_type`, `sender`, `message`, `item`, `created_at`) VALUES (?, ?, ?, ?, ?, ?)",
                MailConstants.Databases.Mysql.Tables.MAIL_TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, this.mail.getReceiverId());
            statement.setString(2, this.mail.getType());
            statement.setString(3, this.mail.getSender());
            statement.setString(4, this.mail.getMessage());
            statement.setString(5, InventoryUtils.serializeContents(new ItemStack[]{this.mail.getItem()}));
            statement.setTimestamp(6, new Timestamp(this.mail.getCreatedAt()));

            return statement;
        };
    }
}
