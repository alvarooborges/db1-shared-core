package net.hyze.core.shared.misc.report;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.shared.user.User;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Report implements Comparable<Report> {

    @Getter
    protected int reported;

    @Getter
    protected Set<ReportItem> reports = Sets.newTreeSet();

    @Getter
    protected Set<Integer> staffers = Sets.newHashSet();

    public Report(int reported) {
        this.reported = reported;
    }

    public void addReport(ReportItem reportItem) {
        reports.add(reportItem);
    }

    public void addStaffer(User user) {
        staffers.add(user.getId());
    }

    public Map<ReportCategory, Integer> getReportsPerCategory(String serverId) {
        TreeMap<ReportCategory, Integer> result = Maps.newTreeMap();

        reports.stream()
                .filter(ri -> serverId == null || serverId.equalsIgnoreCase(ri.getServerId()))
                .forEach(ri -> result.put(ri.getCategory(), result.getOrDefault(ri.getCategory(), 0) + 1));

        return result;
    }

    public Collection<ReportItem> getReports(String serverId) {
        return reports.stream().filter(ri -> serverId == null || serverId.equalsIgnoreCase(ri.getServerId())).collect(Collectors.toCollection(TreeSet::new));
    }

    public boolean removeExpired() {
        return reports.removeIf(it -> it.hasExpired());
    }

    public int compare(Report o, String serverId) {
        return getReports(serverId).size() == o.getReports(serverId).size() ? Integer.compare(reported, o.getReported())
                : Integer.compare(getReports(serverId).size(), o.getReports(serverId).size()) * -1;
    }

    @Override
    public int compareTo(Report o) {
        return reports.size() == o.getReports().size() ? Integer.compare(reported, o.getReported()) : Integer.compare(reports.size(), o.getReports().size()) * -1;
    }

    /*
     * 
     */
    public static Comparator<Report> getComparator(String serverId) {
        return new Comparator<Report>() {

            @Override
            public int compare(Report o1, Report o2) {
                return o1.compare(o2, serverId);
            }
        };
    }
}
