package net.hyze.core.shared.servers.storage;

import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.servers.storage.specs.SelectAllServersSpec;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import java.util.List;

public class ServersRepository extends MysqlRepository {

    public ServersRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public List<Server> fetchAll() {
        return query(new SelectAllServersSpec());
    }
}
