package net.hyze.core.shared.punishments.storage.specs.punishments;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.punishments.PunishmentState;
import net.hyze.core.shared.punishments.PunishmentType;
import net.hyze.core.shared.user.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public class SelectActivePunishmentByUserSpec extends SelectPunishmentsSpec {

    private final User user;
    private final PunishmentType type;
    
    @Override
    public ResultSetExtractor<Set<Punishment>> getResultSetExtractor() {
        return (ResultSet result) -> {           
            
            return (Set<Punishment>) super.getResultSetExtractor().extractData(result)
                    .stream()
                    .filter(punishment -> punishment.getState().equals(PunishmentState.ACTIVE))
                    .collect(Collectors.toSet());
            
        };
        
    }
    
    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection connection) -> {

            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `user_id` = ? AND `type` = ? AND `revoker_user_id` IS NULL ORDER BY `created_at` DESC LIMIT 1;",
                    CoreConstants.Databases.Mysql.Tables.PUNISHMENTS_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, this.user.getId());
            statement.setString(2, this.type.getName());

            return statement;

        };

    }
    
}