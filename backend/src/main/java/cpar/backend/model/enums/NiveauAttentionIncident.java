package cpar.backend.model.enums;

import cpar.backend.model.entities.Incident;

import java.time.Duration;
import java.time.LocalDateTime;

public enum NiveauAttentionIncident {
    VERT,
    ORANGE,
    ROUGE,
    PRIS_EN_CHARGE,
    RESOLU;

    public static final Duration SEUIL_ORANGE = Duration.ofHours(48);
    public static final Duration SEUIL_ROUGE = Duration.ofHours(72);

    public static NiveauAttentionIncident de(Incident incident, LocalDateTime maintenant) {
        if (incident.getStatut() == StatutIncident.RESOLU) {
            return RESOLU;
        }

        if (incident.getStatut() == StatutIncident.EN_COURS) {
            return PRIS_EN_CHARGE;
        }

        Duration ouverture = incident.dureeOuverture(maintenant);

        if (ouverture.compareTo(SEUIL_ROUGE) >= 0) {
            return ROUGE;
        }

        if (ouverture.compareTo(SEUIL_ORANGE) >= 0) {
            return ORANGE;
        }

        return VERT;
    }
}
