package ma.gestionlogs.repository;
import ma.gestionlogs.model.LogEvent;
import java.util.ArrayList;
import java.util.List;

public class InMemoryLogRepository implements LogRepository {
    private List<LogEvent> database = new ArrayList<>();

    @Override
    public void save(LogEvent event) {
        database.add(event);
        System.out.println("💾 [DATABASE] Log saved: " + event.getMessage());
    }
}