package cpar.backend.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

public record PrestataireFicheDto(
        @JsonUnwrapped PrestataireResumeDto prestataire,
        List<EvaluationPrestataireDto> evaluations) {
}