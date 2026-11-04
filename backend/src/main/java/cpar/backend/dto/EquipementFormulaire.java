package cpar.backend.dto;

import cpar.backend.model.enums.EtatsEquipement;
import cpar.backend.model.enums.TypeEquipement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EquipementFormulaire(
        @Pattern(regexp = "^[A-Z0-9]{0,8}$",
                message = "La référence contient au plus 8 lettres majuscules ou chiffres (ex. CAM00001).")
        String reference,

        @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères.")
        String description,

        @NotNull(message = "Le type est obligatoire.")
        TypeEquipement type,

        EtatsEquipement etat,

        Long stationId) {
}