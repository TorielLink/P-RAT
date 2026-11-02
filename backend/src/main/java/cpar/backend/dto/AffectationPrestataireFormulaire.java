package cpar.backend.dto;

import jakarta.validation.constraints.NotNull;

public record AffectationPrestataireFormulaire(
        @NotNull(message = "Le prestataire est obligatoire.")
        Long prestataireId) {
}