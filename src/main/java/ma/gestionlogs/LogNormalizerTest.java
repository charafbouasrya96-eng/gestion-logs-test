
package ma.gestionlogs;

import ma.gestionlogs.model.LogEvent;

public class LogNormalizerTest {

    public static void main(String[] args) {

        String rawLog =
            "<13>Oct 25 16:50:34 192 charafbouasrya96[4188]: TEST-LOG-SYSTEME-REEL";

        LogNormalizer normalizer = new LogNormalizer();

        LogEvent event = normalizer.normalize(rawLog);

        if (event == null) {
            System.out.println("Log invalide ou vide.");
            return;
        }

        System.out.println("Hostname : " + event.getHostname());
        System.out.println("Service : " + event.getService());
        System.out.println("Severity : " + event.getSeverity());
        System.out.println("Message : " + event.getMessage());
        System.out.println("Date de réception : "
                + event.getReceivedTimestamp());
        System.out.println("Date de l'événement : "
                + event.getEventTimestamp());
        System.out.println("Catégorie : " + event.getCategory());
    }
}
