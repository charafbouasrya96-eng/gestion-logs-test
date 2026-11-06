package ma.gestionlogs.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;

import ma.gestionlogs.model.LogEvent;

/**
 * Stockage MongoDB des LogEvent.
 * Les Instant sont stockés en Date BSON pour permettre les recherches par période.
 * Les recherches par période portent sur receivedTimestamp, qui existe pour tous
 * les logs (eventTimestamp peut être absent quand le format n'est pas reconnu).
 */
public class MongoLogRepository implements LogRepository {

    private final MongoCollection<Document> collection;

    public MongoLogRepository(MongoCollection<Document> collection) {
        this.collection = collection;
        collection.createIndex(Indexes.descending("receivedTimestamp"));
        collection.createIndex(Indexes.ascending("hostname"));
        collection.createIndex(Indexes.ascending("severity"));
    }

    @Override
    public void save(LogEvent event) {
        try {
            collection.insertOne(toDocument(event));
            System.out.println("💾 [MONGODB] Log saved: " + event.getMessage());
        } catch (MongoException e) {
            // On ne propage pas l'erreur : une panne MongoDB ne doit pas couper la connexion du client.
            System.err.println("❌ [MONGODB] Echec de sauvegarde : " + e.getMessage());
        }
    }

    @Override
    public List<LogEvent> findAll() {
        return find(new Document());
    }

    @Override
    public List<LogEvent> findByHostname(String hostname) {
        return find(Filters.eq("hostname", hostname));
    }

    @Override
    public List<LogEvent> findBySeverity(String severity) {
        return find(Filters.eq("severity", severity));
    }

    @Override
    public List<LogEvent> findByPeriod(Instant from, Instant to) {
        return find(Filters.and(
                Filters.gte("receivedTimestamp", Date.from(from)),
                Filters.lt("receivedTimestamp", Date.from(to))));
    }

    private List<LogEvent> find(Bson filter) {
        List<LogEvent> result = new ArrayList<>();
        for (Document doc : collection.find(filter).sort(Sorts.descending("receivedTimestamp"))) {
            result.add(fromDocument(doc));
        }
        return result;
    }

    private static Document toDocument(LogEvent e) {
        return new Document("_id", e.getId())
                .append("eventTimestamp", toDate(e.getEventTimestamp()))
                .append("receivedTimestamp", toDate(e.getReceivedTimestamp()))
                .append("hostname", e.getHostname())
                .append("service", e.getService())
                .append("severity", e.getSeverity())
                .append("category", e.getCategory())
                .append("message", e.getMessage())
                .append("sourceAddress", e.getSourceAddress())
                .append("username", e.getUsername());
    }

    private static LogEvent fromDocument(Document d) {
        LogEvent e = new LogEvent();
        e.setId(d.getString("_id"));
        e.setEventTimestamp(toInstant(d.getDate("eventTimestamp")));
        e.setReceivedTimestamp(toInstant(d.getDate("receivedTimestamp")));
        e.setHostname(d.getString("hostname"));
        e.setService(d.getString("service"));
        e.setSeverity(d.getString("severity"));
        e.setCategory(d.getString("category"));
        e.setMessage(d.getString("message"));
        e.setSourceAddress(d.getString("sourceAddress"));
        e.setUsername(d.getString("username"));
        return e;
    }

    private static Date toDate(Instant i) {
        return i == null ? null : Date.from(i);
    }

    private static Instant toInstant(Date d) {
        return d == null ? null : d.toInstant();
    }
}