package net.hyze.core.shared.cache.local;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.NonNull;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.UserPreference;

public class UserPreferenceLocalCache implements LocalCache {

    private final Map<User, UserPreference> cache = Maps.newConcurrentMap();

    public UserPreference get(@NonNull User user) {
        UserPreference userPreference = this.cache.get(user);

        if (userPreference == null) {
            userPreference = refresh(user);
        }

        return userPreference;
    }

    public void remove(@NonNull User user) {
        this.cache.remove(user);
    }

    public UserPreference refresh(@NonNull User user) {
        UserPreference preferences = CoreProvider.Repositories.USERS_PREFERENCES.provide().fetchUserPreference(user);

        this.cache.remove(user);
        this.cache.put(user, preferences);

        return preferences;
    }

}
