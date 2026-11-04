package cpar.backend.logs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cpar.backend.dto.EvenementIncidentDto;
import cpar.backend.model.enums.TypeEvenementIncident;

@Component
public class JournalIncidentLecteur {
    private static final Logger log = LoggerFactory.getLogger(JournalIncidentLecteur.class);
    private static final int NB_COLONNES = 6;

    // Du plus récent au plus ancien
    public List<EvenementIncidentDto> lire(Long incidentId) {
        Path fichier = Path.of(JournalIncidentListener.fichierDe(incidentId));
        if (!Files.exists(fichier)) {
            return List.of();
        }
        List<String> lignes;
        try {
            lignes = Files.readAllLines(fichier, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Impossible de lire {}", fichier.toAbsolutePath(), e);
            return List.of();
        }
        List<EvenementIncidentDto> evenements = new ArrayList<>();
        for (String ligne : lignes) {
            EvenementIncidentDto evenement = analyser(ligne);
            if (evenement != null) {
                evenements.add(evenement);
            }
        }
        Collections.reverse(evenements);
        return evenements;
    }

    // date|auteur|type|champ|ancienne|nouvelle ; une ligne abîmée est ignorée
    private static EvenementIncidentDto analyser(String ligne) {
        String[] parties = ligne.split(Pattern.quote(EvenementIncident.SEPARATEUR), -1);
        if (parties.length != NB_COLONNES) {
            log.warn("Ligne de journal ignorée : {}", ligne);
            return null;
        }
        try {
            return new EvenementIncidentDto(
                    LocalDateTime.parse(parties[0]),
                    videEnNull(parties[1]),
                    TypeEvenementIncident.valueOf(parties[2]),
                    videEnNull(parties[3]),
                    videEnNull(parties[4]),
                    videEnNull(parties[5]));
        } catch (DateTimeParseException | IllegalArgumentException e) {
            log.warn("Ligne de journal ignorée : {}", ligne);
            return null;
        }
    }

    private static String videEnNull(String valeur) {
        return valeur.isEmpty() ? null : valeur;
    }
}