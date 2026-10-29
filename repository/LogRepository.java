package ma.gestionlogs.repository;
import ma.gestionlogs.model.LogEvent;

public interface LogRepository {
    void save(LogEvent event);
}