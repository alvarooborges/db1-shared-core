package net.hyze.core.bungee.user.cache;

import com.google.common.collect.Maps;
import net.hyze.core.bungee.user.ProxiedUser;
import net.hyze.core.shared.cache.local.LocalCache;
import java.util.HashMap;
import lombok.NonNull;

public class ProxiedUserLocalCache implements LocalCache {

    private final HashMap<String, ProxiedUser> cache = Maps.newHashMap();

    public ProxiedUser get(@NonNull String nick) {
        return this.cache.get(nick.toLowerCase());
    }

    public void put(@NonNull String nick, @NonNull ProxiedUser proxiedUser) {
        this.cache.put(nick.toLowerCase(), proxiedUser);
    }

    public void delete(@NonNull String nick) {
        this.cache.remove(nick.toLowerCase());
    }

}
