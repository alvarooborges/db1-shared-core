package net.hyze.core.shared.user.cache.local;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.local.CredentialLocalCache;
import net.hyze.core.shared.user.User;
import lombok.NonNull;
import net.hyze.core.shared.servers.Server;

public class UserLocalCache extends CredentialLocalCache<User> {

    private final LoadingCache<Server, Set<User>> CACHE_BY_SERVER = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build((Server server) -> {
                return CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchUsersByServer(server);
            });

    public Set<User> getOnlineUsersByServer(Server server) {
        return CACHE_BY_SERVER.get(server);
    }

    @Override
    public CacheLoader<String, User> getLoaderByNick() {
        return (@NonNull String nick) -> {
            User user = CoreProvider.Repositories.USERS.provide().fetchByNick(nick);

            return user;
        };
    }

    @Override
    public CacheLoader<Integer, User> getLoaderById() {
        return (@NonNull Integer id) -> {
            User user = CoreProvider.Repositories.USERS.provide().fetchById(id);

            return user;
        };
    }
}
