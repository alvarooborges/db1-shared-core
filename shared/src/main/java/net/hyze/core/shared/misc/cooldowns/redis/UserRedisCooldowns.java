package net.hyze.core.shared.misc.cooldowns.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRedisCooldowns {

    public static void start(User user, String key, long value, TimeUnit unit) {
        CoreProvider.Cache.Local.USER_REDIS_COOLDOWNS
                .provide()
                .start(user, key, System.currentTimeMillis() + unit.toMillis(value));
    }

    public static void end(User user, String key) {
        CoreProvider.Cache.Local.USER_REDIS_COOLDOWNS
                .provide()
                .end(user, key);
    }

    public static boolean hasEnded(User user, String key) {
        Long expires = CoreProvider.Cache.Local.USER_REDIS_COOLDOWNS
                .provide()
                .get(user, key);

        if (expires == null) {
            return true;
        }

        if (expires <= System.currentTimeMillis()) {
            CoreProvider.Cache.Local.USER_REDIS_COOLDOWNS
                    .provide()
                    .end(user, key);
            return true;
        }

        return false;
    }

    public static long getMillisLeft(User user, String key) {
        if (hasEnded(user, key)) {
            return 0;
        }

        Long expires = CoreProvider.Cache.Local.USER_REDIS_COOLDOWNS
                .provide()
                .get(user, key);

        return expires - System.currentTimeMillis();
    }

    public static int getSecondsLeft(User user, String key) {
        return hasEnded(user, key) ? 0 : ((int) TimeUnit.MILLISECONDS.toSeconds(getMillisLeft(user, key))) + 1;
    }
}