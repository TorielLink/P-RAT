package cpar.backend.model;

import cpar.backend.model.entities.Incident;
import cpar.backend.model.enums.NiveauAttentionIncident;
import cpar.backend.model.enums.StatutIncident;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NiveauAttentionIncidentTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 30, 10, 0);

    private static Incident incidentOuvertDepuis(Duration duree) {
        Incident incident = new Incident();
        incident.setStatut(StatutIncident.OUVERT);
        incident.setDateOuverture(MAINTENANT.minus(duree));
        return incident;
    }

    @Test
    void unIncidentNonAffecteOuvertDepuis47HeuresEstVert() {
        assertEquals(NiveauAttentionIncident.VERT, NiveauAttentionIncident.de(incidentOuvertDepuis(Duration.ofHours(47)), MAINTENANT));
    }

    @Test
    void aQuaranteHuitHeuresUnIncidentDevientOrange() {
        assertEquals(NiveauAttentionIncident.ORANGE, NiveauAttentionIncident.de(incidentOuvertDepuis(Duration.ofHours(48)), MAINTENANT));
    }

    @Test
    void aSoixanteDouzeHeuresUnIncidentDevientRouge() {
        assertEquals(NiveauAttentionIncident.ROUGE, NiveauAttentionIncident.de(incidentOuvertDepuis(Duration.ofHours(72)), MAINTENANT));
    }

    @Test
    void unIncidentAffecteEstPrisEnChargeQuelleQueSoitSaDuree() {
        Incident incident = incidentOuvertDepuis(Duration.ofHours(100));
        incident.setStatut(StatutIncident.EN_COURS);
        assertEquals(NiveauAttentionIncident.PRIS_EN_CHARGE, NiveauAttentionIncident.de(incident, MAINTENANT));
    }

    @Test
    void laDureeDUnIncidentResoluEstFigeeALaResolution() {
        Incident incident = incidentOuvertDepuis(Duration.ofDays(5));
        incident.setStatut(StatutIncident.RESOLU);
        incident.setDateResolution(MAINTENANT.minusDays(3));
        assertEquals(Duration.ofDays(2), incident.dureeOuverture(MAINTENANT));
    }
}
