package cpar.backend.logs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ProfileLogger implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(ProfileLogger.class);
    private final Environment env;
    private final GeneralLogger generalLogger;

    public ProfileLogger(Environment env, GeneralLogger generalLogger) {
        this.env = env;
        this.generalLogger = generalLogger;
    }

    @Override
    public void run(ApplicationArguments args) {
        String profils = Arrays.toString(env.getActiveProfiles());
        log.info("Profils actifs : {}", profils);
        generalLogger.ecrire("logs/initialisations.txt", "Profils actifs : " + profils);
    }
}