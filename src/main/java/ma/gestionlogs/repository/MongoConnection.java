package ma.gestionlogs.repository;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Gère la connexion à MongoDB. Un seul MongoClient est créé : il est thread-safe
 * et peut donc être partagé par tous les threads du récepteur.
 */
public class MongoConnection implements AutoCloseable {

    private static final String DEFAULT_URI =
            "mongodb://localhost:27017/?serverSelectionTimeoutMS=5000";
    private static final String DEFAULT_DATABASE = "gestion_logs";
    private static final String LOGS_COLLECTION = "logs";

    private final MongoClient client;
    private final MongoDatabase database;

    public MongoConnection() {
        this(DEFAULT_URI, DEFAULT_DATABASE);
    }

    public MongoConnection(String uri, String databaseName) {
        this.client = MongoClients.create(uri);
        this.database = client.getDatabase(databaseName);
    }

    public MongoCollection<Document> getLogsCollection() {
        return database.getCollection(LOGS_COLLECTION);
    }

    /** Retourne true si le serveur MongoDB répond. */
    public boolean ping() {
        try {
            database.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            System.err.println("Ping MongoDB impossible : " + e.getMessage());
            return false;
        }
    }

    @Override
    public void close() {
        client.close();
    }
}