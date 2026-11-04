package cpar.backend.dto;

import cpar.backend.model.entities.Evaluation;

import java.time.LocalDate;

public record EvaluationDto(Integer note, String commentaire, LocalDate dateEvaluation) {

    public static EvaluationDto depuis(Evaluation evaluation) {
        return new EvaluationDto(evaluation.getNote(), evaluation.getCommentaire(), evaluation.getDateEvaluation());
    }
}