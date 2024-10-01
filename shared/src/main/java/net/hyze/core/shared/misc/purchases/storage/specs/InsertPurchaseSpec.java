package net.hyze.core.shared.misc.purchases.storage.specs;

import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.core.shared.misc.purchases.Purchase;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class InsertPurchaseSpec extends InsertSqlSpec<InsertPurchaseSpec.Response> {

    private final Purchase purchase;

    @Override
    public Response parser(int affectedRows, KeyHolder keyHolder) {

        if (keyHolder.getKey() != null) {
            return Response.SUCCESS;
        }

        return Response.DUPLICATED;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            StringBuilder builder = new StringBuilder()
                    .append("INSERT INTO `purchases` (`user_id`, `server_id`, `type`, `value`, `cycle`, `transaction`, ")
                    .append("`quantity`, `currency`, `paid_price`, `original_price`, `ip`, `email`, `activation_state`, `announcement_state`) ")
                    .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ")
                    .append("ON DUPLICATE KEY UPDATE `user_id`=`user_id`;");

            PreparedStatement statement = connection.prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, purchase.getUserId());

            if (purchase.getServer() != null) {
                statement.setString(2, purchase.getServer().getId());
            } else {
                statement.setNull(2, Types.VARCHAR);
            }

            statement.setString(3, purchase.getType().name());
            statement.setString(4, purchase.getValue());
            statement.setInt(5, purchase.getCycle());
            statement.setString(6, purchase.getTransaction());
            statement.setInt(7, purchase.getQuantity());
            statement.setString(8, purchase.getCurrency());
            statement.setDouble(9, purchase.getPaidPrice());
            statement.setDouble(10, purchase.getOriginalPrice());
            statement.setString(11, purchase.getIp());
            statement.setString(12, purchase.getEmail());
            statement.setString(13, purchase.getActivationState().name());
            statement.setString(14, purchase.getAnnouncementState().name());

            return statement;
        };
    }

    public enum Response {
        DUPLICATED, SUCCESS;
    }
}
