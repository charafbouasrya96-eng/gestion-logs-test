package ma.gestionlogs.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ma.gestionlogs.model.LogEvent;

public class InMemoryLogRepository implements LogRepository {

    // Liste synchronisée : le récepteur écrit depuis plusieurs threads (un par client).
    private final List<LogEvent> database =
            Collections.synchronizedList(new ArrayList<>());

    @Override
    public void save(LogEvent event) {
        database.add(event);
        System.out.println("💾 [DATABASE] Log saved: " + event.getMessage());
    }

    @Override
    public List<LogEvent> findAll() {
        return query(e -> true);
    }

    @Override
    public List<LogEvent> findByHostname(String hostname) {
        return query(e -> hostname != null && hostname.equals(e.getHostname()));
    }

    @Override
    public List<LogEvent> findBySeverity(String severity) {
        return query(e -> severity != null && severity.equalsIgnoreCase(e.getSeverity()));
    }

    @Override
    public List<LogEvent> findByPeriod(Instant from, Instant to) {
        return query(e -> {
            Instant t = e.getReceivedTimestamp();
            return t != null && !t.isBefore(from) && t.isBefore(to);
        });
    }

    private List<LogEvent> query(Predicate<LogEvent> filter) {
        List<LogEvent> snapshot;
        synchronized (database) {
            snapshot = new ArrayList<>(database);
        }
        return snapshot.stream()
                .filter(filter)
                .sorted(Comparator.comparing(LogEvent::getReceivedTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }
}