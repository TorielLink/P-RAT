-- Position des stations sur la carte en grille (plan schématique, de -20 à 20 sur chaque axe)
-- Nullable : une station sans position reste valide, elle n'est simplement pas placée sur la carte
ALTER TABLE station
    ADD COLUMN positionX INT NULL,
    ADD COLUMN positionY INT NULL,
    ADD CONSTRAINT checkPositionX CHECK (positionX BETWEEN -20 AND 20),
    ADD CONSTRAINT checkPositionY CHECK (positionY BETWEEN -20 AND 20),
    -- Les deux coordonnées ou aucune : pas de demi-position impossible à placer
    ADD CONSTRAINT checkPositionComplete CHECK ((positionX IS NULL) = (positionY IS NULL)),
    -- Une case par station : deux marqueurs superposés seraient impossibles à cliquer
    ADD CONSTRAINT positionUnique UNIQUE (positionX, positionY);

-- Données de démo : disposition fidèle au réseau réel (ouest → est, nord → sud)
UPDATE station SET positionX = -5, positionY = 3 WHERE nom = 'Châtelet';
UPDATE station SET positionX = 4, positionY = -4 WHERE nom = 'Gare de Lyon';
UPDATE station SET positionX = 12, positionY = -1 WHERE nom = 'Nation';