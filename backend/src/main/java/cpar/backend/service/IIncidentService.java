package cpar.backend.service;

import cpar.backend.dto.DeclarationIncidentFormulaire;
import cpar.backend.dto.ModificationIncidentFormulaire;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.enums.StatutIncident;

import java.util.List;

/** login : login de l'utilisateur connecté, fourni par le contrôleur depuis la session. */
public interface IIncidentService {

    Incident obtenirIncidentParId(Long id, String login);

    List<Incident> obtenirTousIncidents(String login);

    List<Incident> obtenirIncidentsParEquipement(Long equipementId, String login);

    List<Incident> obtenirIncidentsParStatut(StatutIncident statut, String login);

    Incident declarerIncident(DeclarationIncidentFormulaire formulaire, String login);

    Incident modifierIncident(Long id, ModificationIncidentFormulaire formulaire, String login);

    Incident affecterPrestataire(Long id, Long prestataireId);

    Incident resoudreIncident(Long id);

    void supprimerIncident(Long id);
}