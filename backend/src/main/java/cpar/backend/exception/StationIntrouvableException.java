package cpar.backend.exception;

public class StationIntrouvableException extends RuntimeException {

    public StationIntrouvableException(Long id) {
        super("Aucune station ne correspond à l'identifiant " + id + ".");
    }

    public StationIntrouvableException(String nom) {
        super("Aucune station ne correspond au nom « " + nom + " ».");
    }
}