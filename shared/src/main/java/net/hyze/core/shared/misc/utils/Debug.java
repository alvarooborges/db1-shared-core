package net.hyze.core.shared.misc.utils;

import java.util.Map.Entry;
import org.apache.commons.collections4.map.LinkedMap;

public class Debug {

    private final String name;
    private final LinkedMap<StackTraceElement, Long> anchors = new LinkedMap<>();
    private final long min;

    public Debug(String name) {
        this.name = name;
        this.anchors.put(Thread.currentThread().getStackTrace()[2], System.currentTimeMillis());
        this.min = -1;
    }

    public Debug(String name, long min) {
        this.name = name;
        this.anchors.put(Thread.currentThread().getStackTrace()[2], System.currentTimeMillis());
        this.min = min;
    }

    public void anchor() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];

        this.anchors.put(caller, System.currentTimeMillis());
    }

    public void done() {
        this.anchors.put(Thread.currentThread().getStackTrace()[2], System.currentTimeMillis());

        long total = this.anchors.get(this.anchors.lastKey()) - this.anchors.get(this.anchors.firstKey());

        if (total <= this.min) {
            return;
        }
        
        StringBuilder builder = new StringBuilder();

        builder.append("&e[DEBUG] [")
                .append(Thread.currentThread().getName())
                .append("] [").append(this.name)
                .append("] ").append("[Total: ")
                .append(total).append("ms]")
                .append("\n");

        Entry<StackTraceElement, Long> lastEntry = null;

        for (Entry<StackTraceElement, Long> entry : this.anchors.entrySet()) {

            if (lastEntry != null) {

                long elapsed = entry.getValue() - lastEntry.getValue();

                if (elapsed != 0) {
                    builder.append("    [")
                            .append(lastEntry.getKey())
                            .append(" - ").append(entry.getKey())
                            .append(": ").append(elapsed)
                            .append("ms] ")
                            .append("\n");
                }

            }

            lastEntry = entry;

        }

        Printer.INFO.coloredPrint(builder.toString());
    }
}
