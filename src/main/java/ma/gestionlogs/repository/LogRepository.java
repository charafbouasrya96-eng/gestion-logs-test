package ma.gestionlogs.repository;

import java.time.Instant;
import java.util.List;

import ma.gestionlogs.model.LogEvent;

public interface LogRepository {

    void save(LogEvent event);

    /** Tous les logs, du plus récent au plus ancien. */
    List<LogEvent> findAll();

    /** Logs d'une machine, du plus récent au plus ancien. */
    List<LogEvent> findByHostname(String hostname);

    /** Logs d'une sévérité donnée (ex. "ERROR"), du plus récent au plus ancien. */
    List<LogEvent> findBySeverity(String severity);

    /** Logs reçus entre from (inclus) et to (exclu), du plus récent au plus ancien. */
    List<LogEvent> findByPeriod(Instant from, Instant to);
}