package ma.gestionlogs.service;

import ma.gestionlogs.model.LogEvent;
import ma.gestionlogs.repository.LogRepository;

public class LogIngestionService {
    private final LogRepository logRepository;
    private final RuleDetectionService ruleDetectionService;

    public LogIngestionService(LogRepository logRepository, RuleDetectionService ruleDetectionService) {
        this.logRepository = logRepository;
        this.ruleDetectionService = ruleDetectionService;
    }

    public void processEvent(LogEvent event) {
        System.out.println("⚙️ [INGESTION] Processing event...");
        persistEvent(event);
        ruleDetectionService.evaluateRules(event);
    }

    private void persistEvent(LogEvent event) {
        logRepository.save(event);
    }
}