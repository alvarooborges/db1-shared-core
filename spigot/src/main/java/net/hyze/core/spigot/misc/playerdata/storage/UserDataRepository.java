package net.hyze.core.spigot.misc.playerdata.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.playerdata.storage.specs.SelectUserDataSpec;
import net.hyze.core.spigot.misc.playerdata.storage.specs.InsertOrUpdateUserDataSpec;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class UserDataRepository extends MysqlRepository {

    public UserDataRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void update(User user, @NonNull NBTTagCompound compound) {
        query(new InsertOrUpdateUserDataSpec(user, compound));
    }

    public NBTTagCompound fetch(@NonNull User user) {
        return query(new SelectUserDataSpec(user));
    }
}
