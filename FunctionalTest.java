
package ma.gestionlogs;

import ma.gestionlogs.model.LogEvent;
import ma.gestionlogs.functional.LogProcessor;
import ma.gestionlogs.functional.EventPredicate;
import ma.gestionlogs.functional.AnomalyScorer;

public class FunctionalTest {

    public static void main(String[] args) {

        LogEvent event = new LogEvent(
            "rhel-server",
            "sshd",
            "WARNING",
            "Failed password for user admin"
        );

        // 1. Action à effectuer sur le log
        LogProcessor processor =
            log -> System.out.println("Message : " + log.getMessage());

        processor.process(event);

        // 2. Condition de détection
        EventPredicate predicate =
            log -> log.getMessage() != null
                && log.getMessage().toLowerCase().contains("failed");

        System.out.println("Echec détecté : " + predicate.test(event));

        // 3. Exemple simple de score, uniquement pour le test
        AnomalyScorer scorer =
            log -> "WARNING".equalsIgnoreCase(log.getSeverity()) ? 0.7 : 0.1;

        System.out.println("Score de test : " + scorer.calculateScore(event));
    }
}
