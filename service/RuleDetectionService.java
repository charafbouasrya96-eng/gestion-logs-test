package ma.gestionlogs.service;

import ma.gestionlogs.model.LogEvent;
import ma.gestionlogs.model.DetectionRule;
import ma.gestionlogs.repository.RuleRepository;
import java.util.List;

public class RuleDetectionService {
    private final RuleRepository ruleRepository;

    public RuleDetectionService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public void evaluateRules(LogEvent event) {
        System.out.println("🔍 [RULE ENGINE] Evaluating rules for event...");
        List<DetectionRule> activeRules = ruleRepository.findEnabledRules();

        for (DetectionRule rule : activeRules) {
            if (event.getMessage() != null && event.getMessage().contains(rule.getCondition())) {
                System.out.println("🚨 [ALERTE] Rule matched: " + rule.getName());
            }
        }
    }
}