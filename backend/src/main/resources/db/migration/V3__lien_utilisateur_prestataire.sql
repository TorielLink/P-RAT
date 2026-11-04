-- Rattache un compte de profil PRESTATAIRE à son entreprise prestataire
ALTER TABLE utilisateur
    ADD COLUMN prestataireId BIGINT NULL,
    ADD CONSTRAINT cleEtrangereUtilisateurPrestataire FOREIGN KEY (prestataireId)
        REFERENCES prestataire (id) ON DELETE SET NULL,
    ADD INDEX utilisateurPrestataireId (prestataireId);

-- Données de démonstration : une entreprise prestataire et le compte « prestataire » qui lui est rattaché
INSERT INTO prestataire (nom, email, telephone)
VALUES ('Maintenance Démo', 'contact@maintenance-demo.example', '0100000000');

UPDATE utilisateur
SET prestataireId = (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo')
WHERE login = 'prestataire';