package net.hyze.core.shared.misc.cooldowns;

import com.google.common.collect.Maps;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.hyze.core.shared.misc.utils.NumberUtils;

public class Cooldowns {

    public static final Map<String, Long> COOLDOWNS = Maps.newConcurrentMap();

    public static void start(String key, long value, TimeUnit unit) {
        COOLDOWNS.put(key, System.currentTimeMillis() + unit.toMillis(value));
    }

    public static boolean end(String key) {
        return COOLDOWNS.remove(key) != null;
    }

    public static boolean hasEnded(String key) {

        if (!COOLDOWNS.containsKey(key)) {
            return true;
        }

        if (COOLDOWNS.get(key) <= System.currentTimeMillis()) {
            COOLDOWNS.remove(key);
            return true;
        }

        return false;
    }

    public static long getMillisLeft(String key) {
        return hasEnded(key) ? 0L : COOLDOWNS.get(key) - System.currentTimeMillis();
    }

    public static int getSecondsLeft(String key) {
        return hasEnded(key) ? 0 : ((int) TimeUnit.MILLISECONDS.toSeconds(getMillisLeft(key))) + 1;
    }

    public static String getFormattedTimeLeft(String key) {
        return getFormattedTimeLeft(getMillisLeft(key));
    }

    public static String getFormattedTimeLeft(long millis) {
        if (millis < 0) {
            return "";
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);

        if (days > 0) {
            sb.append(days).append("d ");
        }

        if (hours > 0) {
            sb.append(hours).append("h ");
        }

        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }

        if (days == 0 && hours == 0 && minutes == 0 && seconds == 1 && millis < 1000) {

            double halfsec = millis / 1000D;

            NumberFormat nf = new DecimalFormat("#.##");
            String value = nf.format(NumberUtils.roundDouble(halfsec, 2));

            sb.append(value).append("s");

        } else if (seconds > 0) {

            sb.append(seconds).append("s");

        }

        return (sb.toString().trim());
    }
}
