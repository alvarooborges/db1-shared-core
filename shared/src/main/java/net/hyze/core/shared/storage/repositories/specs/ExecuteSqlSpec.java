package net.hyze.core.shared.storage.repositories.specs;

import net.hyze.core.shared.contracts.repositories.specs.SqlSpec;
import org.springframework.jdbc.core.PreparedStatementCallback;

public abstract class ExecuteSqlSpec<T> implements SqlSpec<T> {

    public abstract PreparedStatementCallback<T> getPreparedStatementCallback();
}
