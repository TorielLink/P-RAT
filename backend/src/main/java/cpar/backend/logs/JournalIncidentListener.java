package cpar.backend.logs;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class JournalIncidentListener {

    static final String DOSSIER = "backend/src/main/java/cpar/backend/logs/files/incidents/";

    private final GeneralLogger generalLogger;

    public JournalIncidentListener(GeneralLogger generalLogger) {
        this.generalLogger = generalLogger;
    }

    public static String fichierDe(Long incidentId) {
        return DOSSIER + "incident-" + incidentId + ".txt";
    }

    // Appelé seulement si la transaction de l'action métier a réussi
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void journaliser(EvenementIncident evenement) {
        generalLogger.ecrire(fichierDe(evenement.incidentId()), evenement.versLigne());
    }
}