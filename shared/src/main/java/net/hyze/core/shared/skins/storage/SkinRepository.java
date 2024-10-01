package net.hyze.core.shared.skins.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.skins.SkinRecord;
import net.hyze.core.shared.skins.storage.specs.DeleteSkinRecordSpec;
import net.hyze.core.shared.skins.storage.specs.InsertOrUpdateSkinRecordSpec;
import net.hyze.core.shared.skins.storage.specs.SelectSkinRecordSpec;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;

public class SkinRepository extends MysqlRepository {

    public SkinRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }
    
    public SkinRecord fetch(User user) {
        return query(new SelectSkinRecordSpec(user));
    }
    
    public SkinRecord insertOrUpdate(User user, SkinRecord skinRecord) {
        return query(new InsertOrUpdateSkinRecordSpec(user, skinRecord));
    }
    
    public void clear(User user) {
        query(new DeleteSkinRecordSpec(user));
    }

}
