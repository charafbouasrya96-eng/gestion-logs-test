package ma.gestionlogs;

import java.util.List;
import java.util.function.BooleanSupplier;

import ma.gestionlogs.service.StatisticsUtils;

/**
 * Tests automatiques de StatisticsUtils (tests T1 et T2 du cahier des charges).
 * Classe avec main, comme LogNormalizerTest : pas besoin de JUnit.
 */
public class StatisticsUtilsTest {

    private static final double TOLERANCE = 0.01;
    private static final double ZMAX = 6.0;
    private static final double ECART_TYPE_MIN = 1.0;

    private static int reussis = 0;
    private static int echoues = 0;

    // Historique du cas A (N = 10 fenetres)
    private static final List<Double> HISTORIQUE_A =
            List.of(8.0, 10.0, 9.0, 11.0, 10.0, 12.0, 9.0, 10.0, 11.0, 10.0);

    // Historique parfaitement plat des cas D et E : dix fois 10
    private static final List<Double> HISTORIQUE_PLAT =
            List.of(10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0);

    public static void main(String[] args) {
        testerT1();
        testerCasA();
        testerCasB();
        testerCasC();
        testerCasD();
        testerCasE();
        testerEntreesInvalides();

        System.out.println();
        System.out.println("Resultat : " + reussis + " reussi(s), " + echoues + " echec(s)");
        if (echoues > 0) {
            System.exit(1);
        }
    }

    // ---------- Les tests ----------

    private static void testerT1() {
        List<Double> donnees = List.of(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
        verifier("T1 moyenne = 5",
                () -> egalApprox(StatisticsUtils.moyenne(donnees), 5.0));
        verifier("T1 ecart-type d'echantillon = 2,138",
                () -> egalApprox(StatisticsUtils.ecartTypeEchantillon(donnees), 2.138));
    }

    private static void testerCasA() {
        double z = calculerZ(HISTORIQUE_A, 25.0);
        verifier("Cas A moyenne = 10",
                () -> egalApprox(StatisticsUtils.moyenne(HISTORIQUE_A), 10.0));
        verifier("Cas A ecart-type = 1,155",
                () -> egalApprox(StatisticsUtils.ecartTypeEchantillon(HISTORIQUE_A), 1.155));
        verifier("Cas A z = 12,99",
                () -> egalApprox(z, 12.99));
        verifier("Cas A score = 1",
                () -> egalApprox(StatisticsUtils.scoreAnomalie(z, ZMAX), 1.0));
    }

    private static void testerCasB() {
        double z = calculerZ(HISTORIQUE_A, 14.0);
        verifier("Cas B z = 3,46",
                () -> egalApprox(z, 3.46));
        verifier("Cas B score = 0,577",
                () -> egalApprox(StatisticsUtils.scoreAnomalie(z, ZMAX), 0.577));
        verifier("Cas B z >= 3 (c'est une anomalie)",
                () -> z >= 3.0);
    }

    private static void testerCasC() {
        double z = calculerZ(HISTORIQUE_A, 12.0);
        verifier("Cas C z = 1,73",
                () -> egalApprox(z, 1.73));
        verifier("Cas C z < 3 (aucune anomalie)",
                () -> z < 3.0);
    }

    private static void testerCasD() {
        double ecartType = StatisticsUtils.ecartTypeEchantillon(HISTORIQUE_PLAT);
        double z = calculerZ(HISTORIQUE_PLAT, 11.0);
        verifier("Cas D ecart-type = 0",
                () -> egalApprox(ecartType, 0.0));
        verifier("Cas D ecart-type effectif = 1 (grace au minimum)",
                () -> egalApprox(StatisticsUtils.ecartTypeEffectif(ecartType, ECART_TYPE_MIN), 1.0));
        verifier("Cas D z = 1 (aucune anomalie)",
                () -> egalApprox(z, 1.0));
    }

    private static void testerCasE() {
        double z = calculerZ(HISTORIQUE_PLAT, 40.0);
        verifier("Cas E z = 30",
                () -> egalApprox(z, 30.0));
        verifier("Cas E score = 1",
                () -> egalApprox(StatisticsUtils.scoreAnomalie(z, ZMAX), 1.0));
    }

    private static void testerEntreesInvalides() {
        verifier("moyenne d'une liste vide leve une exception",
                () -> leveUneException(() -> StatisticsUtils.moyenne(List.of())));
        verifier("scoreZ avec ecart-type 0 leve une exception",
                () -> leveUneException(() -> StatisticsUtils.scoreZ(1.0, 1.0, 0.0)));
    }

    // ---------- Les outils de test ----------

    /** Calcule le z d'une valeur par rapport a un historique. */
    private static double calculerZ(List<Double> historique, double valeurCible) {
        double moyenne = StatisticsUtils.moyenne(historique);
        double ecartTypeEff = StatisticsUtils.ecartTypeEffectif(
                StatisticsUtils.ecartTypeEchantillon(historique), ECART_TYPE_MIN);
        return StatisticsUtils.scoreZ(valeurCible, moyenne, ecartTypeEff);
    }

    /** Lance une verification et affiche [OK] ou [ECHEC]. */
    private static void verifier(String nom, BooleanSupplier condition) {
        try {
            if (condition.getAsBoolean()) {
                reussis++;
                System.out.println("[OK]    " + nom);
            } else {
                echoues++;
                System.out.println("[ECHEC] " + nom);
            }
        } catch (RuntimeException e) {
            echoues++;
            System.out.println("[ECHEC] " + nom + " -> exception inattendue : " + e);
        }
    }

    /** Vrai si les deux nombres sont egaux a TOLERANCE pres. */
    private static boolean egalApprox(double reel, double attendu) {
        return Math.abs(reel - attendu) <= TOLERANCE;
    }

    /** Vrai si l'action leve bien une IllegalArgumentException. */
    private static boolean leveUneException(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}