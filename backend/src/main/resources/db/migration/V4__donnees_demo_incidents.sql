-- ---------- Stations ----------
INSERT INTO station (nom) VALUES ('Châtelet');
SET @chatelet = LAST_INSERT_ID();

INSERT INTO station (nom) VALUES ('Gare de Lyon');
SET @gareDeLyon = LAST_INSERT_ID();

-- Station sans équipement (F2 : « Aucun équipement dans cette station »)
INSERT INTO station (nom) VALUES ('Nation');

-- ---------- Équipements ----------
INSERT INTO equipement (reference, description, type, etat, stationId)
VALUES ('DEMCAM01', 'Caméra quai direction Est', 'CAMERA', 'EN_PANNE', @chatelet);
SET @camera = LAST_INSERT_ID();

INSERT INTO equipement (reference, description, type, etat, stationId)
VALUES ('DEMASC01', 'Ascenseur accès correspondance', 'ASCENSEUR', 'EN_PANNE', @chatelet);
SET @ascenseur = LAST_INSERT_ID();

INSERT INTO equipement (reference, description, type, etat, stationId)
VALUES ('DEMESC01', 'Escalator sortie principale', 'ESCALATOR', 'EN_PANNE', @gareDeLyon);
SET @escalator = LAST_INSERT_ID();

INSERT INTO equipement (reference, description, type, etat, stationId)
VALUES ('DEMPOR01', 'Porte palière quai 2', 'PORTE', 'EN_MAINTENANCE', @gareDeLyon);
SET @porte = LAST_INSERT_ID();

INSERT INTO equipement (reference, description, type, etat, stationId)
VALUES ('DEMCAM02', 'Caméra hall billetterie', 'CAMERA', 'EN_SERVICE', @gareDeLyon);
SET @cameraHall = LAST_INSERT_ID();

-- Équipement sans incident (F3 : « Aucun incident enregistré »)
INSERT INTO equipement (reference, description, type, etat, stationId)
VALUES ('DEMCAM03', 'Caméra couloir nord', 'CAMERA', 'EN_SERVICE', @chatelet);

-- ---------- Comptes et prestataire existants (V2, V3) ----------
SET @agent = (SELECT id FROM utilisateur WHERE login = 'agent');
SET @admin = (SELECT id FROM utilisateur WHERE login = 'admin');
SET @maintenance = (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo');

-- ---------- Incidents ----------
-- Non affecté depuis 5 h → VERT
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, utilisateurId)
VALUES (@camera, 'Image figée sur le moniteur du poste de contrôle.',
        NOW() - INTERVAL 5 HOUR, 'OUVERT', 'MOYENNE', @agent);

-- Non affecté depuis 50 h → ORANGE
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, utilisateurId)
VALUES (@ascenseur, 'Ascenseur bloqué au niveau -1, portes qui ne s''ouvrent plus.',
        NOW() - INTERVAL 50 HOUR, 'OUVERT', 'HAUTE', @agent);

-- Non affecté depuis 80 h → ROUGE
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, utilisateurId)
VALUES (@escalator, 'Escalator à l''arrêt, bruit métallique signalé par les voyageurs.',
        NOW() - INTERVAL 80 HOUR, 'OUVERT', 'CRITIQUE', @admin);

-- Affecté depuis 100 h → PRIS EN CHARGE, jamais rouge
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId)
VALUES (@porte, 'Porte palière qui se referme avec un temps de retard.',
        NOW() - INTERVAL 100 HOUR, 'EN_COURS', 'HAUTE', @maintenance, @agent);

-- Résolu et évalué (F5 : évaluation affichée)
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES (@cameraHall, 'Caméra hors ligne après une coupure électrique.',
        NOW() - INTERVAL 6 DAY, 'RESOLU', 'MOYENNE', @maintenance, @agent, NOW() - INTERVAL 4 DAY);
SET @incidentEvalue = LAST_INSERT_ID();

INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (@incidentEvalue, 4, 'Intervention rapide, câble remplacé proprement.', CURRENT_DATE - INTERVAL 3 DAY);

-- Résolu sans évaluation (F5 : « pas encore évalué »)
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES (@ascenseur, 'Bouton d''appel du rez-de-chaussée inopérant.',
        NOW() - INTERVAL 10 DAY, 'RESOLU', 'BASSE', @maintenance, @admin, NOW() - INTERVAL 9 DAY);