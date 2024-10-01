package net.hyze.core.shared.misc.mojang.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.misc.mojang.MojangAPI;
import net.hyze.core.shared.misc.mojang.exceptions.SkinNotFoundException;
import net.hyze.core.shared.misc.mojang.exceptions.TooManyRequestsException;
import net.hyze.core.shared.misc.mojang.exceptions.UUIDNotFoundException;
import net.hyze.core.shared.skins.Skin;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MojangLocalCache implements LocalCache {

    private final LoadingCache<String, UUID> uuids = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build((String nick) -> {
                try {
                    return MojangAPI.getUUID(nick);
                } catch (IOException | TooManyRequestsException | UUIDNotFoundException ex) {
                    return null;
                }
            });

    private final LoadingCache<UUID, Skin> skins = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build((UUID uuid) -> {
                try {
                    return MojangAPI.getSkin(uuid);
                } catch (IOException | TooManyRequestsException | SkinNotFoundException ex) {
                    return null;
                }
            });

    public UUID getUUID(String nick, boolean populate) throws ExecutionException {
        return populate ? this.uuids.get(nick) : this.uuids.getIfPresent(nick);
    }

    public Skin getSkin(UUID uuid, boolean populate) throws ExecutionException {
        return populate ? this.skins.get(uuid) : this.skins.getIfPresent(uuid);
    }

}
