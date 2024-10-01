package net.hyze.core.shared.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UserGroupLocalCache implements LocalCache {

    private final LoadingCache<User, EnumSet<Group>> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(user -> CoreProvider.Repositories.GROUPS.provide()
                    .fetchByUser(user));

    /**
     * Função optimizada pra buscar todos os grupos de uma lista de app.
     * <p>
     * Caso não exista cache de grupos para um ou mais app solicitado, a função
     * irá buscar de uma só vez no banco de dados.
     */
    public EnumSet<Group> get(@NonNull User user) {
        return CACHE.get(user);
    }

    public void remove(@NonNull User user) {
        CACHE.invalidate(user);
    }
}
