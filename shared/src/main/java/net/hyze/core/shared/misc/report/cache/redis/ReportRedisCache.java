package net.hyze.core.shared.misc.report.cache.redis;

import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.misc.report.Report;
import net.hyze.core.shared.misc.report.ReportCategory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import redis.clients.jedis.Jedis;

public class ReportRedisCache implements RedisCache {

    private static final String REPORTS_FIELD = "reports"; // reported -> Report
    public static final String REPORT_TELEPORT_FIELD = "reports.teleport";

    public static final long EXPIRE_TIME = TimeUnit.DAYS.toMillis(2);
    public static final int MAX_STAFFERS = 3;

    @Getter
    private static final Map<String, ReportCategory> categories = Maps.newTreeMap();
    private static final Map<String, ReportCategory> aliases = Maps.newHashMap();

    public void registerCategory(ReportCategory category) {
        categories.put(category.getName().toLowerCase(), category);
        aliases.put(category.getName().toLowerCase(), category);

        if (category.getAliases() != null) {
            for (String alias : category.getAliases()) {
                aliases.put(alias.toLowerCase(), category);
            }
        }
    }

    public ReportCategory getCategory(String id) {
        return aliases.get(id.toLowerCase());
    }

    public Report getReport(User user) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String json = jedis.hget(REPORTS_FIELD, String.valueOf(user.getId()));

            if (json == null) {
                return new Report(user.getId());
            }

            Report report = CoreConstants.JACKSON.readValue(json, Report.class);

            if (report.removeExpired() || report.getReports().isEmpty()) {
                updateReport(report);
            }

            return report;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void updateReport(Report report) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {

            if (report.getReports().isEmpty()) {
                jedis.hdel(REPORTS_FIELD, String.valueOf(report.getReported()));
            } else {
                jedis.hset(REPORTS_FIELD, String.valueOf(report.getReported()), CoreConstants.JACKSON.writeValueAsString(report));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void removeReport(int id) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.hdel(REPORTS_FIELD, String.valueOf(id));
        }
    }

}
