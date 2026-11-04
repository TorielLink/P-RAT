package cpar.backend.service;

import cpar.backend.dto.EvaluationPrestataireDto;
import cpar.backend.dto.PrestataireFicheDto;
import cpar.backend.dto.PrestataireResumeDto;
import cpar.backend.dto.StatistiquesPrestataire;
import cpar.backend.exception.PrestataireIntrouvableException;
import cpar.backend.exception.UtilisateurIntrouvableException;
import cpar.backend.model.entities.Prestataire;
import cpar.backend.model.entities.Utilisateur;
import cpar.backend.repository.EvaluationRepository;
import cpar.backend.repository.PrestataireRepository;
import cpar.backend.repository.UtilisateurRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PrestataireService implements IPrestataireService {

    private final PrestataireRepository prestataireRepository;
    private final EvaluationRepository evaluationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public PrestataireService(PrestataireRepository prestataireRepository,
                              EvaluationRepository evaluationRepository,
                              UtilisateurRepository utilisateurRepository) {
        this.prestataireRepository = prestataireRepository;
        this.evaluationRepository = evaluationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public List<PrestataireResumeDto> obtenirPrestataires(String login) {
        Map<Long, StatistiquesPrestataire> statistiques = statistiquesParPrestataire();
        return prestatairesVisiblesPar(chargerUtilisateur(login)).stream()
                .map(prestataire -> PrestataireResumeDto.depuis(prestataire, statistiques.get(prestataire.getId())))
                .toList();
    }

    @Override
    public PrestataireFicheDto obtenirFiche(Long id, String login) {
        Utilisateur utilisateur = chargerUtilisateur(login);
        // 404 plutôt que 403 : un prestataire ne découvre pas les fiches des autres
        if (utilisateur.estPrestataire() && !Objects.equals(utilisateur.prestataireRattacheId(), id)) {
            throw new PrestataireIntrouvableException(id);
        }
        Prestataire prestataire = prestataireRepository.findById(id)
                .orElseThrow(() -> new PrestataireIntrouvableException(id));
        List<EvaluationPrestataireDto> evaluations = evaluationRepository
                .findByIncidentPrestataireIdOrderByDateEvaluationDescIdDesc(id).stream()
                .map(EvaluationPrestataireDto::depuis)
                .toList();
        return new PrestataireFicheDto(
                PrestataireResumeDto.depuis(prestataire, statistiquesParPrestataire().get(id)), evaluations);
    }

    private List<Prestataire> prestatairesVisiblesPar(Utilisateur utilisateur) {
        if (!utilisateur.estPrestataire()) {
            return prestataireRepository.findAll(Sort.by("nom", "id"));
        }
        Long id = utilisateur.prestataireRattacheId();
        return id == null ? List.of() : prestataireRepository.findById(id).map(List::of).orElse(List.of());
    }

    private Map<Long, StatistiquesPrestataire> statistiquesParPrestataire() {
        return evaluationRepository.calculerStatistiquesParPrestataire().stream()
                .collect(Collectors.toMap(StatistiquesPrestataire::prestataireId, Function.identity()));
    }

    private Utilisateur chargerUtilisateur(String login) {
        return utilisateurRepository.findByLogin(login)
                .orElseThrow(() -> new UtilisateurIntrouvableException(login));
    }
}