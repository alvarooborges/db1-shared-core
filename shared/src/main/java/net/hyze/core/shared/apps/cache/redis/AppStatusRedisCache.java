package net.hyze.core.shared.apps.cache.redis;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.apps.ServerStatus;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.redis.RedisScriptManager;
import net.hyze.core.shared.servers.Server;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AppStatusRedisCache implements RedisCache {

    public static final int TTL_SECONDS = 10;

    public static final Function<String, String> KEY_CONVERTER_BY_ID = id -> "apps:" + id.toLowerCase();

    private Cache<String, AppStatus> cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

    public void delete(App app) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.del(KEY_CONVERTER_BY_ID.apply(app.getId()));
        }
    }

    public void update(App app) {

        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            AppStatus status = app.getStatus();
            if (status != null) {
                String jackson = CoreConstants.JACKSON.writeValueAsString(status);

                jedis.set(KEY_CONVERTER_BY_ID.apply(app.getId()), jackson);
                jedis.expire(KEY_CONVERTER_BY_ID.apply(app.getId()), 5);
            }
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, "update application status erro", ex);
        }
    }

    public <T extends AppStatus> T fetch(App app, Class<T> statusClass) {
        return fetch(app.getId(), statusClass);
    }

    public <T extends AppStatus> T fetch(String appId, Class<T> statusClass) {
        String key = KEY_CONVERTER_BY_ID.apply(appId);
        T status = (T) cache.getIfPresent(key);

        if (status != null) {
            return status;
        }

        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String value = jedis.get(key);

            if (value != null) {
                status = CoreConstants.JACKSON.readValue(value, statusClass);

                cache.put(key, status);

                return status;
            }
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "fetch application status error", ex);
        }

        return null;
    }

    public Map<String, AppStatus> fetch(Collection<String> ids) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            Map<String, Response<String>> responses = Maps.newHashMap();

            ids.forEach(id -> {
                Response<String> response = getApp(id, pipeline);

                responses.put(id, response);
            });

            pipeline.sync();

            Map<String, AppStatus> apps = Maps.newHashMap();

            responses.forEach((appId, response) -> {
                String value = response.get();

                try {
                    if (value != null) {
                        apps.put(appId, CoreConstants.JACKSON.readValue(response.get(), AppStatus.class));
                        return;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                apps.put(appId, null);
            });

            return apps;
        }
    }

    public <T extends AppStatus> Map<String, T> fetch(Collection<String> ids, Class<T> statusClass) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            Map<String, Response<String>> responses = Maps.newHashMap();

            ids.forEach(id -> {
                Response<String> response = getApp(id, pipeline);

                responses.put(id, response);
            });

            pipeline.sync();

            Map<String, T> apps = Maps.newHashMap();

            responses.forEach((appId, response) -> {
                String value = response.get();

                try {
                    if (value != null) {
                        apps.put(appId, CoreConstants.JACKSON.readValue(response.get(), statusClass));
                        return;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                apps.put(appId, null);
            });

            return apps;
        }
    }

    public Response<String> getApp(String appId, Pipeline pipeline) {
        if (appId.startsWith("apps:")) {
            return pipeline.get(appId);
        } else {
            return pipeline.get(KEY_CONVERTER_BY_ID.apply(appId));
        }
    }

    private Set<AppStatus> processOutput(Set<Response<String>> out) {
        return out.stream()
                .map(Response::get)
                .filter(Objects::nonNull)
                .map(value -> {
                    try {
                        return CoreConstants.JACKSON.readValue(value, AppStatus.class);
                    } catch (IOException ex) {
                        Logger.getGlobal().log(Level.SEVERE, "fetch multiple applications status error", ex);
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<ServerStatus> fetchOnlineApps(AppType type) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Object object = RedisScriptManager.execute(
                    jedis,
                    "fetchAppStatusByType.lua",
                    Collections.emptyList(),
                    Lists.newArrayList(type.name())
            );

            return processObjectToServerStatuses(object);
        }
    }

    public Set<ServerStatus> fetchOnlineApps(Server server) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Object object = RedisScriptManager.execute(
                    jedis,
                    "fetchAppStatusByServer.lua",
                    Collections.emptyList(),
                    Lists.newArrayList(server.name())
            );

            return processObjectToServerStatuses(object);
        }
    }

    private Set<ServerStatus> processObjectToServerStatuses(Object object) {
        if (object != null) {
            return ((Collection<String>) object).stream()
                    .map(value -> {
                        try {
                            return CoreConstants.JACKSON.readValue(value, ServerStatus.class);
                        } catch (IOException ex) {
                            Logger.getGlobal().log(Level.SEVERE, "process object to server statuses", ex);
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    public Set<AppStatus> fetchOnlineApps() {

        Set<Response<String>> output = Sets.newHashSet();

        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {

            Set<String> keys = jedis.keys("apps:*");

            Pipeline pipeline = jedis.pipelined();

            keys.forEach(appId -> {
                output.add(pipeline.get(appId));
            });

            pipeline.sync();
        }

        return processOutput(output);
    }
}
