package cpar.backend.exception;

public class EquipementIntrouvableException extends RuntimeException {

    public EquipementIntrouvableException(Long id) {
        super("Aucun équipement ne correspond à l'identifiant " + id + ".");
    }

    public EquipementIntrouvableException(String reference) {
        super("Aucun équipement ne correspond à la référence « " + reference + " ».");
    }
}