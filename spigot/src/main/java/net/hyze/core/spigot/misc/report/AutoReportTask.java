package net.hyze.core.spigot.misc.report;

import com.google.common.collect.Maps;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import org.bukkit.Bukkit;

public class AutoReportTask implements Runnable {

    public final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int i = 0;
    @Getter
    private final ConcurrentMap<String, Integer> CHECK = Maps.newConcurrentMap();

    @Override
    public void run() {

        Bukkit.getOnlinePlayers().stream().filter(Objects::nonNull).forEach(player -> {
            int get = AutoReport.get(player);

            if (get >= AutoReport.getValue()) {
                CHECK.put(player.getName(), CHECK.getOrDefault(player.getName(), 0) + 1);
            }

            if (CHECK.getOrDefault(player.getName(), 0) >= 3) {
                executor.execute(() -> AutoReport.report(player, get));
                CHECK.remove(player.getName());
            }

            AutoReport.reset(player);
        });

        i++;

        if (i == 5) {
            CHECK.clear();
            i = 0;
        }
    }

}
