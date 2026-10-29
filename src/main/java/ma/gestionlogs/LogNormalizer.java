
package ma.gestionlogs;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ma.gestionlogs.model.LogEvent;

public class LogNormalizer {

    private static final Pattern SYSLOG_PATTERN = Pattern.compile(
        "^<(\\d+)>([A-Z][a-z]{2}\\s+\\d{1,2}\\s+\\d{2}:\\d{2}:\\d{2})\\s+(\\S+)\\s+(\\S+?)(?:\\[(\\d+)\\])?:\\s?(.*)$"
    );

    private static final DateTimeFormatter SYSLOG_DATE_FORMAT =
        DateTimeFormatter.ofPattern(
            "yyyy MMM d HH:mm:ss", Locale.ENGLISH
        );

    public LogEvent normalize(String rawLog) {

        if (rawLog == null || rawLog.isBlank()) {
            return null;
        }

        String line = rawLog.trim();
        Matcher matcher = SYSLOG_PATTERN.matcher(line);

        LogEvent event = new LogEvent();
        event.setReceivedTimestamp(Instant.now());

        // Conserver la ligne complète si son format est inconnu.
        event.setMessage(line);

        if (!matcher.matches()) {
            event.setCategory("UNPARSED");
            return event;
        }

        int priority = Integer.parseInt(matcher.group(1));
        int severityCode = priority % 8;

        String timestampText = matcher.group(2);
        String hostname = matcher.group(3);
        String service = matcher.group(4);
        String message = matcher.group(6);

        event.setHostname(hostname);
        event.setService(service);
        event.setSeverity(getSeverityName(severityCode));
        event.setCategory("SYSLOG");
        event.setMessage(message);

        // Le format Syslog traditionnel ne contient pas l'année.
        // On utilise provisoirement l'année actuelle.
        int currentYear = LocalDate.now().getYear();

        LocalDateTime localDateTime = LocalDateTime.parse(
            currentYear + " " + timestampText,
            SYSLOG_DATE_FORMAT
        );

        event.setEventTimestamp(
            localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        );

        return event;
    }


private String getSeverityName(int code) {
    switch (code) {
        case 0:
            return "EMERGENCY";
        case 1:
            return "ALERT";
        case 2:
            return "CRITICAL";
        case 3:
            return "ERROR";
        case 4:
            return "WARNING";
        case 5:
            return "NOTICE";
        case 6:
            return "INFO";
        case 7:
            return "DEBUG";
        default:
            return "UNKNOWN";
    }
}
}
