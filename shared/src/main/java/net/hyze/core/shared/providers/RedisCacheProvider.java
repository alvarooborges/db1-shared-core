package net.hyze.core.shared.providers;

import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.contracts.Provider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedisCacheProvider<T extends RedisCache> implements Provider<T> {

    private final T cache;

    @Override
    public void prepare() {
    }

    @Override
    public void shut() {
    }

    @Override
    public T provide() {
        return cache;
    }
}
