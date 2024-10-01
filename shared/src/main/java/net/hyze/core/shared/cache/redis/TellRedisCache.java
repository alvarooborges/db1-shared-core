package net.hyze.core.shared.cache.redis;

import com.google.common.primitives.Ints;
import net.hyze.core.shared.CoreProvider;
import java.util.function.Function;
import lombok.NonNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class TellRedisCache implements RedisCache {

    public static final Function<Integer, String> KEY_CONVERTER_BY_ID = id -> "tell:" + id;

    public Integer getTarget(@NonNull Integer userId) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String out = jedis.get(KEY_CONVERTER_BY_ID.apply(userId));

            return out != null ? Ints.tryParse(out) : null;
        }
    }

    public void setTarget(@NonNull Integer userId, @NonNull Integer userTargetId) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.set(KEY_CONVERTER_BY_ID.apply(userId), userTargetId.toString());
            pipeline.expire(KEY_CONVERTER_BY_ID.apply(userId), 60 * 2);
            pipeline.sync();
        }
    }

}
