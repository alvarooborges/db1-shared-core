package net.hyze.core.shared.storage.repositories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.contracts.repositories.Repository;
import net.hyze.core.shared.contracts.repositories.specs.MongoSpec;
import net.hyze.core.shared.providers.MongoDatabaseProvider;

@RequiredArgsConstructor
public class MongoRepository implements Repository {
    @Getter
    private final MongoDatabaseProvider databaseProvider;

    public <E> E query(MongoSpec<E> spec) {
        return spec.query(this.databaseProvider.provide().getConnection());
    }
}
