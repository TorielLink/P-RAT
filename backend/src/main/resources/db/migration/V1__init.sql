CREATE TABLE IF NOT EXISTS station (
                         id  BIGINT       NOT NULL AUTO_INCREMENT,
                         nom VARCHAR(100) NOT NULL,
                         PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS prestataire (
                             id        BIGINT       NOT NULL AUTO_INCREMENT,
                             nom       VARCHAR(100) NOT NULL,
                             email     VARCHAR(255) NULL,
                             telephone VARCHAR(20)  NULL,
                             PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS utilisateur (
                             id         BIGINT       NOT NULL AUTO_INCREMENT,
                             login      VARCHAR(50)  NOT NULL,
                             motDePasse VARCHAR(255) NOT NULL,
                             prenom     VARCHAR(100) NULL,
                             profil     VARCHAR(20)  NULL,
                             actif      BOOLEAN      NOT NULL DEFAULT TRUE,
                             PRIMARY KEY (id),
                             CONSTRAINT logUnique UNIQUE (login),
                             CONSTRAINT checkProfil CHECK (profil IN ('ADMIN', 'AGENT', 'PRESTATAIRE'))
);

CREATE TABLE IF NOT EXISTS equipement (
                            id           BIGINT       NOT NULL AUTO_INCREMENT,
                            reference    VARCHAR(8)   NULL,
                            description  VARCHAR(255) NULL,
                            type         VARCHAR(30)  NOT NULL,
                            etat         VARCHAR(30)  NOT NULL DEFAULT 'EN_SERVICE',
                            stationId    BIGINT       NULL,
                            dateCreation DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (id),
                            CONSTRAINT refUnique UNIQUE (reference),
                            CONSTRAINT checkType CHECK (type IN ('CAMERA', 'ASCENSEUR', 'PORTE', 'ESCALATOR')),
                            CONSTRAINT checkEtat CHECK (etat IN ('EN_SERVICE', 'EN_PANNE', 'EN_MAINTENANCE', 'HORS_SERVICE')),
                            CONSTRAINT cleEtrangereStation FOREIGN KEY (stationId)
                                REFERENCES station (id) ON DELETE CASCADE,
                            INDEX equipementStationId (stationId)
);

CREATE TABLE IF NOT EXISTS incident (
                          id             BIGINT        NOT NULL AUTO_INCREMENT,
                          equipementId   BIGINT        NOT NULL,
                          description    VARCHAR(2000) NULL,
                          dateOuverture  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          statut         VARCHAR(20)   NOT NULL DEFAULT 'OUVERT',
                          priorite       VARCHAR(20)   NULL DEFAULT 'MOYENNE',
                          prestataireId  BIGINT        NULL,
                          utilisateurId  BIGINT        NULL,
                          dateResolution DATETIME      NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT checkStatu CHECK (statut IN ('OUVERT', 'EN_COURS', 'RESOLU')),
                          CONSTRAINT checkPrio CHECK (priorite IN ('BASSE', 'MOYENNE', 'HAUTE', 'CRITIQUE')),
                          CONSTRAINT checkDate CHECK (dateResolution IS NULL OR dateResolution >= dateOuverture),
                          CONSTRAINT cleEtrangereEquipement FOREIGN KEY (equipementId)
                              REFERENCES equipement (id) ON DELETE CASCADE,
                          CONSTRAINT cleEtrangerePrestataire FOREIGN KEY (prestataireId)
                              REFERENCES prestataire (id) ON DELETE SET NULL,
                          CONSTRAINT cleEtrangereUtilisateur FOREIGN KEY (utilisateurId)
                              REFERENCES utilisateur (id) ON DELETE SET NULL,
                          INDEX incidentEquipementId (equipementId),
                          INDEX incidentPrestataireId (prestataireId),
                          INDEX incidentUtilisateurId (utilisateurId)
);

CREATE TABLE IF NOT EXISTS evaluation (
                            id             BIGINT        NOT NULL AUTO_INCREMENT,
                            incidentId     BIGINT        NOT NULL,
                            note           INT           NULL,
                            commentaire    VARCHAR(1000) NULL,
                            dateEvaluation DATE          NOT NULL DEFAULT (CURRENT_DATE),
                            PRIMARY KEY (id),
                            CONSTRAINT cleIncidentUnique UNIQUE (incidentId),
                            CONSTRAINT checkNote CHECK (note BETWEEN 1 AND 5),
                            CONSTRAINT cleEtrangereIncident FOREIGN KEY (incidentId)
                                REFERENCES incident (id) ON DELETE CASCADE
);