package net.hyze.core.shared.providers;

import net.hyze.core.shared.contracts.Provider;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.shared.storage.MongoDatabase;

public class MongoDatabaseProvider implements Provider<MongoDatabase> {

    private final MongoDatabase database;

    public MongoDatabaseProvider(String host, int port, String database, String user, String password, String authDatabase) {
        this.database = new MongoDatabase(host, port, database, user, password, authDatabase);
    }

    @Override
    public void prepare() {
        Printer.INFO.print(String.format(
                "Abrindo conexão mongo %s...",
                database.getDatabase()
        ));
        
        database.openConnection();
        
        Printer.INFO.print(String.format(
                "Conexão mongo %s OK.",
                database.getDatabase()
        ));
    }

    @Override
    public MongoDatabase provide() {
        return database;
    }

    @Override
    public void shut() {
        database.closeConnection();
    }

}
