package net.hyze.core.shared.contracts.repositories.specs;

import com.mongodb.client.MongoDatabase;


public abstract class MongoSpec<T> implements Spec<T> {

    public abstract T query(MongoDatabase database);
}
