package cpar.backend.exception;

public class EvaluationImpossibleException extends RuntimeException {

    private EvaluationImpossibleException(String message) {
        super(message);
    }

    public static EvaluationImpossibleException incidentNonResolu(Long id) {
        return new EvaluationImpossibleException(
                "L'incident " + id + " n'est pas résolu : il ne peut pas encore être évalué.");
    }

    public static EvaluationImpossibleException sansPrestataire(Long id) {
        return new EvaluationImpossibleException(
                "L'incident " + id + " n'a été affecté à aucun prestataire : il n'y a pas d'intervention à évaluer.");
    }

    public static EvaluationImpossibleException dejaEvalue(Long id) {
        return new EvaluationImpossibleException(
                "L'incident " + id + " a déjà été évalué : une seule évaluation est possible par incident.");
    }
}