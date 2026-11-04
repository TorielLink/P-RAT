package cpar.backend.dto;

import cpar.backend.model.enums.EtatsEquipement;
import jakarta.validation.constraints.NotNull;

public record EtatEquipementFormulaire(
        @NotNull(message = "L'état est obligatoire.")
        EtatsEquipement etat) {
}