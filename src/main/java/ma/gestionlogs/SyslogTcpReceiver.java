package ma.gestionlogs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import ma.gestionlogs.repository.LogRepository;
import ma.gestionlogs.repository.MongoConnection;
import ma.gestionlogs.repository.MongoLogRepository;
import ma.gestionlogs.model.LogEvent;

import ma.gestionlogs.repository.InMemoryRuleRepository;
import ma.gestionlogs.service.LogIngestionService;
import ma.gestionlogs.service.RuleDetectionService;

public class SyslogTcpReceiver {

    private static final int PORT = 5514;
    private static final LogNormalizer normalizer = new LogNormalizer();
    private static LogIngestionService ingestionService;

    public static void main(String[] args) {
        // Assemblage des composants (étape 4 : on remplacera InMemoryLogRepository par MongoLogRepository)
        MongoConnection mongo = new MongoConnection();
        if (!mongo.ping()) {
            System.err.println("MongoDB inaccessible : verifiez que le service MongoDB est demarre. Arret du recepteur.");
            mongo.close();
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(mongo::close));

        LogRepository logRepo = new MongoLogRepository(mongo.getLogsCollection());
        InMemoryRuleRepository ruleRepo = new InMemoryRuleRepository();
        RuleDetectionService ruleEngine = new RuleDetectionService(ruleRepo);
        ingestionService = new LogIngestionService(logRepo, ruleEngine);

        System.out.println("Demarrage du recepteur TCP sur le port " + PORT);

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("En attente de messages Syslog...");
            while (true) {
                Socket client = server.accept();
                new Thread(() -> handleClient(client)).start();
            }
        } catch (Exception e) {
            System.err.println("Impossible de demarrer le recepteur : " + e.getMessage());
        }
    }

    private static void handleClient(Socket client) {
        String ip = client.getInetAddress().getHostAddress();
        System.out.println("Connexion etablie depuis : " + client.getInetAddress());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))) {

            String rawLog;
            while ((rawLog = reader.readLine()) != null) {
                LogEvent event = normalizer.normalize(rawLog);

                if (event == null) {
                    System.out.println("Log vide ou invalide.");
                    continue;
                }

                // Compléter l'événement avant de l'envoyer à l'ingestion
                event.setId(UUID.randomUUID().toString());
                event.setReceivedTimestamp(Instant.now());
                event.setSourceAddress(ip);

                // Affichage en un seul println (évite le mélange entre clients)
                System.out.println("=== LOG NORMALISE ===\n"
                        + "Hostname : " + event.getHostname() + "\n"
                        + "Service : " + event.getService() + "\n"
                        + "Severity : " + event.getSeverity() + "\n"
                        + "Message : " + event.getMessage() + "\n"
                        + "Date evenement : " + event.getEventTimestamp() + "\n"
                        + "Categorie : " + event.getCategory());

                ingestionService.processEvent(event);
            }
            System.out.println("Deconnexion de : " + ip);

        } catch (Exception e) {
            System.err.println("Erreur client " + ip + " : " + e.getMessage());
        } finally {
            try { client.close(); } catch (Exception ignored) { }
        }
    }
}