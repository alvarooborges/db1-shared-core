package net.hyze.core.shared.group.due.storage;

import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.group.due.GroupDue;
import net.hyze.core.shared.group.due.storage.specs.DeleteGroupDueSpec;
import net.hyze.core.shared.group.due.storage.specs.InsertGroupDueSpec;
import net.hyze.core.shared.group.due.storage.specs.SelectAllGroupDueSpec;
import net.hyze.core.shared.group.due.storage.specs.SelectGroupDueSpec;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;

import java.util.List;

public class GroupDueRepository extends MysqlRepository {

    public GroupDueRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public List<GroupDue> fetch(User user) {
        return query(new SelectGroupDueSpec(user));
    }

    public List<GroupDue> fetchAll(User user) {
        return query(new SelectAllGroupDueSpec(user));
    }

    public boolean insert(User user, Group group, int days) {
        return query(new InsertGroupDueSpec(user, group, days));
    }

    public boolean delete(User user, Group group) {
        return query(new DeleteGroupDueSpec(user, group));
    }
}
