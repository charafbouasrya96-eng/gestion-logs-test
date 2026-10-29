
package ma.gestionlogs.functional;

import ma.gestionlogs.model.LogEvent;

@FunctionalInterface
public interface AnomalyScorer {

    double calculateScore(LogEvent event);
}
