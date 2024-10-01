package net.hyze.core.shared.config.storage;

import net.hyze.core.shared.config.storage.specs.SelectAllKeysSpec;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import java.util.Map;

public class ConfigRepository extends MysqlRepository {

    public ConfigRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public Map<String, String> fetch() {
        return query(new SelectAllKeysSpec());
    }
    
}
