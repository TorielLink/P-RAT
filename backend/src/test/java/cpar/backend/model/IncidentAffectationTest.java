package cpar.backend.model;

import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Prestataire;
import cpar.backend.model.enums.NiveauAttentionIncident;
import cpar.backend.model.enums.StatutIncident;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class IncidentAffectationTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 3, 10, 0);

    @Test
    void affecterUnIncidentNonAffecteLeFaitPasserPrisEnCharge() {
        Incident incident = incidentOuvertDepuisHeures(80);
        Prestataire maintenance = prestataire(1L);

        incident.affecterA(maintenance);

        assertSame(maintenance, incident.getPrestataire());
        assertEquals(StatutIncident.EN_COURS, incident.getStatut());
        assertEquals(NiveauAttentionIncident.PRIS_EN_CHARGE, NiveauAttentionIncident.de(incident, MAINTENANT));
    }

    @Test
    void reaffecterRemplaceLePrestataire() {
        Incident incident = incidentOuvertDepuisHeures(5);
        Prestataire nouveau = prestataire(2L);
        incident.affecterA(prestataire(1L));

        incident.affecterA(nouveau);

        assertSame(nouveau, incident.getPrestataire());
        assertEquals(StatutIncident.EN_COURS, incident.getStatut());
    }

    @Test
    void reaffecterAuMemePrestataireEstSansEffet() {
        Incident incident = incidentOuvertDepuisHeures(5);
        Prestataire actuel = prestataire(1L);
        incident.affecterA(actuel);

        incident.affecterA(prestataire(1L));

        assertSame(actuel, incident.getPrestataire());
    }

    private static Incident incidentOuvertDepuisHeures(long heures) {
        Incident incident = new Incident();
        incident.setStatut(StatutIncident.OUVERT);
        incident.setDateOuverture(MAINTENANT.minusHours(heures));
        return incident;
    }

    private static Prestataire prestataire(Long id) {
        Prestataire prestataire = new Prestataire();
        prestataire.setId(id);
        return prestataire;
    }
}