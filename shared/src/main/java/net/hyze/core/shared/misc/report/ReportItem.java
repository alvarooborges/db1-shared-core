package net.hyze.core.shared.misc.report;

import static java.lang.Long.compare;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReportItem implements Comparable<ReportItem> {

    @Getter
    private int reporter;

    @Getter
    private long reportTime;

    @Getter
    private String serverId;

    @Getter
    private ReportCategory category;

    public ReportItem(int reporter, ReportCategory category, String serverId) {
        this.reporter = reporter;
        this.reportTime = System.currentTimeMillis();
        this.serverId = serverId;
        this.category = category;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() > (reportTime + ReportManager.EXPIRE_TIME);
    }

    //
    @Override
    public int compareTo(ReportItem reportItem) {
        return compare(reportTime, reportItem.reportTime);
    }

}
