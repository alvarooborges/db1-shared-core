package net.hyze.core.shared.providers;

import com.google.common.collect.Lists;
import net.hyze.core.shared.contracts.Provider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MysqlRepositoryProvider<T extends MysqlRepository> implements Provider<T> {

    private final Supplier<MysqlDatabaseProvider> databaseProciderSupplier;
    private final Class<? extends T> repositoryClass;

    private T repository;

    private Object[] args;

    public MysqlRepositoryProvider(Supplier<MysqlDatabaseProvider> databaseProciderSupplier, Class<? extends T> repositoryClass) {
        this.databaseProciderSupplier = databaseProciderSupplier;
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
            list.add(databaseProciderSupplier.get());

            if(args.length > 0) {
                Collections.addAll(list, args);
            }

            repository = repositoryClass.getConstructor(list.stream().map(Object::getClass).toArray(Class[]::new)).newInstance(list.stream().toArray(Object[]::new));
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(MysqlRepositoryProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
