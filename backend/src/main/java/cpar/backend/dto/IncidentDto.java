package cpar.backend.dto;

import cpar.backend.model.entities.Equipement;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Prestataire;
import cpar.backend.model.entities.Station;
import cpar.backend.model.entities.Utilisateur;
import cpar.backend.model.enums.PrioriteIncident;
import cpar.backend.model.enums.StatutIncident;

import java.time.LocalDateTime;

public record IncidentDto(
        Long id,
        Long equipementId,
        String equipementReference,
        Long stationId,
        String stationNom,
        String description,
        StatutIncident statut,
        PrioriteIncident priorite,
        Long prestataireId,
        String prestataireNom,
        String declarantPrenom,
        LocalDateTime dateOuverture,
        LocalDateTime dateResolution) {

    public static IncidentDto depuis(Incident incident) {
        Equipement equipement = incident.getEquipement();
        Station station = equipement.getStation();
        Prestataire prestataire = incident.getPrestataire();
        Utilisateur declarant = incident.getUtilisateur();
        return new IncidentDto(
                incident.getId(),
                equipement.getId(),
                equipement.getReference(),
                station != null ? station.getId() : null,
                station != null ? station.getNom() : null,
                incident.getDescription(),
                incident.getStatut(),
                incident.getPriorite(),
                prestataire != null ? prestataire.getId() : null,
                prestataire != null ? prestataire.getNom() : null,
                declarant != null ? declarant.getPrenom() : null,
                incident.getDateOuverture(),
                incident.getDateResolution());
    }
}