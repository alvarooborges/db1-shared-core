package net.hyze.core.shared.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Map;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.hyze.core.shared.user.Credentialable;
import java.util.Collection;

/**
 * Cache local para Usuários
 *
 * Caso o usuário requisitado não exista, uma pesquisa será relizada no banco
 * dados para busca-lo, caso ele não seja encontrado, o retorno da função #get()
 * será null. Este cache não possui tempo de expiração. A informação só será
 * atualizada caso a funcão #put() seja chamado com o novo dado
 *
 * @param <T>
 */
@NoArgsConstructor
public abstract class CredentialLocalCache<T extends Credentialable> implements LocalCache {

    protected final LoadingCache<String, T> CACHE_BY_NICK = Caffeine.newBuilder()
            .build(getLoaderByNick()::load);

    protected final LoadingCache<Integer, T> CACHE_BY_ID = Caffeine.newBuilder()
            .build(getLoaderById()::load);

    public T get(Integer id) {
        T obj = CACHE_BY_ID.get(id);

        if (obj != null) {
            CACHE_BY_NICK.put(obj.getNick(), obj);
        }

        return obj;
    }

    public T get(@NonNull String nick) {
        T obj = CACHE_BY_NICK.get(nick);

        if (obj != null) {
            CACHE_BY_ID.put(obj.getId(), obj);
        }

        return obj;
    }

    public T getIfPresent(Integer id) {
        return CACHE_BY_ID.getIfPresent(id);
    }

    public T getIfPresent(String nick) {
        return CACHE_BY_NICK.getIfPresent(nick);
    }

    public Map<Integer, T> getAllPresentByIds(Set<Integer> ids) {
        return CACHE_BY_ID.getAllPresent(ids);
    }

    public Map<String, T> getAllPresentByNicks(Set<String> nicks) {
        return CACHE_BY_NICK.getAllPresent(nicks);
    }

    public Collection<T> getAllPresent() {
        return CACHE_BY_NICK.asMap().values();
    }

    public Map<Integer, T> getAllByIds(Collection<Integer> ids) {
        Map<Integer, T> objs = CACHE_BY_ID.getAll(ids);

        objs.values().forEach(obj -> {
            CACHE_BY_NICK.put(obj.getNick(), obj);
        });

        return objs;
    }

    public Map<String, T> getAllByNicks(Collection<String> nicks) {
        Map<String, T> objs = CACHE_BY_NICK.getAll(nicks);

        objs.values().forEach(obj -> {
            CACHE_BY_ID.put(obj.getId(), obj);
        });

        return objs;
    }

    public void put(@NonNull T user) {
        CACHE_BY_NICK.put(user.getNick(), user);
        CACHE_BY_ID.put(user.getId(), user);
    }

    public void remove(int id) {
        T obj = getIfPresent(id);

        if (obj != null) {
            CACHE_BY_NICK.invalidate(obj.getNick());
        }

        CACHE_BY_ID.invalidate(id);
    }

    public void remove(@NonNull String nick) {
        T obj = getIfPresent(nick);

        if (obj != null) {
            CACHE_BY_ID.invalidate(obj.getId());
        }

        CACHE_BY_NICK.invalidate(nick);
    }

    public abstract CacheLoader<String, T> getLoaderByNick();

    public abstract CacheLoader<Integer, T> getLoaderById();
}
