package cpar.backend.logs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GeneralLogger {
    private static final Logger log = LoggerFactory.getLogger(GeneralLogger.class);

    public synchronized void ecrire(String cheminFichier, String message) {
        Path fichier = Path.of(cheminFichier);
        try {
            Path dossier = fichier.getParent();
            if (dossier != null) {
                Files.createDirectories(dossier);
            }
            Files.writeString(fichier, message + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.warn("Impossible d'écrire dans {}", fichier.toAbsolutePath(), e);
        }
    }
}