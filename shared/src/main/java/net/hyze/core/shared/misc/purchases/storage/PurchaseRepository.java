package net.hyze.core.shared.misc.purchases.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.misc.purchases.Purchase;
import net.hyze.core.shared.misc.purchases.PurchaseState;
import net.hyze.core.shared.misc.purchases.storage.specs.InsertPurchaseSpec;
import net.hyze.core.shared.misc.purchases.storage.specs.SelectAnnouncementPendingByUserSpec;
import net.hyze.core.shared.misc.purchases.storage.specs.SelectPurchasesByUserSpec;
import net.hyze.core.shared.misc.purchases.storage.specs.UpdateActivationStateSpec;
import net.hyze.core.shared.misc.purchases.storage.specs.UpdateAnnouncementStateSpec;
import net.hyze.core.shared.user.User;
import java.util.List;

public class PurchaseRepository extends MysqlRepository {

    public PurchaseRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public InsertPurchaseSpec.Response insert(Purchase purchase) {
        return query(new InsertPurchaseSpec(purchase));
    }

    public List<Purchase> getAnnouncementPendingPurchases(User user) {
        return query(new SelectAnnouncementPendingByUserSpec(user, PurchaseState.PENDING));
    }

    public boolean updateAnnouncementState(Purchase purchase, PurchaseState state) {
        return query(new UpdateAnnouncementStateSpec(purchase, state));
    }

    public List<Purchase> getPenddingPurchases(User user) {
        return query(new SelectPurchasesByUserSpec(user, PurchaseState.PENDING));
    }

    public boolean updateActivationState(Purchase purchase, PurchaseState state) {
        return query(new UpdateActivationStateSpec(purchase, state));
    }

}
