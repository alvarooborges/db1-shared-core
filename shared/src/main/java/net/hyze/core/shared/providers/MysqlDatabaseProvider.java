package net.hyze.core.shared.providers;

import net.hyze.core.shared.contracts.Provider;
import net.hyze.core.shared.storage.MysqlDatabase;
import java.net.InetSocketAddress;
import net.hyze.core.shared.misc.utils.Printer;

public class MysqlDatabaseProvider implements Provider<MysqlDatabase> {

    private final MysqlDatabase database;

    public MysqlDatabaseProvider(InetSocketAddress address, String user, String password, String database) {
        this.database = new MysqlDatabase(address, user, password, database);
    }

    @Override
    public void prepare() {
        Printer.INFO.print(String.format(
                "Abrindo conexão mysql %s...",
                database.getDatabase()
        ));
        
        database.openConnection();
        
        Printer.INFO.print(String.format(
                "Conexão mysql %s OK.",
                database.getDatabase()
        ));
    }

    @Override
    public MysqlDatabase provide() {
        return database;
    }

    @Override
    public void shut() {
        database.closeConnection();
    }

}
