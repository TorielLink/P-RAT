package cpar.backend.service;

import cpar.backend.dto.DeclarationIncidentRequest;
import cpar.backend.dto.IncidentDetailDto;
import cpar.backend.dto.ModificationIncidentRequest;
import cpar.backend.exception.EquipementIntrouvableException;
import cpar.backend.exception.IncidentIntrouvableException;
import cpar.backend.exception.IncidentResoluException;
import cpar.backend.exception.OperationInterditeException;
import cpar.backend.model.entities.Equipement;
import cpar.backend.model.entities.Evaluation;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Prestataire;
import cpar.backend.model.entities.Utilisateur;
import cpar.backend.model.enums.NiveauAttentionIncident;
import cpar.backend.model.enums.PrioriteIncident;
import cpar.backend.model.enums.ProfilUtilisateur;
import cpar.backend.model.enums.StatutIncident;
import cpar.backend.repository.EvaluationRepository;
import cpar.backend.repository.IncidentRepository;
import cpar.backend.repository.PrestataireRepository;
import cpar.backend.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 30, 10, 0);
    private static final ZoneId ZONE = ZoneId.systemDefault();

    @Mock private IncidentRepository incidentRepository;
    @Mock private PrestataireRepository prestataireRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private EvaluationRepository evaluationRepository;
    @Mock private IEquipementService equipementService;

    private IncidentService incidentService;

    @BeforeEach
    void creeServiceAvecHorlogeFigee() {
        Clock horlogeFigee = Clock.fixed(MAINTENANT.atZone(ZONE).toInstant(), ZONE);
        incidentService = new IncidentService(incidentRepository, prestataireRepository,
                utilisateurRepository, evaluationRepository, equipementService, horlogeFigee);
    }

    @Test
    void unIncidentDeclareDemarreSansPrestataireAvecDateDeLHorloge() {
        when(equipementService.obtenirEquipementParId(3L)).thenReturn(new Equipement());
        when(utilisateurRepository.findByLogin("agent")).thenReturn(Optional.of(compte(ProfilUtilisateur.AGENT, null)));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(appel -> appel.getArgument(0));

        Incident incident = incidentService.declarerIncident(3L,
                new DeclarationIncidentRequest("  La caméra ne répond plus  ", null), "agent");

        assertNull(incident.getPrestataire());
        assertEquals(StatutIncident.OUVERT, incident.getStatut());
        assertEquals(MAINTENANT, incident.getDateOuverture());
        assertNull(incident.getDateResolution());
        assertEquals("La caméra ne répond plus", incident.getDescription());
    }

    @Test
    void unEquipementInexistantEmpecheDeclarationEtRienNEstEnregistre() {
        when(equipementService.obtenirEquipementParId(999L)).thenThrow(new EquipementIntrouvableException(999L));

        assertThrows(EquipementIntrouvableException.class, () -> incidentService.declarerIncident(999L,
                new DeclarationIncidentRequest("La caméra ne répond plus", null), "agent"));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void leDetailCalculeDureeEtNiveauDAttentionAvecLHorloge() {
        Incident incident = incidentOuvertDepuis(Duration.ofHours(50));
        when(incidentRepository.findWithDetailsById(1L)).thenReturn(Optional.of(incident));
        when(utilisateurRepository.findByLogin("agent")).thenReturn(Optional.of(compte(ProfilUtilisateur.AGENT, null)));
        when(evaluationRepository.findByIncidentId(1L)).thenReturn(Optional.empty());

        IncidentDetailDto detail = incidentService.obtenirDetailIncident(1L, "agent");

        assertEquals(Duration.ofHours(50).toMinutes(), detail.dureeOuvertureMinutes());
        assertEquals(NiveauAttentionIncident.ORANGE, detail.niveauAttention());
        assertNull(detail.evaluation());
    }

    @Test
    void unIncidentResoluAUneDureeFigeeEtAfficheSonEvaluation() {
        Incident incident = incidentOuvertDepuis(Duration.ofDays(5));
        incident.setStatut(StatutIncident.RESOLU);
        incident.setDateResolution(MAINTENANT.minusDays(3));
        Evaluation evaluation = new Evaluation();
        evaluation.setNote(4);
        evaluation.setCommentaire("Intervention rapide");

        when(incidentRepository.findWithDetailsById(1L)).thenReturn(Optional.of(incident));
        when(utilisateurRepository.findByLogin("agent")).thenReturn(Optional.of(compte(ProfilUtilisateur.AGENT, null)));
        when(evaluationRepository.findByIncidentId(1L)).thenReturn(Optional.of(evaluation));

        IncidentDetailDto detail = incidentService.obtenirDetailIncident(1L, "agent");

        assertEquals(Duration.ofDays(2).toMinutes(), detail.dureeOuvertureMinutes());
        assertEquals(NiveauAttentionIncident.RESOLU, detail.niveauAttention());
        assertEquals(4, detail.evaluation().note());
    }

    @Test
    void unPrestataireNePeutPasVoirLIncidentDUnAutrePrestataire() {
        Incident incident = incidentOuvertDepuis(Duration.ofHours(10));
        incident.setPrestataire(entreprise(8L));
        when(incidentRepository.findWithDetailsById(5L)).thenReturn(Optional.of(incident));
        when(utilisateurRepository.findByLogin("prestataire"))
                .thenReturn(Optional.of(compte(ProfilUtilisateur.PRESTATAIRE, entreprise(7L))));

        assertThrows(IncidentIntrouvableException.class,
                () -> incidentService.obtenirDetailIncident(5L, "prestataire"));
    }

    @Test
    void laDateDeResolutionVientDeLHorloge() {
        when(incidentRepository.findWithDetailsById(1L)).thenReturn(Optional.of(incidentOuvertDepuis(Duration.ofHours(10))));

        Incident resolu = incidentService.resoudreIncident(1L);

        assertEquals(StatutIncident.RESOLU, resolu.getStatut());
        assertEquals(MAINTENANT, resolu.getDateResolution());
    }

    // -------- MODIFICATION --------

    @Test
    void unIncidentResoluNePeutPlusEtreModifie() {
        Incident resolu = incidentOuvertDepuis(Duration.ofDays(2));
        resolu.setStatut(StatutIncident.RESOLU);
        resolu.setDescription("Description d'origine");
        when(incidentRepository.findWithDetailsById(1L)).thenReturn(Optional.of(resolu));

        assertThrows(IncidentResoluException.class, () -> incidentService.modifierIncident(1L,
                new ModificationIncidentRequest("Nouvelle description valide", null), "admin"));
        assertEquals("Description d'origine", resolu.getDescription());
    }

    @Test
    void unAgentNePeutPasModifierLIncidentDUnAutreAgent() {
        Utilisateur auteur = compte(ProfilUtilisateur.AGENT, null);
        auteur.setId(2L);
        Utilisateur autreAgent = compte(ProfilUtilisateur.AGENT, null);
        autreAgent.setId(3L);
        Incident incident = incidentOuvertDepuis(Duration.ofHours(5));
        incident.setUtilisateur(auteur);

        when(incidentRepository.findWithDetailsById(1L)).thenReturn(Optional.of(incident));
        when(utilisateurRepository.findByLogin("autreAgent")).thenReturn(Optional.of(autreAgent));

        assertThrows(OperationInterditeException.class, () -> incidentService.modifierIncident(1L,
                new ModificationIncidentRequest("Nouvelle description valide", null), "autreAgent"));
    }

    @Test
    void unAgentModifieSonIncidentNonAffecte() {
        Utilisateur agent = compte(ProfilUtilisateur.AGENT, null);
        agent.setId(2L);
        Incident incident = incidentOuvertDepuis(Duration.ofHours(5));
        incident.setUtilisateur(agent);

        when(incidentRepository.findWithDetailsById(1L)).thenReturn(Optional.of(incident));
        when(utilisateurRepository.findByLogin("agent")).thenReturn(Optional.of(agent));

        Incident modifie = incidentService.modifierIncident(1L,
                new ModificationIncidentRequest("  Nouvelle description valide  ", PrioriteIncident.CRITIQUE), "agent");

        assertEquals("Nouvelle description valide", modifie.getDescription());
        assertEquals(PrioriteIncident.CRITIQUE, modifie.getPriorite());
    }

    @Test
    void lesChampsNonModifiablesRestentInchanges() {
        Equipement equipement = new Equipement();
        Prestataire prestataire = entreprise(7L);
        Incident incident = incidentOuvertDepuis(Duration.ofHours(30));
        incident.setEquipement(equipement);
        incident.setStatut(StatutIncident.EN_COURS);
        incident.setPrestataire(prestataire);
        LocalDateTime ouverture = incident.getDateOuverture();

        when(incidentRepository.findWithDetailsById(1L)).thenReturn(Optional.of(incident));
        when(utilisateurRepository.findByLogin("admin")).thenReturn(Optional.of(compte(ProfilUtilisateur.ADMIN, null)));

        Incident modifie = incidentService.modifierIncident(1L,
                new ModificationIncidentRequest("Nouvelle description valide", null), "admin");

        assertSame(equipement, modifie.getEquipement());
        assertSame(prestataire, modifie.getPrestataire());
        assertEquals(StatutIncident.EN_COURS, modifie.getStatut());
        assertEquals(ouverture, modifie.getDateOuverture());
        assertNull(modifie.getDateResolution());
        assertEquals(PrioriteIncident.MOYENNE, modifie.getPriorite());
    }

    private static Incident incidentOuvertDepuis(Duration duree) {
        Incident incident = new Incident();
        incident.setEquipement(new Equipement());
        incident.setStatut(StatutIncident.OUVERT);
        incident.setDateOuverture(MAINTENANT.minus(duree));
        return incident;
    }

    private static Prestataire entreprise(Long id) {
        Prestataire prestataire = new Prestataire();
        prestataire.setId(id);
        return prestataire;
    }

    private static Utilisateur compte(ProfilUtilisateur profil, Prestataire prestataire) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setProfil(profil);
        utilisateur.setPrestataire(prestataire);
        return utilisateur;
    }
}