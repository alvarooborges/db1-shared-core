package net.hyze.core.shared.dungeon.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.dungeon.DungeonUserData;
import net.hyze.core.shared.dungeon.storage.DungeonUserDataRepository;
import net.hyze.core.shared.user.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class DungeonUserDataCache implements LocalCache {

    private final Supplier<DungeonUserDataRepository> repositorySupplier;

    private final LoadingCache<User, DungeonUserData> CACHE;

    public DungeonUserDataCache(Supplier<DungeonUserDataRepository> repositorySupplier) {
        this.repositorySupplier = repositorySupplier;

        this.CACHE = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .build(user -> {
                    DungeonUserDataRepository repository = repositorySupplier.get();
                    DungeonUserData data = repository.fetchData(user);
                    data.setMapAccesses(repository.fetchAccesses(user));
                    return data;
                });
    }

    public DungeonUserData get(@NonNull User user) {
        return this.CACHE.get(user);
    }

    public void remove(@NonNull User user) {
        this.CACHE.invalidate(user);
    }

    public DungeonUserData refresh(@NonNull User user) {
        this.CACHE.refresh(user);

        return this.CACHE.get(user);
    }


}
