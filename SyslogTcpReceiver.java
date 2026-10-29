package ma.gestionlogs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import ma.gestionlogs.model.LogEvent;
import ma.gestionlogs.repository.InMemoryLogRepository;
import ma.gestionlogs.repository.InMemoryRuleRepository;
import ma.gestionlogs.service.LogIngestionService;
import ma.gestionlogs.service.RuleDetectionService;

public class SyslogTcpReceiver {

    private static final int PORT = 5514;
    private static LogIngestionService ingestionService;

    public static void main(String[] args) {
        // 1. WIRE UP THE DEPENDENCIES
        InMemoryLogRepository logRepo = new InMemoryLogRepository();
        InMemoryRuleRepository ruleRepo = new InMemoryRuleRepository();
        RuleDetectionService ruleEngine = new RuleDetectionService(ruleRepo);
        ingestionService = new LogIngestionService(logRepo, ruleEngine);

        // 2. START TCP SERVER
        System.out.println("Demarrage du recepteur TCP sur le port " + PORT);

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("En attente de messages Syslog...");

            while (true) {
                Socket client = server.accept();
                new Thread(() -> handleClient(client)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket client) {
        String ip = client.getInetAddress().getHostAddress();
        System.out.println("Connexion depuis : " + ip);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))) {
            
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("MESSAGE RECU : " + message);
                
                // 3. CONVERT TO YOUR LogEvent AND SEND TO INGESTION
                LogEvent event = new LogEvent();
                event.setId(UUID.randomUUID().toString());
                event.setReceivedTimestamp(Instant.now()); // Your Instant model!
                event.setSourceAddress(ip);
                event.setMessage(message);
                
                ingestionService.processEvent(event);
            }
            
            System.out.println("Deconnexion de : " + ip);

        } catch (Exception e) {
            System.out.println("Erreur avec le client " + ip + " : " + e.getMessage());
        } finally {
            try { client.close(); } catch (Exception e) {}
        }
    }
}