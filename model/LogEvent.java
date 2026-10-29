
package ma.gestionlogs.model;

import java.time.Instant;

public class LogEvent {

    private String id;
    private Instant eventTimestamp;
    private Instant receivedTimestamp;
    private String hostname;
    private String service;
    private String severity;
    private String category;
    private String message;
    private String sourceAddress;
    private String username;

    public LogEvent() {
        this.receivedTimestamp = Instant.now();
    }

    public LogEvent(String hostname, String service,
                    String severity, String message) {
        this();
        this.hostname = hostname;
        this.service = service;
        this.severity = severity;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Instant eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public Instant getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public void setReceivedTimestamp(Instant receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
