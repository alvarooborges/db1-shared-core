package net.hyze.core.shared.storage.repositories.specs;

import net.hyze.core.shared.contracts.repositories.specs.SqlSpec;
import org.springframework.jdbc.core.ResultSetExtractor;

public abstract class SelectSqlSpec<T> implements SqlSpec<T> {

    abstract public ResultSetExtractor<T> getResultSetExtractor();
}
