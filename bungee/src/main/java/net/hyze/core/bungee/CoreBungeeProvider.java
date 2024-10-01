package net.hyze.core.bungee;

import net.hyze.core.bungee.user.cache.ProxiedUserLocalCache;
import net.hyze.core.shared.CoreProvider.Database;
import net.hyze.core.shared.providers.LocalCacheProvider;
import net.hyze.core.shared.providers.MysqlRepositoryProvider;
import net.hyze.core.shared.sessions.storage.UserSessionRepository;

public class CoreBungeeProvider {

    public static class Cache {

        public static class Local {

            public static final LocalCacheProvider<ProxiedUserLocalCache> PROXIED_USERS = new LocalCacheProvider(
                    new ProxiedUserLocalCache()
            );

        }

    }

}
