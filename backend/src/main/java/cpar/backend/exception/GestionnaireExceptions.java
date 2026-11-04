package cpar.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GestionnaireExceptions {
    @ExceptionHandler({StationIntrouvableException.class, EquipementIntrouvableException.class,
            IncidentIntrouvableException.class, PrestataireIntrouvableException.class,
            UtilisateurIntrouvableException.class})
    public ProblemDetail introuvable(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({ReferenceDejaUtiliseeException.class, IncidentDejaResoluException.class,
            EvaluationImpossibleException.class, PositionOccupeeException.class})
    public ProblemDetail conflit(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail donneesInvalides(MethodArgumentNotValidException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Certaines données saisies sont invalides.");
        Map<String, String> erreurs = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(erreur -> erreurs.putIfAbsent(erreur.getField(), erreur.getDefaultMessage()));
        probleme.setProperty("erreurs", erreurs);
        return probleme;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail requeteIllisible() {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "La requête est mal formée (valeur inconnue ou format incorrect).");
    }

    @ExceptionHandler(OperationInterditeException.class)
    public ProblemDetail interdit(OperationInterditeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }

}