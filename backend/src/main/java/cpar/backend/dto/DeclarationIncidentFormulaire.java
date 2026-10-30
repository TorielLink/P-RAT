package cpar.backend.dto;

import cpar.backend.model.enums.PrioriteIncident;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeclarationIncidentFormulaire(
        @NotNull(message = "L'équipement concerné est obligatoire.")
        Long equipementId,

        @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères.")
        String description,

        PrioriteIncident priorite) {
}