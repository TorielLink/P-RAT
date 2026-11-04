package cpar.backend.service;

import cpar.backend.dto.*;
import cpar.backend.exception.*;
import cpar.backend.logs.EvenementIncident;
import cpar.backend.model.entities.Evaluation;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Prestataire;
import cpar.backend.model.entities.Utilisateur;
import cpar.backend.model.enums.PrioriteIncident;
import cpar.backend.model.enums.ProfilUtilisateur;
import cpar.backend.model.enums.StatutIncident;
import cpar.backend.model.enums.TypeEvenementIncident;
import cpar.backend.repository.EvaluationRepository;
import cpar.backend.repository.IncidentRepository;
import cpar.backend.repository.PrestataireRepository;
import cpar.backend.repository.UtilisateurRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cpar.backend.logs.JournalIncidentLecteur;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class IncidentService implements IIncidentService {

    private final IncidentRepository incidentRepository;
    private final PrestataireRepository prestataireRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EvaluationRepository evaluationRepository;
    private final IEquipementService equipementService;
    private final ApplicationEventPublisher evenements;
    private final JournalIncidentLecteur journal;
    private final Clock horloge;

    private static final Sort TRI_HISTORIQUE = Sort.by(Sort.Order.desc("dateResolution"), Sort.Order.desc("id"));
    private static final int TAILLE_MAX_PAGE = 100;

    public IncidentService(IncidentRepository incidentRepository,
                           PrestataireRepository prestataireRepository,
                           UtilisateurRepository utilisateurRepository,
                           EvaluationRepository evaluationRepository,
                           IEquipementService equipementService,
                           ApplicationEventPublisher evenements,
                           JournalIncidentLecteur journal,
                           Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.prestataireRepository = prestataireRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.evaluationRepository = evaluationRepository;
        this.equipementService = equipementService;
        this.evenements = evenements;
        this.journal = journal;
        this.horloge = horloge;
    }

    // ---------- Lecture : un prestataire ne voit que les incidents qui lui sont affectés ----------

    @Override
    public List<EvenementIncidentDto> obtenirJournal(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new IncidentIntrouvableException(id);
        }
        return journal.lire(id);
    }

    @Override
    public IncidentDetailDto obtenirDetailIncident(Long id, String login) {
        Utilisateur utilisateur = chargerUtilisateur(login);
        Incident incident = chargerIncidentVisiblePar(id, utilisateur);
        Evaluation evaluation = evaluationRepository.findByIncidentId(id).orElse(null);
        return IncidentDetailDto.depuis(incident, evaluation, LocalDateTime.now(horloge),
                incident.estModifiablePar(utilisateur));
    }

    // 404 plutôt que 403 : on ne révèle pas l'existence des incidents des autres prestataires
    private Incident chargerIncidentVisiblePar(Long id, Utilisateur utilisateur) {
        Incident incident = chargerIncident(id);
        if (utilisateur.estPrestataire() && nEstPasAffecteA(incident, utilisateur)) {
            throw new IncidentIntrouvableException(id);
        }
        return incident;
    }

    @Override
    public List<Incident> obtenirTousIncidents(String login) {
        Utilisateur utilisateur = chargerUtilisateur(login);
        if (utilisateur.estPrestataire()) {
            Long prestataireId = utilisateur.prestataireRattacheId();
            return prestataireId == null
                    ? List.of()
                    : incidentRepository.findByPrestataireIdOrderByDateOuvertureDescIdDesc(prestataireId);
        }
        return incidentRepository.findAllByOrderByDateOuvertureDescIdDesc();
    }

    @Override
    public List<Incident> obtenirIncidentsParEquipement(Long equipementId, String login) {
        equipementService.obtenirEquipementParId(equipementId);
        Utilisateur utilisateur = chargerUtilisateur(login);
        if (utilisateur.estPrestataire()) {
            Long prestataireId = utilisateur.prestataireRattacheId();
            return prestataireId == null
                    ? List.of()
                    : incidentRepository.findByEquipementIdAndPrestataireIdOrderByDateOuvertureDescIdDesc(equipementId, prestataireId);
        }
        return incidentRepository.findByEquipementIdOrderByDateOuvertureDescIdDesc(equipementId);
    }

    @Override
    public List<Incident> obtenirIncidentsParStatut(StatutIncident statut, String login) {
        Utilisateur utilisateur = chargerUtilisateur(login);
        if (utilisateur.estPrestataire()) {
            Long prestataireId = utilisateur.prestataireRattacheId();
            return prestataireId == null
                    ? List.of()
                    : incidentRepository.findByStatutAndPrestataireIdOrderByDateOuvertureDescIdDesc(statut, prestataireId);
        }
        return incidentRepository.findByStatutOrderByDateOuvertureDescIdDesc(statut);
    }

    @Override
    public PageDto<HistoriqueIncidentDto> obtenirHistorique(String login, int page, int taille) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.clamp(taille, 1, TAILLE_MAX_PAGE),
                TRI_HISTORIQUE);
        Utilisateur utilisateur = chargerUtilisateur(login);

        Page<Incident> resolus;
        if (utilisateur.estPrestataire()) {
            Long prestataireId = utilisateur.prestataireRattacheId();
            resolus = prestataireId == null
                    ? Page.empty(pageable)
                    : incidentRepository.findByStatutAndPrestataireId(StatutIncident.RESOLU, prestataireId, pageable);
        } else {
            resolus = incidentRepository.findByStatut(StatutIncident.RESOLU, pageable);
        }

        List<Long> idsResolus = resolus.getContent().stream().map(Incident::getId).toList();
        Map<Long, Integer> notes = evaluationRepository.findByIncidentIdIn(idsResolus).stream()
                .filter(evaluation -> evaluation.getNote() != null)
                .collect(Collectors.toMap(evaluation -> evaluation.getIncident().getId(), Evaluation::getNote));

        return PageDto.depuis(resolus, resolus.getContent().stream()
                .map(incident -> HistoriqueIncidentDto.depuis(incident, notes.get(incident.getId())))
                .toList());
    }

    // ---------- Écriture ----------

    /** Rattaché à un équipement, ouvert à l'instant du serveur, sans prestataire ni résolution. */
    @Override
    @Transactional
    public Incident declarerIncident(Long equipementId, DeclarationIncidentRequest demande, String login) {
        Incident incident = new Incident();
        incident.setEquipement(equipementService.obtenirEquipementParId(equipementId));
        incident.setDescription(demande.description().trim());
        incident.setPriorite(demande.priorite() != null ? demande.priorite() : PrioriteIncident.MOYENNE);
        incident.setStatut(StatutIncident.OUVERT);
        incident.setDateOuverture(LocalDateTime.now(horloge));
        incident.setUtilisateur(chargerUtilisateur(login));
        Incident enregistre = incidentRepository.save(incident);
        publier(enregistre.getId(), login, TypeEvenementIncident.DECLARATION, null, null, null);
        return enregistre;
    }

    /** Admin : tous les incidents. Agent : seulement ceux qui lui sont affectés. Prestataire : refusé par SecurityConfig. */
    @Override
    @Transactional
    public Incident modifierIncident(Long id, ModificationIncidentRequest demande, String login) {
        Incident incident = obtenirIncidentNonResolu(id);
        if (!incident.estModifiablePar(chargerUtilisateur(login))) {
            throw new OperationInterditeException(
                    "Vous ne pouvez modifier que les incidents non affectés que vous avez déclarés.");
        }
        String ancienneDescription = incident.getDescription();
        PrioriteIncident anciennePriorite = incident.getPriorite();

        incident.setDescription(demande.description().trim());
        if (demande.priorite() != null) {
            incident.setPriorite(demande.priorite());
        }

        // Une ligne de journal par champ réellement modifié
        if (!Objects.equals(ancienneDescription, incident.getDescription())) {
            publier(id, login, TypeEvenementIncident.MODIFICATION, "description",
                    ancienneDescription, incident.getDescription());
        }
        if (anciennePriorite != incident.getPriorite()) {
            publier(id, login, TypeEvenementIncident.MODIFICATION, "priorite",
                    nomOuNull(anciennePriorite), nomOuNull(incident.getPriorite()));
        }
        return incident;
    }

    /** Admin seulement (SecurityConfig). Affecter ou réaffecter : l'incident passe EN_COURS. */
    @Override
    @Transactional
    public Incident affecterPrestataire(Long id, Long prestataireId, String login) {
        Incident incident = obtenirIncidentNonResolu(id);
        Prestataire prestataire = prestataireRepository.findById(prestataireId)
                .orElseThrow(() -> new PrestataireIntrouvableException(prestataireId));
        Prestataire ancien = incident.getPrestataire();
        // Même prestataire : rien ne change, rien à journaliser
        if (ancien != null && Objects.equals(ancien.getId(), prestataire.getId())) {
            return incident;
        }
        incident.affecterA(prestataire);
        publier(id, login,
                ancien == null ? TypeEvenementIncident.AFFECTATION : TypeEvenementIncident.REAFFECTATION,
                "prestataire", ancien == null ? null : ancien.getNom(), prestataire.getNom());
        return incident;
    }

    /** Admin seulement (SecurityConfig). Supprime aussi l'évaluation (ON DELETE CASCADE en base). */
    @Override
    @Transactional
    public void supprimerIncident(Long id) {
        incidentRepository.delete(chargerIncident(id));
    }

    // ---------- Outils ----------

    private Incident chargerIncident(Long id) {
        return incidentRepository.findWithDetailsById(id)
                .orElseThrow(() -> new IncidentIntrouvableException(id));
    }

    // Un incident résolu est figé : toute modification est refusée
    private Incident obtenirIncidentNonResolu(Long id) {
        Incident incident = chargerIncident(id);
        if (incident.getStatut() == StatutIncident.RESOLU) {
            throw new IncidentDejaResoluException(id);
        }
        return incident;
    }

    private Utilisateur chargerUtilisateur(String login) {
        return utilisateurRepository.findByLogin(login)
                .orElseThrow(() -> new UtilisateurIntrouvableException(login));
    }

    // Écrit dans le fichier seulement après le commit (JournalIncidentListener)
    private void publier(Long incidentId, String login, TypeEvenementIncident type,
                         String champ, String ancienne, String nouvelle) {
        evenements.publishEvent(new EvenementIncident(incidentId, login,
                LocalDateTime.now(horloge), type, champ, ancienne, nouvelle));
    }

    private static String nomOuNull(Enum<?> valeur) {
        return valeur == null ? null : valeur.name();
    }

    @Override
    @Transactional
    public Incident resoudreIncident(Long id, String login) {
        Incident incident = obtenirIncidentNonResolu(id); // 404 puis 409
        Utilisateur utilisateur = chargerUtilisateur(login);
        if (utilisateur.estPrestataire() && nEstPasAffecteA(incident, utilisateur)) {
            throw new OperationInterditeException("Cet incident n'est pas affecté à votre entreprise.");
        }
        StatutIncident ancienStatut = incident.getStatut();
        incident.setStatut(StatutIncident.RESOLU);
        incident.setDateResolution(LocalDateTime.now(horloge));
        publier(id, login, TypeEvenementIncident.RESOLUTION, "statut",
                nomOuNull(ancienStatut), StatutIncident.RESOLU.name());
        return incident;
    }

    @Override
    public List<IncidentActifDto> obtenirIncidentsActifs(String login) {
        Utilisateur utilisateur = chargerUtilisateur(login);
        List<Incident> actifs;
        if (utilisateur.estPrestataire()) {
            Long prestataireId = utilisateur.prestataireRattacheId();
            actifs = prestataireId == null
                    ? List.of()
                    : incidentRepository.findByStatutNotAndPrestataireIdOrderByDateOuvertureAscIdAsc(
                    StatutIncident.RESOLU, prestataireId);
        } else {
            actifs = incidentRepository.findByStatutNotOrderByDateOuvertureAscIdAsc(StatutIncident.RESOLU);
        }
        LocalDateTime maintenant = LocalDateTime.now(horloge);
        return actifs.stream().map(incident -> IncidentActifDto.depuis(incident, maintenant)).toList();
    }

    // Comparaison par identifiant : évite de charger le prestataire lié au compte
    private static boolean nEstPasAffecteA(Incident incident, Utilisateur utilisateur) {
        Long prestataireCompte = utilisateur.prestataireRattacheId();
        return prestataireCompte == null
                || incident.getPrestataire() == null
                || !Objects.equals(incident.getPrestataire().getId(), prestataireCompte);
    }

    // ------ Évaluation prestataire --------
    /** admin seulement -> incident résolu, affecté, et une seule évaluation */
    @Override
    @Transactional
    public Evaluation evaluerIncident(Long id, EvaluationRequest demande, String login) {
        Incident incident = chargerIncident(id);
        incident.verifierQuIlPeutEtreEvalue();
        if (evaluationRepository.existsByIncidentId(id)) {
            throw EvaluationImpossibleException.dejaEvalue(id);
        }
        Evaluation evaluation = new Evaluation();
        evaluation.setIncident(incident);
        evaluation.setNote(demande.note());
        evaluation.setCommentaire(commentaireOuNull(demande.commentaire()));
        evaluation.setDateEvaluation(LocalDate.now(horloge));
        try {
            Evaluation enregistree = evaluationRepository.saveAndFlush(evaluation);
            publier(id, login, TypeEvenementIncident.EVALUATION, "note",
                    null, demande.note() == null ? null : demande.note().toString());
            return enregistree;
        } catch (DataIntegrityViolationException doublon) {
            throw EvaluationImpossibleException.dejaEvalue(id);
        }
    }

    // "" ou "   " = pas de commentaire
    private static String commentaireOuNull(String commentaire) {
        return commentaire == null || commentaire.isBlank() ? null : commentaire.trim();
    }
}