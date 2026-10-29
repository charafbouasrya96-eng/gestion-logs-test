
package ma.gestionlogs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import ma.gestionlogs.model.LogEvent;

public class SyslogTcpReceiver {

    private static final int PORT = 5514;

    public static void main(String[] args) {

        LogNormalizer normalizer = new LogNormalizer();

        System.out.println(
            "Demarrage du recepteur TCP sur le port " + PORT
        );

        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println("En attente de messages Syslog...");

            while (true) {

                try (Socket client = server.accept();
                     BufferedReader reader = new BufferedReader(
                         new InputStreamReader(
                             client.getInputStream(),
                             StandardCharsets.UTF_8))) {

                    System.out.println(
                        "Connexion depuis : " + client.getInetAddress()
                    );

                    String rawLog;

                    while ((rawLog = reader.readLine()) != null) {

                        // 1. Afficher le message brut reçu
                        System.out.println("\n=== LOG BRUT ===");
                        System.out.println(rawLog);

                        // 2. Normaliser le log
                        LogEvent event = normalizer.normalize(rawLog);

                        // 3. Afficher le résultat normalisé
                        if (event != null) {

                            System.out.println("=== LOG NORMALISE ===");
                            System.out.println(
                                "Hostname : " + event.getHostname()
                            );
                            System.out.println(
                                "Service : " + event.getService()
                            );
                            System.out.println(
                                "Severity : " + event.getSeverity()
                            );
                            System.out.println(
                                "Message : " + event.getMessage()
                            );
                            System.out.println(
                                "Date evenement : "
                                + event.getEventTimestamp()
                            );
                            System.out.println(
                                "Date reception : "
                                + event.getReceivedTimestamp()
                            );
                            System.out.println(
                                "Categorie : " + event.getCategory()
                            );

                        } else {
                            System.out.println(
                                "Log vide ou invalide."
                            );
                        }
                    }

                } catch (Exception e) {
                    System.err.println(
                        "Erreur client : " + e.getMessage()
                    );
                }
            }

        } catch (Exception e) {
            System.err.println(
                "Impossible de demarrer le recepteur : "
                + e.getMessage()
            );
        }
    }
}
