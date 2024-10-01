package net.hyze.core.shared.group.storage;

import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.group.storage.specs.DeleteGroupSpec;
import net.hyze.core.shared.group.storage.specs.InsertGroupSpec;
import net.hyze.core.shared.group.storage.specs.SelectAllGroupsSpec;
import net.hyze.core.shared.group.storage.specs.SelectGroupsByUserSpec;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class GroupRepository extends MysqlRepository {

    public GroupRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void populate() {
        query(new SelectAllGroupsSpec());
    }

    public EnumSet<Group> fetchByUser(User user) {
        return query(new SelectGroupsByUserSpec(user));
    }

    public boolean addGroup(User user, Group group) {
        return query(new InsertGroupSpec(user, group));
    }

    public boolean removeGroup(User user, Group group) {
        return query(new DeleteGroupSpec(user, group));
    }
}
