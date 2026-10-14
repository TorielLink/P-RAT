CREATE TABLE IF NOT EXISTS equipement (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference     VARCHAR(50)  NOT NULL UNIQUE,
    libelle       VARCHAR(100) NOT NULL,
    description   TEXT,
    type          VARCHAR(50)  NOT NULL,
    station       VARCHAR(100) NOT NULL,
    etat          ENUM('ACTIF', 'INACTIF', 'EN_PANNE') NOT NULL DEFAULT 'ACTIF',
    date_creation TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO equipement (reference, libelle, description, type, station, etat) VALUES
('EQ-001', 'Routeur principal', 'Routeur cœur de réseau', 'RESEAU', 'Station Centrale', 'ACTIF'),
('EQ-002', 'Caméra hall', 'Caméra de surveillance hall d''entrée', 'VIDEO', 'Station Nord', 'INACTIF'),
('EQ-003', 'Capteur température', 'Sonde température salle serveurs', 'CAPTEUR', 'Station Centrale', 'EN_PANNE');
