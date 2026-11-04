package cpar.backend.dto;

/** Une station sur la carte : position, équipements et incidents actifs par niveau (calculés à la requête). */
public record StationCarteDto(
        Long id,
        String nom,
        Integer positionX,
        Integer positionY,
        long nombreEquipements,
        /** Pire niveau parmi les incidents actifs : ROUGE, ORANGE, VERT, PRIS_EN_CHARGE ou AUCUN. */
        String situation,
        long rouge,
        long orange,
        long vert,
        long prisEnCharge) {
}