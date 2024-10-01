package net.hyze.core.shared.misc.report.storage.specs;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.hyze.core.shared.misc.report.ReportCategory;
import net.hyze.core.shared.misc.utils.Patterns;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

public class SelectReportCategoriesSpec extends SelectSqlSpec<List<ReportCategory>> {

    @Override
    public ResultSetExtractor<List<ReportCategory>> getResultSetExtractor() {
        return result -> {

            List<ReportCategory> out = Lists.newArrayList();;

            while (result.next()) {
                String descriptionJson = result.getString("description");
                String aliasesJson = result.getString("aliases");

                out.add(new ReportCategory(result.getString("name"), descriptionJson == null ? null : Lists.newArrayList(Patterns.SEMI_COLON.split(descriptionJson)), aliasesJson == null ? null : Sets.newHashSet(Patterns.SEMI_COLON.split(aliasesJson))));
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = String.format("SELECT * FROM `report_categories`;");

            PreparedStatement statement = connection.prepareStatement(query);

            return statement;
        };
    }

}
