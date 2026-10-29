
package ma.gestionlogs.functional;

import ma.gestionlogs.model.LogEvent;

@FunctionalInterface
public interface LogProcessor {

    void process(LogEvent event);
}
