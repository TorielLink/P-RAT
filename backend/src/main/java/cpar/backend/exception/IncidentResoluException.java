package cpar.backend.exception;

public class IncidentResoluException extends RuntimeException {
    public IncidentResoluException(Long id) {
        super("L'incident " + id + " est résolu : il ne peut plus être modifié.");
    }
}