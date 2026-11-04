package cpar.backend.dto;

import cpar.backend.model.entities.Evaluation;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Station;

import java.time.LocalDate;

public record EvaluationPrestataireDto(
        Long incidentId,
        String equipementReference,
        String stationNom,
        Integer note,
        String commentaire,
        LocalDate dateEvaluation) {

    public static EvaluationPrestataireDto depuis(Evaluation evaluation) {
        Incident incident = evaluation.getIncident();
        Station station = incident.getEquipement().getStation();
        return new EvaluationPrestataireDto(
                incident.getId(),
                incident.getEquipement().getReference(),
                station != null ? station.getNom() : null,
                evaluation.getNote(),
                evaluation.getCommentaire(),
                evaluation.getDateEvaluation());
    }
}