package net.hyze.core.shared.contracts.storages;

public interface Database<T> {

    T getConnection();

    void openConnection();

    void closeConnection();
}
