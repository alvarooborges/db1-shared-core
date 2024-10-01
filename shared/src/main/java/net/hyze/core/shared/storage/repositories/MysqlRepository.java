package net.hyze.core.shared.storage.repositories;

import net.hyze.core.shared.contracts.repositories.Repository;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.contracts.repositories.specs.Spec;
import net.hyze.core.shared.contracts.repositories.specs.SqlSpec;
import net.hyze.core.shared.storage.repositories.specs.DeleteSqlSpec;
import net.hyze.core.shared.storage.repositories.specs.ExecuteSqlSpec;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import org.springframework.jdbc.core.JdbcTemplate;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@Getter
@RequiredArgsConstructor
public abstract class MysqlRepository implements Repository {

    @Getter
    private final MysqlDatabaseProvider databaseProvider;

    public final <E, S extends Spec<E>> E query(S specification) {
        return this.query((SqlSpec<E>) specification);
    }

    protected <E> E query(SqlSpec<E> specification) {
        JdbcTemplate template = new JdbcTemplate(getDatabaseProvider().provide().getDataSource());

        if (specification instanceof ExecuteSqlSpec) {
            ExecuteSqlSpec<E> executer = (ExecuteSqlSpec) specification;

            return template.execute(executer.getPreparedStatementCreator(), executer.getPreparedStatementCallback());
        } else if (specification instanceof SelectSqlSpec) {
            SelectSqlSpec<E> selector = (SelectSqlSpec) specification;

            return template.query(specification.getPreparedStatementCreator(), selector.getResultSetExtractor());
        } else if (specification instanceof UpdateSqlSpec) {
            UpdateSqlSpec<E> updater = (UpdateSqlSpec) specification;

            int rowsAffected = template.update(updater.getPreparedStatementCreator());

            return updater.parser(rowsAffected);
        } else if (specification instanceof DeleteSqlSpec) {
            DeleteSqlSpec<E> deleter = (DeleteSqlSpec) specification;
            int rowsAffected = template.update(deleter.getPreparedStatementCreator());

            return deleter.parser(rowsAffected);
        } else if (specification instanceof InsertSqlSpec) {
            InsertSqlSpec<E> inserter = (InsertSqlSpec) specification;

            KeyHolder holder = new GeneratedKeyHolder();

            int rowsAffected = template.update(inserter.getPreparedStatementCreator(), holder);

            return inserter.parser(rowsAffected, holder);
        }

        return null;
    }
}
