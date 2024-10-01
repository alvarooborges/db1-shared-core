package net.hyze.core.shared.providers;

import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.contracts.Provider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocalCacheProvider<T extends LocalCache> implements Provider<T> {

    private final T cache;

    @Override
    public void prepare() {
        cache.populate();
    }

    @Override
    public void shut() {
    }

    @Override
    public T provide() {
        return cache;
    }
}
