package net.hyze.core.shared.dungeon.storage;

import net.hyze.core.shared.dungeon.BasicDungeonMap;
import net.hyze.core.shared.dungeon.storage.specs.SelectAllDungeonMapsSpec;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;

import java.util.List;

public class DungeonRepository<DM extends BasicDungeonMap> extends MysqlRepository {

    public Class<DM> clazz;

    public DungeonRepository(MysqlDatabaseProvider databaseProvider/*, Class<DM> clazz)*/) {
        super(databaseProvider);
    }

    public List<DM> fetchAllMaps() {
        return query(new SelectAllDungeonMapsSpec<>(clazz));
    }

}
