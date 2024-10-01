package net.hyze.core.shared.misc.report;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

public class ReportManager {

    public static final long EXPIRE_TIME = TimeUnit.DAYS.toMillis(2);
    public static final int MAX_STAFFERS = 3;

    @Getter
    private static final Map<String, ReportCategory> categories = Maps.newTreeMap();
    private static final Map<String, ReportCategory> aliases = Maps.newHashMap();

    public static void registerCategory(ReportCategory category) {
        categories.put(category.getName().toLowerCase(), category);
        aliases.put(category.getName().toLowerCase(), category);

        if (category.getAliases() != null) {
            for (String alias : category.getAliases()) {
                aliases.put(alias.toLowerCase(), category);
            }
        }
    }

    public static ReportCategory getCategory(String id) {
        return aliases.get(id.toLowerCase());
    }

}
