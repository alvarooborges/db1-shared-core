package net.hyze.core.shared.storage.repositories.specs;

import net.hyze.core.shared.contracts.repositories.specs.SqlSpec;
import org.springframework.jdbc.support.KeyHolder;

public abstract class InsertSqlSpec<T> implements SqlSpec<T> {

    abstract public T parser(int affectedRows, KeyHolder keyHolder);

}
