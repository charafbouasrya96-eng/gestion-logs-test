
package ma.gestionlogs;

import java.time.Instant;

import ma.gestionlogs.model.LogEvent;

public class LogNormalizerTest {

    public static void main(String[] args) {

        LogNormalizer normalizer = new LogNormalizer();

        // TEST 1 : Format RFC 3164
        System.out.println("========== TEST RFC 3164 ==========");

        String rfc3164 =
            "<13>Oct 25 16:50:34 192 charafbouasrya96[4188]: TEST-LOG-SYSTEME-REEL";

        LogEvent event3164 = normalizer.normalize(rfc3164);

        check(event3164 != null, "RFC 3164 : événement non null");
        check("192".equals(event3164.getHostname()), "RFC 3164 : hostname");
        check("charafbouasrya96".equals(event3164.getService()), "RFC 3164 : service");
        check("NOTICE".equals(event3164.getSeverity()), "RFC 3164 : severity");
        check("TEST-LOG-SYSTEME-REEL".equals(event3164.getMessage()), "RFC 3164 : message");
        check("SYSLOG".equals(event3164.getCategory()), "RFC 3164 : catégorie");
        check(event3164.getEventTimestamp() != null, "RFC 3164 : date événement");

        // TEST 2 : Format RFC 5424 reçu depuis RHEL
        System.out.println("\n========== TEST RFC 5424 ==========");

        String rfc5424 =
            "<13>1 2026-11-06T18:17:56.580258+01:00 " +
            "192.168.159.128 charafbouasrya96 - - " +
            "[timeQuality tzKnown=\"1\" isSynced=\"0\"] " +
            "TEST_DIAGNOSTIC_CHARAF";

        LogEvent event5424 = normalizer.normalize(rfc5424);

        check(event5424 != null, "RFC 5424 : événement non null");
        check("192.168.159.128".equals(event5424.getHostname()), "RFC 5424 : hostname");
        check("charafbouasrya96".equals(event5424.getService()), "RFC 5424 : service");
        check("NOTICE".equals(event5424.getSeverity()), "RFC 5424 : severity");
        check("TEST_DIAGNOSTIC_CHARAF".equals(event5424.getMessage()), "RFC 5424 : message");
        check("SYSLOG".equals(event5424.getCategory()), "RFC 5424 : catégorie");

        Instant expectedTimestamp =
            Instant.parse("2026-11-06T17:17:56.580258Z");

        check(
            expectedTimestamp.equals(event5424.getEventTimestamp()),
            "RFC 5424 : conversion du timestamp"
        );

        // TEST 3 : RFC 5424 sans timestamp disponible
        System.out.println("\n========== TEST RFC 5424 SANS DATE ==========");

        String rfc5424NoDate =
            "<13>1 - 192.168.159.128 sshd 1234 ID47 - " +
            "Failed password for user admin";

        LogEvent eventNoDate = normalizer.normalize(rfc5424NoDate);

        check(eventNoDate != null, "RFC 5424 sans date : événement non null");
        check("sshd".equals(eventNoDate.getService()), "RFC 5424 sans date : service");
        check(eventNoDate.getEventTimestamp() == null, "RFC 5424 sans date : timestamp null");
        check(
            "Failed password for user admin".equals(eventNoDate.getMessage()),
            "RFC 5424 sans date : message"
        );

        System.out.println("\n========== FIN DES TESTS ==========");
    }

    private static void check(boolean condition, String description) {

        if (condition) {
            System.out.println("[OK] " + description);
        } else {
            System.err.println("[ECHEC] " + description);
            throw new AssertionError(description);
        }
    }
}
