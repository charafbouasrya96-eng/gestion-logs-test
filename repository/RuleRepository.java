package ma.gestionlogs.repository;
import ma.gestionlogs.model.DetectionRule;
import java.util.List;

public interface RuleRepository {
    List<DetectionRule> findEnabledRules();
}