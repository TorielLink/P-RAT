package cpar.backend.dto;

import cpar.backend.model.entities.Equipement;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Station;

import java.time.LocalDateTime;

/** Une ligne de l'historique */
public record HistoriqueIncidentDto(
        Long id,
        Long equipementId,
        String equipementReference,
        Long stationId,
        String stationNom,
        String description,
        LocalDateTime dateOuverture,
        LocalDateTime dateResolution,
        long dureeTraitementMinutes,
        String prestataireNom,
        Integer note) {

    public static HistoriqueIncidentDto depuis(Incident incident, Integer note) {
        Equipement equipement = incident.getEquipement();
        Station station = equipement.getStation();
        return new HistoriqueIncidentDto(
                incident.getId(),
                equipement.getId(),
                equipement.getReference(),
                station != null ? station.getId() : null,
                station != null ? station.getNom() : null,
                incident.getDescription(),
                incident.getDateOuverture(),
                incident.getDateResolution(),
                incident.dureeOuverture(incident.getDateResolution()).toMinutes(),
                incident.getPrestataire() != null ? incident.getPrestataire().getNom() : null,
                note);
    }
}