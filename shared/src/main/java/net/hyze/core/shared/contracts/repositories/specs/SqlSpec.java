package net.hyze.core.shared.contracts.repositories.specs;

import org.springframework.jdbc.core.PreparedStatementCreator;

public interface SqlSpec<T> extends Spec<T> {

    PreparedStatementCreator getPreparedStatementCreator();
}
