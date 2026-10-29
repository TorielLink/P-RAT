package cpar.backend.exception;

public class ReferenceDejaUtiliseeException extends RuntimeException {

    public ReferenceDejaUtiliseeException(String reference) {
        super("La référence « " + reference + " » est déjà utilisée par un autre équipement.");
    }
}