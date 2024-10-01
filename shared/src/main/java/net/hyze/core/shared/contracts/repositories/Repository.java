package net.hyze.core.shared.contracts.repositories;

import net.hyze.core.shared.contracts.Provider;

public interface Repository {

    Provider<?> getDatabaseProvider();
}
