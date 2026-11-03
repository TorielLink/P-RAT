package cpar.backend.dto;

import cpar.backend.model.entities.Equipement;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Prestataire;
import cpar.backend.model.entities.Station;
import cpar.backend.model.enums.NiveauAttentionIncident;
import cpar.backend.model.enums.TypeEquipement;

import java.time.LocalDateTime;

/** Une ligne du tableau de bord */
public record IncidentActifDto(
        Long id,
        NiveauAttentionIncident niveauAttention,
        Long stationId,
        String stationNom,
        Long equipementId,
        String equipementReference,
        String equipementDescription,
        TypeEquipement equipementType,
        String description,
        long dureeOuvertureMinutes,
        Long prestataireId,
        String prestataireNom) {

    public static IncidentActifDto depuis(Incident incident, LocalDateTime maintenant) {
        Equipement equipement = incident.getEquipement();
        Station station = equipement.getStation();
        Prestataire prestataire = incident.getPrestataire();
        return new IncidentActifDto(
                incident.getId(),
                NiveauAttentionIncident.de(incident, maintenant),
                station != null ? station.getId() : null,
                station != null ? station.getNom() : null,
                equipement.getId(),
                equipement.getReference(),
                equipement.getDescription(),
                equipement.getType(),
                incident.getDescription(),
                incident.dureeOuverture(maintenant).toMinutes(),
                prestataire != null ? prestataire.getId() : null,
                prestataire != null ? prestataire.getNom() : null);
    }
}