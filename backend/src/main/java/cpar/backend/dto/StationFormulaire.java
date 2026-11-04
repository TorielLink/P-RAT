package cpar.backend.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StationFormulaire(
        @NotBlank(message = "Le nom de la station est obligatoire.")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
        String nom,

        @Min(value = -20, message = "La position X doit être comprise entre -20 et 20.")
        @Max(value = 20, message = "La position X doit être comprise entre -20 et 20.")
        Integer positionX,

        @Min(value = -20, message = "La position Y doit être comprise entre -20 et 20.")
        @Max(value = 20, message = "La position Y doit être comprise entre -20 et 20.")
        Integer positionY) {

        /** Les deux coordonnées ou aucune : même règle que la contrainte checkPositionComplete de la V5. */
        @AssertTrue(message = "Renseignez les deux coordonnées X et Y, ou aucune.")
        public boolean isPositionComplete() {
                return (positionX == null) == (positionY == null);
        }
}