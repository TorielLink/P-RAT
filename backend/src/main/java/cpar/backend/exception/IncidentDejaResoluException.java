package cpar.backend.exception;

public class IncidentDejaResoluException extends RuntimeException {
    public IncidentDejaResoluException(Long id) {
        super("L'incident " + id + " est déjà résolu : il ne peut plus être modifié, affecté ni réaffecté.");
    }
}