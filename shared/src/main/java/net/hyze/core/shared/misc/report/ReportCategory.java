package net.hyze.core.shared.misc.report;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.hyze.core.shared.misc.jackson.CodecReportCategory;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@JsonSerialize(using = CodecReportCategory.Serializer.class)
@JsonDeserialize(using = CodecReportCategory.Deserializer.class)
public class ReportCategory implements Comparable<ReportCategory> {

    @Getter
    private final String name;

    @Getter
    private final List<String> description;

    @Getter
    private final Set<String> aliases;

    @Override
    public int compareTo(ReportCategory o) {
        return this.name.compareTo(o.name);
    }

}
