package cpar.backend.exception;

public class PrestataireIntrouvableException extends RuntimeException {
    public PrestataireIntrouvableException(Long id) {
        super("Aucun prestataire ne correspond à l'identifiant " + id + ".");
    }
}