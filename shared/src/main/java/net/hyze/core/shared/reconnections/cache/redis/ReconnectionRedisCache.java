package net.hyze.core.shared.reconnections.cache.redis;

import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.reconnections.Reconnection;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Collection;
import java.util.Map;

public class ReconnectionRedisCache implements RedisCache {

    private static final String REDIRECTION_KEY = "reconnections:%s";

    public Reconnection fetchReconnection(Server server, User user) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String target = jedis.hget(String.format(REDIRECTION_KEY, server.getId()), String.valueOf(user.getId()));
            AppStatus targetApp;

            if(target != null && (targetApp = CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(target, AppStatus.class)) != null) {
                return new Reconnection(server, user, targetApp);
            }
        }

        return null;
    }

    public void updateReconnection(Server server, Collection<User> users, AppStatus targetApp) {
        Map<String, String> map = Maps.newHashMap();

        for(User user : users) {
            map.put(String.valueOf(user.getId()), targetApp.getAppId());
        }

        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.hmset(String.format(REDIRECTION_KEY, server.getId()), map);
        }
    }

    public void updateReconnection(Server server, User user, AppStatus targetApp) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.hset(String.format(REDIRECTION_KEY, server.getId()), String.valueOf(user.getId()), targetApp.getAppId());
        }
    }

    public void clearReconnection(Server server, Collection<User> users) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.hdel(String.format(REDIRECTION_KEY, server.getId()), users.stream().map(User::getId).map(String::valueOf).toArray(String[]::new));
        }
    }

    public void clearReconnection(Server server, User user) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.hdel(String.format(REDIRECTION_KEY, server.getId()), String.valueOf(user.getId()));
        }
    }

}
