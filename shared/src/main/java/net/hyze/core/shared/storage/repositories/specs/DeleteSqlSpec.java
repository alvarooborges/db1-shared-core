package net.hyze.core.shared.storage.repositories.specs;

import net.hyze.core.shared.contracts.repositories.specs.SqlSpec;

public abstract class DeleteSqlSpec<T> implements SqlSpec<T> {

    abstract public T parser(int affectedRows);
}
