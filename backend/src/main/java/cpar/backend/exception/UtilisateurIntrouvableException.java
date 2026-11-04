package cpar.backend.exception;

public class UtilisateurIntrouvableException extends RuntimeException {
    public UtilisateurIntrouvableException(String login) {
        super("Aucun utilisateur ne correspond à l'identifiant « " + login + " ».");
    }
}
