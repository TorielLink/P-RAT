package cpar.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EvaluationRequest(
        @NotNull(message = "La note est obligatoire.")
        @Min(value = EvaluationRequest.NOTE_MIN, message = "La note doit être un entier de 1 à 5.")
        @Max(value = EvaluationRequest.NOTE_MAX, message = "La note doit être un entier de 1 à 5.")
        Integer note,

        @Size(max = EvaluationRequest.COMMENTAIRE_MAX,
                message = "Le commentaire ne doit pas dépasser {max} caractères.")
        String commentaire) {

    public static final int NOTE_MIN = 1;
    public static final int NOTE_MAX = 5;
    public static final int COMMENTAIRE_MAX = 500;
}