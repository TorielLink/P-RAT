package cpar.backend.dto;

import cpar.backend.model.entities.Equipement;
import cpar.backend.model.entities.Station;
import cpar.backend.model.enums.EtatsEquipement;
import cpar.backend.model.enums.TypeEquipement;

import java.time.LocalDateTime;

public record EquipementDto(
        Long id,
        String reference,
        String description,
        TypeEquipement type,
        EtatsEquipement etat,
        Long stationId,
        String stationNom,
        LocalDateTime dateCreation) {

    public static EquipementDto depuis(Equipement equipement) {
        Station station = equipement.getStation();
        return new EquipementDto(
                equipement.getId(),
                equipement.getReference(),
                equipement.getDescription(),
                equipement.getType(),
                equipement.getEtat(),
                station != null ? station.getId() : null,
                station != null ? station.getNom() : null,
                equipement.getDateCreation());
    }
}