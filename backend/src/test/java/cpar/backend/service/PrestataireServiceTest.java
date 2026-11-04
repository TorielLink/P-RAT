package cpar.backend.service;

import cpar.backend.dto.PrestataireResumeDto;
import cpar.backend.dto.StatistiquesPrestataire;
import cpar.backend.exception.PrestataireIntrouvableException;
import cpar.backend.model.entities.Prestataire;
import cpar.backend.model.entities.Utilisateur;
import cpar.backend.model.enums.ProfilUtilisateur;
import cpar.backend.repository.EvaluationRepository;
import cpar.backend.repository.PrestataireRepository;
import cpar.backend.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestataireServiceTest {

    @Mock private PrestataireRepository prestataireRepository;
    @Mock private EvaluationRepository evaluationRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @InjectMocks private PrestataireService prestataireService;

    @Test
    void laMoyenneEtLeNombreDEvaluationsSontAssociesAChaquePrestataire() {
        when(utilisateurRepository.findByLogin("admin")).thenReturn(Optional.of(compte(ProfilUtilisateur.ADMIN, null)));
        when(prestataireRepository.findAll(any(Sort.class))).thenReturn(List.of(prestataire(1L), prestataire(2L)));
        when(evaluationRepository.calculerStatistiquesParPrestataire())
                .thenReturn(List.of(new StatistiquesPrestataire(1L, 4.5, 2L)));

        List<PrestataireResumeDto> resultat = prestataireService.obtenirPrestataires("admin");

        assertEquals(4.5, resultat.get(0).noteMoyenne());
        assertEquals(2, resultat.get(0).nombreEvaluations());
        assertNull(resultat.get(1).noteMoyenne());
        assertEquals(0, resultat.get(1).nombreEvaluations());
    }

    @Test
    void unPrestataireNeVoitQueSonEntreprise() {
        when(utilisateurRepository.findByLogin("prestataire"))
                .thenReturn(Optional.of(compte(ProfilUtilisateur.PRESTATAIRE, prestataire(1L))));
        when(prestataireRepository.findById(1L)).thenReturn(Optional.of(prestataire(1L)));
        when(evaluationRepository.calculerStatistiquesParPrestataire()).thenReturn(List.of());

        List<PrestataireResumeDto> resultat = prestataireService.obtenirPrestataires("prestataire");

        assertEquals(1, resultat.size());
        assertEquals(1L, resultat.getFirst().id());
    }

    @Test
    void unPrestataireNeVoitPasLaFicheDUnAutre() {
        when(utilisateurRepository.findByLogin("prestataire"))
                .thenReturn(Optional.of(compte(ProfilUtilisateur.PRESTATAIRE, prestataire(1L))));

        assertThrows(PrestataireIntrouvableException.class, () -> prestataireService.obtenirFiche(2L, "prestataire"));
    }

    private static Prestataire prestataire(Long id) {
        Prestataire prestataire = new Prestataire();
        prestataire.setId(id);
        prestataire.setNom("Prestataire " + id);
        return prestataire;
    }

    private static Utilisateur compte(ProfilUtilisateur profil, Prestataire prestataire) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setProfil(profil);
        utilisateur.setPrestataire(prestataire);
        return utilisateur;
    }
}