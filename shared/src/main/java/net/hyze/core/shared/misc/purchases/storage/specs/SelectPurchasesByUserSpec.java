package net.hyze.core.shared.misc.purchases.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.Lists;
import net.hyze.core.shared.misc.purchases.Purchase;
import net.hyze.core.shared.misc.purchases.PurchaseState;
import net.hyze.core.shared.misc.purchases.PurchaseType;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.user.User;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public class SelectPurchasesByUserSpec extends SelectSqlSpec<List<Purchase>> {

    private final User user;
    private final PurchaseState activationState;

    @Override
    public ResultSetExtractor<List<Purchase>> getResultSetExtractor() {
        return result -> {
            List<Purchase> out = Lists.newArrayList();

            while (result.next()) {
                try {
                    Purchase purchase = new Purchase(
                            result.getInt("id"),
                            result.getInt("user_id"),
                            Server.getById(result.getString("server_id")).orNull(),
                            Enums.getIfPresent(PurchaseType.class, result.getString("type")).get(),
                            result.getString("value"),
                            result.getInt("cycle"),
                            result.getString("transaction"),
                            result.getInt("quantity"),
                            result.getString("currency"),
                            result.getDouble("paid_price"),
                            result.getDouble("original_price"),
                            result.getString("ip"),
                            result.getString("email"),
                            Enums.getIfPresent(PurchaseState.class, result.getString("activation_state")).get(),
                            Enums.getIfPresent(PurchaseState.class, result.getString("announcement_state")).get(),
                            result.getTimestamp("created_at")
                    );

                    out.add(purchase);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = "SELECT * FROM `purchases` WHERE `user_id` = ? AND `activation_state` = ?;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, user.getId());
            statement.setString(2, activationState.name());

            return statement;
        };
    }

}
