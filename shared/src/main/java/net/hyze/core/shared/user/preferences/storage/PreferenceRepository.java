package net.hyze.core.shared.user.preferences.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.shared.user.preferences.UserPreference;
import net.hyze.core.shared.user.preferences.storage.specs.FindPreferenceByUserIdSpec;
import net.hyze.core.shared.user.preferences.storage.specs.InsertOrUpdatePreferenceByUserIdSpec;

public class PreferenceRepository extends MysqlRepository {

    public PreferenceRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public Boolean insertUserPreference(String preferenceId, PreferenceStatus status, User user) {
        return query(new InsertOrUpdatePreferenceByUserIdSpec(preferenceId, status, user.getId()));
    }


    public UserPreference fetchUserPreference(User user) {
        return query(new FindPreferenceByUserIdSpec(user.getId()));
    }

    public Boolean updateUserPreference(String preferenceId, PreferenceStatus status, User user) {
        return query(new InsertOrUpdatePreferenceByUserIdSpec(preferenceId, status, user.getId()));
    }
}
