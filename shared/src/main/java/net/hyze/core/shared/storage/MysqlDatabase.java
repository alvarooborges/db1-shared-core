package net.hyze.core.shared.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.hyze.core.shared.contracts.storages.Database;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MysqlDatabase implements Database<Connection> {

    protected final InetSocketAddress address;
    protected final String user;
    protected final String password;
    
    @Getter
    protected final String database;

    @Getter
    private HikariDataSource dataSource;

    @Override
    public Connection getConnection() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                openConnection();
            }
            
            return dataSource.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void openConnection() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");

        hikariConfig.addDataSourceProperty("serverName", address.getAddress().getHostAddress());
        hikariConfig.addDataSourceProperty("portNumber", address.getPort());
        hikariConfig.addDataSourceProperty("databaseName", database);
        hikariConfig.addDataSourceProperty("user", user);
        hikariConfig.addDataSourceProperty("password", password);

        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setMaximumPoolSize(10);
//        hikariConfig.setIdleTimeout(30);
//        hikariConfig.setMinimumIdle(5);
        hikariConfig.setConnectionTimeout(5000);

        dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
