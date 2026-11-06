package ma.gestionlogs;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import ma.gestionlogs.model.LogEvent;
import ma.gestionlogs.repository.MongoConnection;
import ma.gestionlogs.repository.MongoLogRepository;

public class MongoLogRepositoryTest {

    private static int failures = 0;

    public static void main(String[] args) {
        try (MongoConnection connection = new MongoConnection(
                "mongodb://localhost:27017/?serverSelectionTimeoutMS=5000",
                "gestion_logs_test")) {

            connection.getLogsCollection().drop();
            MongoLogRepository repo = new MongoLogRepository(connection.getLogsCollection());

            Instant now = Instant.now();
            repo.save(event("t1", "rhel-a", "ERROR", "disk failure", now.minus(2, ChronoUnit.HOURS)));
            repo.save(event("t2", "rhel-a", "INFO", "service started", now.minus(1, ChronoUnit.HOURS)));
            repo.save(event("t3", "rhel-b", "ERROR", "auth failure", now));

            check("findAll = 3", repo.findAll().size() == 3);
            check("findAll trié du plus récent", "t3".equals(repo.findAll().get(0).getId()));
            check("findByHostname(rhel-a) = 2", repo.findByHostname("rhel-a").size() == 2);
            check("findBySeverity(ERROR) = 2", repo.findBySeverity("ERROR").size() == 2);
            check("findByPeriod (derniere heure et demie) = 2",
                    repo.findByPeriod(now.minus(90, ChronoUnit.MINUTES), now.plusSeconds(1)).size() == 2);

            List<LogEvent> back = repo.findByHostname("rhel-b");
            LogEvent e = back.get(0);
            check("relecture des champs", "auth failure".equals(e.getMessage())
                    && "ERROR".equals(e.getSeverity()) && "10.0.0.1".equals(e.getSourceAddress())
                    && e.getReceivedTimestamp() != null);
        }
        System.out.println(failures == 0 ? "\nTOUS LES TESTS OK" : "\n" + failures + " TEST(S) EN ECHEC");
    }

    private static LogEvent event(String id, String host, String severity, String msg, Instant received) {
        LogEvent e = new LogEvent(host, "test", severity, msg);
        e.setId(id);
        e.setReceivedTimestamp(received);
        e.setSourceAddress("10.0.0.1");
        return e;
    }

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "OK      " : "ECHEC   ") + name);
        if (!ok) failures++;
    }
}