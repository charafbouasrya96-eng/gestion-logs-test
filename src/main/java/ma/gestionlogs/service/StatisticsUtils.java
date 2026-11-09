package ma.gestionlogs.service;

import java.util.Collection;

/**
 * Fonctions de calcul statistique utilisees par la detection d'anomalies.
 */
public final class StatisticsUtils {

    private StatisticsUtils() {
        // On interdit de faire "new StatisticsUtils()" : cette classe ne sert qu'a calculer.
    }

    /**
     * Moyenne = (x1 + x2 + ... + xN) / N.
     */
    public static double moyenne(Collection<Double> valeurs) {
        if (valeurs == null || valeurs.isEmpty()) {
            throw new IllegalArgumentException("moyenne : la liste est null ou vide");
        }
        return valeurs.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElseThrow();
    }

    /**
     * Ecart-type d'echantillon : racine( somme((xi - moyenne)^2) / (N - 1) ).
     * Renvoie 0.0 s'il y a moins de 2 valeurs (on ne peut pas mesurer un ecart avec 1 seule valeur).
     */
    public static double ecartTypeEchantillon(Collection<Double> valeurs) {
        if (valeurs == null) {
            throw new IllegalArgumentException("ecartTypeEchantillon : la liste est null");
        }
        if (valeurs.size() < 2) {
            return 0.0;
        }
        double moyenneDesValeurs = moyenne(valeurs);
        double sommeDesCarres = valeurs.stream()
                .mapToDouble(v -> (v - moyenneDesValeurs) * (v - moyenneDesValeurs))
                .sum();
        return Math.sqrt(sommeDesCarres / (valeurs.size() - 1));
    }
    
    /**
     * Ecart-type effectif = max(ecartType ; ecartTypeMin).
     * Evite la division par zero quand l'historique est parfaitement plat.
     */
    public static double ecartTypeEffectif(double ecartType, double ecartTypeMin) {
        return Math.max(ecartType, ecartTypeMin);
    }

    /**
     * Score z = (valeur - moyenne) / ecartTypeEffectif.
     * Dit de combien d'ecarts-types la valeur s'eloigne de la moyenne.
     */
    public static double scoreZ(double valeur, double moyenne, double ecartTypeEffectif) {
        if (ecartTypeEffectif <= 0.0) {
            throw new IllegalArgumentException(
                    "scoreZ : l'ecart-type effectif doit etre > 0 (recu : " + ecartTypeEffectif + ")");
        }
        return (valeur - moyenne) / ecartTypeEffectif;
    }
    

    /**
     * Score d'anomalie entre 0 et 1 : min(1 ; max(0 ; z / zMax)).
     * Un z negatif (valeur sous la moyenne) donne 0 ; un z >= zMax donne 1.
     */
    public static double scoreAnomalie(double z, double zMax) {
        if (zMax <= 0.0) {
            throw new IllegalArgumentException(
                    "scoreAnomalie : zMax doit etre > 0 (recu : " + zMax + ")");
        }
        return Math.min(1.0, Math.max(0.0, z / zMax));
    }
}