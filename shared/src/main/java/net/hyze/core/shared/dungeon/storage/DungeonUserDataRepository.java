package net.hyze.core.shared.dungeon.storage;

import net.hyze.core.shared.dungeon.BasicDungeonMap;
import net.hyze.core.shared.dungeon.DungeonMapAccess;
import net.hyze.core.shared.dungeon.DungeonUserData;
import net.hyze.core.shared.dungeon.storage.specs.data.*;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;

import java.util.Map;

public class DungeonUserDataRepository extends MysqlRepository {

    public DungeonUserDataRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public DungeonUserData fetchData(User user) {
        return query(new SelectDungeonDataSpec(user.getId()));
    }

    public Map<String, DungeonMapAccess> fetchAccesses(User user) {
        return query(new SelectDungeonAccessesSpec(user.getId()));
    }

    public void decrementMapAccesses(User user, BasicDungeonMap map, int amount) {
        query(new DecrementMapAccessesSpec(user.getId(), map.getId(), amount));
    }

    public void unlockMapAccesses(User user, BasicDungeonMap map) {
        query(new UnlockMapAccessesSpec(user.getId(), map.getId()));
    }

    public void incrementMapAccesses(User user, BasicDungeonMap map, int amount) {
        query(new IncrementMapAccessesSpec(user.getId(), map.getId(), amount));
    }

    public void decrementRessurectionPotions(User user, int amount) {
        query(new DecrementRessurectionPotionSpec(user.getId(), amount));
    }

    public void incrementRessurectionPotions(User user, int amount) {
        query(new IncrementRessurectionPotionSpec(user.getId(), amount));
    }
}
