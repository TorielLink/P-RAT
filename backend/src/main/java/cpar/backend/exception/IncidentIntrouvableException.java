package cpar.backend.exception;

public class IncidentIntrouvableException extends RuntimeException {
    public IncidentIntrouvableException(Long id) {
        super("Aucun incident ne correspond à l'identifiant " + id + ".");
    }
}