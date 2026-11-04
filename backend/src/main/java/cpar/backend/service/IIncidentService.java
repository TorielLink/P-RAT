package cpar.backend.service;

import cpar.backend.dto.*;
import cpar.backend.model.entities.Evaluation;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.enums.StatutIncident;

import java.util.List;

/** login : login de l'utilisateur connecté, fourni par le contrôleur depuis la session. */
public interface IIncidentService {

    IncidentDetailDto obtenirDetailIncident(Long id, String login);

    Incident obtenirIncidentParId(Long id, String login);

    List<Incident> obtenirTousIncidents(String login);

    List<Incident> obtenirIncidentsParEquipement(Long equipementId, String login);

    List<Incident> obtenirIncidentsParStatut(StatutIncident statut, String login);

    Incident declarerIncident(Long equipementId, DeclarationIncidentRequest demande, String login);

    Incident modifierIncident(Long id, ModificationIncidentRequest demande, String login);

    Incident affecterPrestataire(Long id, Long prestataireId);

    void supprimerIncident(Long id);

    Incident resoudreIncident(Long id, String login);

    PageDto<HistoriqueIncidentDto> obtenirHistorique(String login, int page, int taille);

    List<IncidentActifDto> obtenirIncidentsActifs(String login);

    Evaluation evaluerIncident(Long id, EvaluationRequest demande);
}