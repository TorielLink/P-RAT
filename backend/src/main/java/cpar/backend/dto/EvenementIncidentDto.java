package cpar.backend.dto;

import java.time.LocalDateTime;

import cpar.backend.model.enums.TypeEvenementIncident;

public record EvenementIncidentDto(
        LocalDateTime dateEvenement,
        String auteur,
        TypeEvenementIncident type,
        String champ,
        String ancienneValeur,
        String nouvelleValeur) {
}