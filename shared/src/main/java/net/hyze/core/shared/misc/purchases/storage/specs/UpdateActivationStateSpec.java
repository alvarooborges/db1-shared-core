package net.hyze.core.shared.misc.purchases.storage.specs;

import net.hyze.core.shared.misc.purchases.Purchase;
import net.hyze.core.shared.misc.purchases.PurchaseState;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class UpdateActivationStateSpec extends UpdateSqlSpec<Boolean> {

    private final Purchase purchase;
    private final PurchaseState activationState;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows > 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = "UPDATE `purchases` SET `activation_state` = ? WHERE id = ? LIMIT 1;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, activationState.name());
            statement.setInt(2, purchase.getId());

            return statement;
        };
    }

}
