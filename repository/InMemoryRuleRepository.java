package ma.gestionlogs.repository;
import ma.gestionlogs.model.DetectionRule;
import java.util.ArrayList;
import java.util.List;

public class InMemoryRuleRepository implements RuleRepository {
    @Override
    public List<DetectionRule> findEnabledRules() {
        List<DetectionRule> rules = new ArrayList<>();
        
        // Create a test rule looking for "ERROR"
        DetectionRule rule = new DetectionRule();
        rule.setId("R1");
        rule.setName("Detect Error");
        rule.setCondition("ERROR");
        rule.setEnabled(true);
        
        rules.add(rule);
        return rules;
    }
}