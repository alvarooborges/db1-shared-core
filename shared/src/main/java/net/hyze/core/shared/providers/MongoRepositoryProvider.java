package net.hyze.core.shared.providers;

import com.google.common.collect.Lists;
import net.hyze.core.shared.contracts.Provider;
import net.hyze.core.shared.storage.repositories.MongoRepository;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoRepositoryProvider<T extends MongoRepository> implements Provider<T> {

    private final Supplier<MongoDatabaseProvider> databaseProviderSupplier;
    private final Class<? extends T> repositoryClass;

    private T repository;

    private Object[] args;

    public MongoRepositoryProvider(Supplier<MongoDatabaseProvider> databaseProviderSupplier, Class<? extends T> repositoryClass) {
        this.databaseProviderSupplier = databaseProviderSupplier;
        this.repositoryClass = repositoryClass;
        this.args = new Object[0];
    }

    @Override
    public T provide() {
        return repository;
    }

    @Override
    public void shut() {
    }

    @Override
    public void prepare() {
        try {
            List<Object> list = Lists.newLinkedList();
            list.add(databaseProviderSupplier.get());

            if (args.length > 0) {
                Collections.addAll(list, args);
            }

            repository = repositoryClass.getConstructor(list.stream().map(Object::getClass).toArray(Class[]::new)).newInstance(list.stream().toArray(Object[]::new));
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(MysqlRepositoryProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
