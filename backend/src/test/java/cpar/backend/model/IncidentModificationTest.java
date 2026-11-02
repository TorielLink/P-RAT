package cpar.backend.model;

import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Utilisateur;
import cpar.backend.model.enums.ProfilUtilisateur;
import cpar.backend.model.enums.StatutIncident;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncidentModificationTest {

    private static final Utilisateur ADMIN = utilisateur(1L, ProfilUtilisateur.ADMIN);
    private static final Utilisateur AGENT = utilisateur(2L, ProfilUtilisateur.AGENT);
    private static final Utilisateur AUTRE_AGENT = utilisateur(3L, ProfilUtilisateur.AGENT);
    private static final Utilisateur PRESTATAIRE = utilisateur(4L, ProfilUtilisateur.PRESTATAIRE);

    @Test
    void lAdminModifieUnIncidentAffecte() {
        assertTrue(incident(StatutIncident.EN_COURS, AGENT).estModifiablePar(ADMIN));
    }

    @Test
    void personneNeModifieUnIncidentResolu() {
        Incident resolu = incident(StatutIncident.RESOLU, AGENT);
        assertFalse(resolu.estModifiablePar(ADMIN));
        assertFalse(resolu.estModifiablePar(AGENT));
    }

    @Test
    void unAgentModifieSonIncidentNonAffecte() {
        assertTrue(incident(StatutIncident.OUVERT, AGENT).estModifiablePar(AGENT));
    }

    @Test
    void unAgentNeModifiePasLIncidentDUnAutreAgent() {
        assertFalse(incident(StatutIncident.OUVERT, AUTRE_AGENT).estModifiablePar(AGENT));
    }

    @Test
    void unAgentNeModifiePasSonIncidentDejaAffecte() {
        assertFalse(incident(StatutIncident.EN_COURS, AGENT).estModifiablePar(AGENT));
    }

    @Test
    void unPrestataireNeModifieJamais() {
        assertFalse(incident(StatutIncident.OUVERT, AGENT).estModifiablePar(PRESTATAIRE));
    }

    private static Incident incident(StatutIncident statut, Utilisateur declarant) {
        Incident incident = new Incident();
        incident.setStatut(statut);
        incident.setUtilisateur(declarant);
        return incident;
    }

    private static Utilisateur utilisateur(Long id, ProfilUtilisateur profil) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(id);
        utilisateur.setProfil(profil);
        return utilisateur;
    }
}