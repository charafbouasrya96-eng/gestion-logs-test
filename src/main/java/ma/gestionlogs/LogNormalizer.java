
package ma.gestionlogs;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ma.gestionlogs.model.LogEvent;

public class LogNormalizer {

    // Format RFC 3164
    private static final Pattern RFC3164_PATTERN = Pattern.compile(
        "^<(\\d+)>([A-Z][a-z]{2}\\s+\\d{1,2}\\s+\\d{2}:\\d{2}:\\d{2})\\s+(\\S+)\\s+(\\S+?)(?:\\[(\\d+)\\])?:\\s?(.*)$"
    );

    // Format RFC 5424
    private static final Pattern RFC5424_PATTERN = Pattern.compile(
        "^<(\\d{1,3})>\\d+\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(?:\\[.*?\\]|-)\\s+(.*)$"
    );

    private static final DateTimeFormatter RFC3164_DATE =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM d HH:mm:ss")
            .parseDefaulting(
                ChronoField.YEAR,
                LocalDateTime.now().getYear()
            )
            .toFormatter(Locale.ENGLISH);

    public LogEvent normalize(String rawLog) {

        if (rawLog == null || rawLog.isBlank()) {
            return null;
        }

        String line = rawLog.trim();

        LogEvent event = new LogEvent();
        event.setReceivedTimestamp(Instant.now());
        event.setMessage(line);

        Matcher matcher5424 = RFC5424_PATTERN.matcher(line);

        if (matcher5424.matches()) {
            normalizeRFC5424(matcher5424, event);
            return event;
        }

        Matcher matcher3164 = RFC3164_PATTERN.matcher(line);

        if (matcher3164.matches()) {
            normalizeRFC3164(matcher3164, event);
            return event;
        }

        event.setCategory("UNPARSED");
        return event;
    }

    private void normalizeRFC5424(
            Matcher matcher, LogEvent event) {

        int priority = Integer.parseInt(matcher.group(1));

        String timestamp = matcher.group(2);
        String hostname = matcher.group(3);
        String service = matcher.group(4);
        String message = matcher.group(7);
        event.setHostname("-".equals(hostname) ? null : hostname);
        event.setService("-".equals(service) ? null : service);
        event.setSourceAddress("-".equals(hostname) ? null : hostname);
        event.setUsername(null);

        event.setSeverity(getSeverityName(priority % 8));
        event.setCategory("SYSLOG");
        event.setMessage(message);

        try {
            if (!"-".equals(timestamp)) {
                event.setEventTimestamp(
                    OffsetDateTime.parse(timestamp).toInstant()
                );
            }
        } catch (Exception e) {
            event.setEventTimestamp(null);
        }
    }

    private void normalizeRFC3164(
            Matcher matcher, LogEvent event) {

        int priority = Integer.parseInt(matcher.group(1));

        String timestamp = matcher.group(2);
        String hostname = matcher.group(3);
        String service = matcher.group(4);
        String message = matcher.group(6);

        event.setHostname(hostname);
        event.setService(service);
        event.setSeverity(getSeverityName(priority % 8));
        event.setCategory("SYSLOG");
        event.setMessage(message);

        try {
            LocalDateTime dateTime = LocalDateTime.parse(
                timestamp.replaceAll("\\s+", " "),
                RFC3164_DATE
            );

            event.setEventTimestamp(
                dateTime.atZone(ZoneId.systemDefault()).toInstant()
            );
        } catch (Exception e) {
            event.setEventTimestamp(null);
        }
    }

    private String getSeverityName(int code) {

        switch (code) {
            case 0: return "EMERGENCY";
            case 1: return "ALERT";
            case 2: return "CRITICAL";
            case 3: return "ERROR";
            case 4: return "WARNING";
            case 5: return "NOTICE";
            case 6: return "INFO";
            case 7: return "DEBUG";
            default: return "UNKNOWN";
        }
    }
}
