package net.hyze.core.shared.apps.storage;

import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.storage.specs.SelectAllAppsSpec;
import net.hyze.core.shared.apps.storage.specs.SelectAppByIdSpec;
import net.hyze.core.shared.apps.storage.specs.SelectAppByPortSpec;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import java.util.List;

public class AppsRepository extends MysqlRepository {

    public AppsRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public App fetchById(String id) {
        return query(new SelectAppByIdSpec(id));
    }

    public App fetchByPort(int port) {
        return query(new SelectAppByPortSpec(port));
    }

    public List<App> fetchAll() {
        return query(new SelectAllAppsSpec());
    }
}
