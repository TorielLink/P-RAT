package cpar.backend.dto;

import cpar.backend.model.entities.Evaluation;

public record EvaluationDto(Integer note, String commentaire) {

    public static EvaluationDto depuis(Evaluation evaluation) {
        return new EvaluationDto(evaluation.getNote(), evaluation.getCommentaire());
    }
}
