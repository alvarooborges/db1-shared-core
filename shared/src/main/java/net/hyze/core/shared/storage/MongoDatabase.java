package net.hyze.core.shared.storage;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.contracts.storages.Database;

import java.util.Optional;

@RequiredArgsConstructor
public class MongoDatabase implements Database<com.mongodb.client.MongoDatabase> {

    @Getter
    private final String host;

    @Getter
    private final int port;

    @Getter
    private final String database;
    private final String user;
    private final String password;
    private final String authDatabase;

    @Getter
    private MongoClient client;

    private com.mongodb.client.MongoDatabase mongoDatabase;

    @Override
    public com.mongodb.client.MongoDatabase getConnection() {
        return this.mongoDatabase;
    }

    @Override
    public void openConnection() {
        MongoCredential credential = MongoCredential.createCredential(
                this.user,
                Optional.ofNullable(this.authDatabase).orElse(this.database),
                this.password.toCharArray()
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(String.format("mongodb://%s:%s", this.host, this.port)))
                .credential(credential)
                .build();

        this.client = MongoClients.create(settings);

        this.mongoDatabase = this.client.getDatabase(this.database);
    }

    @Override
    public void closeConnection() {
        if (this.client != null) {
            this.client.close();
        }
    }
}
