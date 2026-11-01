package cpar.backend.dto;

import cpar.backend.model.enums.PrioriteIncident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModificationIncidentRequest(
        @NotBlank(message = "La description est obligatoire.")
        @Size(min = DeclarationIncidentRequest.DESCRIPTION_MIN,
                max = DeclarationIncidentRequest.DESCRIPTION_MAX,
                message = "La description doit contenir entre {min} et {max} caractères.")
        String description,

        PrioriteIncident priorite) {
}