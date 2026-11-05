package cpar.backend.logs;

import java.time.LocalDateTime;

import cpar.backend.model.enums.TypeEvenementIncident;

public record EvenementIncident(
        Long incidentId,
        String auteurLogin,
        LocalDateTime dateEvenement,
        TypeEvenementIncident type,
        String champ,
        String ancienneValeur,
        String nouvelleValeur) {

    static final String SEPARATEUR = "|";

    // Une ligne du fichier : date|auteur|type|champ|ancienne|nouvelle
    public String versLigne() {
        return String.join(SEPARATEUR,
                dateEvenement.withNano(0).toString(),
                nettoyer(auteurLogin),
                type.name(),
                nettoyer(champ),
                nettoyer(ancienneValeur),
                nettoyer(nouvelleValeur));
    }

    private static String nettoyer(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur.replace("\r", " ").replace("\n", " ").replace(SEPARATEUR, "/");
    }
}