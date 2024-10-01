package net.hyze.core.shared.misc.report.storage;

import net.hyze.core.shared.misc.report.ReportCategory;
import net.hyze.core.shared.misc.report.storage.specs.SelectReportCategoriesSpec;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import java.util.List;

public class ReportRepository extends MysqlRepository {

    public ReportRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public List<ReportCategory> fetchCategories() {
        return query(new SelectReportCategoriesSpec());
    }
}
