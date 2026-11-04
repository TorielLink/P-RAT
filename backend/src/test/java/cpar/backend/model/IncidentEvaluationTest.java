package cpar.backend.model;

import cpar.backend.exception.EvaluationImpossibleException;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Prestataire;
import cpar.backend.model.enums.StatutIncident;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncidentEvaluationTest {

    @Test
    void unIncidentNonResoluNePeutPasEtreEvalue() {
        Incident incident = incident(StatutIncident.EN_COURS, new Prestataire());

        assertThrows(EvaluationImpossibleException.class, incident::verifierQuIlPeutEtreEvalue);
    }

    @Test
    void unIncidentResoluSansPrestataireNePeutPasEtreEvalue() {
        Incident incident = incident(StatutIncident.RESOLU, null);

        assertThrows(EvaluationImpossibleException.class, incident::verifierQuIlPeutEtreEvalue);
    }

    @Test
    void unIncidentResoluEtAffectePeutEtreEvalue() {
        Incident incident = incident(StatutIncident.RESOLU, new Prestataire());

        assertDoesNotThrow(incident::verifierQuIlPeutEtreEvalue);
    }

    private static Incident incident(StatutIncident statut, Prestataire prestataire) {
        Incident incident = new Incident();
        incident.setStatut(statut);
        incident.setPrestataire(prestataire);
        return incident;
    }
}