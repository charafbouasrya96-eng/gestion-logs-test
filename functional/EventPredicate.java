
package ma.gestionlogs.functional;

import ma.gestionlogs.model.LogEvent;

@FunctionalInterface
public interface EventPredicate {

    boolean test(LogEvent event);
}
