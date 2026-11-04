-- Jeu de données de démonstration : réseau de 18 stations, équipements, incidents et évaluations.
-- Généré par un script (graine fixe), toutes les dates sont relatives à l'instant de la migration.
-- Mot de passe de tous les nouveaux comptes : demo123 (haché BCrypt repris de la V2)

-- ========== Prestataires (ceux de la V3 et de la V5 sont conservés) ==========
INSERT INTO prestataire (nom, email, telephone) VALUES
    ('Escaliers Mécaniques Services', 'atelier@escaliers-services.example', '0100000003'),
    ('Portes & Accès Sécurité', 'intervention@portes-acces.example', '0100000004'),
    ('Techni-Rail Assistance', 'contact@techni-rail.example', '0100000005');

-- ========== Comptes de démonstration supplémentaires ==========
INSERT INTO utilisateur (login, motDePasse, prenom, profil, prestataireId) VALUES
    ('agent2', '$2a$10$PjRQpTOpxKOG4wp58O/1p.3tSujTKnBFyDs5MrDgqZekaVOf4klaW', 'Mehdi', 'AGENT', NULL),
    ('ascenseurs', '$2a$10$PjRQpTOpxKOG4wp58O/1p.3tSujTKnBFyDs5MrDgqZekaVOf4klaW', 'Sarah', 'PRESTATAIRE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express')),
    ('video', '$2a$10$PjRQpTOpxKOG4wp58O/1p.3tSujTKnBFyDs5MrDgqZekaVOf4klaW', 'Hugo', 'PRESTATAIRE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services')),
    ('escaliers', '$2a$10$PjRQpTOpxKOG4wp58O/1p.3tSujTKnBFyDs5MrDgqZekaVOf4klaW', 'Léa', 'PRESTATAIRE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services')),
    ('portes', '$2a$10$PjRQpTOpxKOG4wp58O/1p.3tSujTKnBFyDs5MrDgqZekaVOf4klaW', 'Karim', 'PRESTATAIRE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'));

-- ========== Stations (positions sur la carte : grille de -20 à 20, une case par station) ==========
INSERT INTO station (nom, positionX, positionY) VALUES
    ('La Défense', -19, 6),
    ('Charles de Gaulle – Étoile', -14, 4),
    ('Invalides', -10, 1),
    ('Saint-Lazare', -7, 8),
    ('Opéra', -6, 6),
    ('Gare du Nord', -1, 11),
    ('République', 3, 6),
    ('Belleville', 8, 8),
    ('Bastille', 3, 1),
    ('Bercy', 7, -6),
    ('Montparnasse – Bienvenüe', -9, -6),
    ('Denfert-Rochereau', -4, -10),
    ('Place d''Italie', 0, -10),
    ('Porte d''Orléans', -5, -15),
    ('Saint-Denis – Université', NULL, NULL);
-- « Saint-Denis – Université » n'a volontairement pas de position : elle illustre une station non placée.

-- ========== Équipements ==========
INSERT INTO equipement (reference, description, type, etat, stationId, dateCreation) VALUES
    ('DEFCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'La Défense' ORDER BY id LIMIT 1), NOW() - INTERVAL 796 DAY),
    ('DEFCA002', 'Caméra quai direction sud', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'La Défense' ORDER BY id LIMIT 1), NOW() - INTERVAL 364 DAY),
    ('DEFAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'La Défense' ORDER BY id LIMIT 1), NOW() - INTERVAL 480 DAY),
    ('DEFES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_MAINTENANCE', (SELECT id FROM station WHERE nom = 'La Défense' ORDER BY id LIMIT 1), NOW() - INTERVAL 529 DAY),
    ('DEFES002', 'Escalator descendant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'La Défense' ORDER BY id LIMIT 1), NOW() - INTERVAL 660 DAY),
    ('DEFPO001', 'Porte palière quai 1', 'PORTE', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'La Défense' ORDER BY id LIMIT 1), NOW() - INTERVAL 755 DAY),
    ('ETOCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_MAINTENANCE', (SELECT id FROM station WHERE nom = 'Charles de Gaulle – Étoile' ORDER BY id LIMIT 1), NOW() - INTERVAL 170 DAY),
    ('ETOCA002', 'Caméra quai direction sud', 'CAMERA', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Charles de Gaulle – Étoile' ORDER BY id LIMIT 1), NOW() - INTERVAL 383 DAY),
    ('ETOAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Charles de Gaulle – Étoile' ORDER BY id LIMIT 1), NOW() - INTERVAL 546 DAY),
    ('ETOES001', 'Escalator montant sortie 1', 'ESCALATOR', 'HORS_SERVICE', (SELECT id FROM station WHERE nom = 'Charles de Gaulle – Étoile' ORDER BY id LIMIT 1), NOW() - INTERVAL 156 DAY),
    ('ETOPO001', 'Porte palière quai 1', 'PORTE', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Charles de Gaulle – Étoile' ORDER BY id LIMIT 1), NOW() - INTERVAL 814 DAY),
    ('INVCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_MAINTENANCE', (SELECT id FROM station WHERE nom = 'Invalides' ORDER BY id LIMIT 1), NOW() - INTERVAL 704 DAY),
    ('INVAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Invalides' ORDER BY id LIMIT 1), NOW() - INTERVAL 750 DAY),
    ('INVPO001', 'Porte palière quai 1', 'PORTE', 'EN_MAINTENANCE', (SELECT id FROM station WHERE nom = 'Invalides' ORDER BY id LIMIT 1), NOW() - INTERVAL 784 DAY),
    ('SLACA001', 'Caméra quai direction nord', 'CAMERA', 'HORS_SERVICE', (SELECT id FROM station WHERE nom = 'Saint-Lazare' ORDER BY id LIMIT 1), NOW() - INTERVAL 859 DAY),
    ('SLACA002', 'Caméra quai direction sud', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Saint-Lazare' ORDER BY id LIMIT 1), NOW() - INTERVAL 225 DAY),
    ('SLAAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Saint-Lazare' ORDER BY id LIMIT 1), NOW() - INTERVAL 618 DAY),
    ('SLAES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Saint-Lazare' ORDER BY id LIMIT 1), NOW() - INTERVAL 534 DAY),
    ('SLAES002', 'Escalator descendant sortie 1', 'ESCALATOR', 'EN_MAINTENANCE', (SELECT id FROM station WHERE nom = 'Saint-Lazare' ORDER BY id LIMIT 1), NOW() - INTERVAL 179 DAY),
    ('SLAPO001', 'Porte palière quai 1', 'PORTE', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Saint-Lazare' ORDER BY id LIMIT 1), NOW() - INTERVAL 609 DAY),
    ('SLAPO002', 'Porte palière quai 2', 'PORTE', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Saint-Lazare' ORDER BY id LIMIT 1), NOW() - INTERVAL 849 DAY),
    ('OPECA001', 'Caméra quai direction nord', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Opéra' ORDER BY id LIMIT 1), NOW() - INTERVAL 314 DAY),
    ('OPEAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Opéra' ORDER BY id LIMIT 1), NOW() - INTERVAL 710 DAY),
    ('OPEES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Opéra' ORDER BY id LIMIT 1), NOW() - INTERVAL 632 DAY),
    ('GDNCA001', 'Caméra quai direction nord', 'CAMERA', 'HORS_SERVICE', (SELECT id FROM station WHERE nom = 'Gare du Nord' ORDER BY id LIMIT 1), NOW() - INTERVAL 872 DAY),
    ('GDNCA002', 'Caméra quai direction sud', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Gare du Nord' ORDER BY id LIMIT 1), NOW() - INTERVAL 395 DAY),
    ('GDNCA003', 'Caméra hall des guichets', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Gare du Nord' ORDER BY id LIMIT 1), NOW() - INTERVAL 268 DAY),
    ('GDNAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Gare du Nord' ORDER BY id LIMIT 1), NOW() - INTERVAL 442 DAY),
    ('GDNAS002', 'Ascenseur salle des billets ↔ quai', 'ASCENSEUR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Gare du Nord' ORDER BY id LIMIT 1), NOW() - INTERVAL 855 DAY),
    ('GDNES001', 'Escalator montant sortie 1', 'ESCALATOR', 'HORS_SERVICE', (SELECT id FROM station WHERE nom = 'Gare du Nord' ORDER BY id LIMIT 1), NOW() - INTERVAL 448 DAY),
    ('GDNES002', 'Escalator descendant sortie 1', 'ESCALATOR', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Gare du Nord' ORDER BY id LIMIT 1), NOW() - INTERVAL 367 DAY),
    ('GDNPO001', 'Porte palière quai 1', 'PORTE', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Gare du Nord' ORDER BY id LIMIT 1), NOW() - INTERVAL 263 DAY),
    ('REPCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_MAINTENANCE', (SELECT id FROM station WHERE nom = 'République' ORDER BY id LIMIT 1), NOW() - INTERVAL 848 DAY),
    ('REPCA002', 'Caméra quai direction sud', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'République' ORDER BY id LIMIT 1), NOW() - INTERVAL 477 DAY),
    ('REPAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'République' ORDER BY id LIMIT 1), NOW() - INTERVAL 713 DAY),
    ('REPES001', 'Escalator montant sortie 1', 'ESCALATOR', 'HORS_SERVICE', (SELECT id FROM station WHERE nom = 'République' ORDER BY id LIMIT 1), NOW() - INTERVAL 665 DAY),
    ('REPES002', 'Escalator descendant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'République' ORDER BY id LIMIT 1), NOW() - INTERVAL 659 DAY),
    ('REPPO001', 'Porte palière quai 1', 'PORTE', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'République' ORDER BY id LIMIT 1), NOW() - INTERVAL 688 DAY),
    ('BELCA001', 'Caméra quai direction nord', 'CAMERA', 'HORS_SERVICE', (SELECT id FROM station WHERE nom = 'Belleville' ORDER BY id LIMIT 1), NOW() - INTERVAL 655 DAY),
    ('BELES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Belleville' ORDER BY id LIMIT 1), NOW() - INTERVAL 544 DAY),
    ('BELPO001', 'Porte palière quai 1', 'PORTE', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Belleville' ORDER BY id LIMIT 1), NOW() - INTERVAL 694 DAY),
    ('BASCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Bastille' ORDER BY id LIMIT 1), NOW() - INTERVAL 612 DAY),
    ('BASAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Bastille' ORDER BY id LIMIT 1), NOW() - INTERVAL 470 DAY),
    ('BASES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Bastille' ORDER BY id LIMIT 1), NOW() - INTERVAL 335 DAY),
    ('BASPO001', 'Porte palière quai 1', 'PORTE', 'EN_MAINTENANCE', (SELECT id FROM station WHERE nom = 'Bastille' ORDER BY id LIMIT 1), NOW() - INTERVAL 576 DAY),
    ('BERCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Bercy' ORDER BY id LIMIT 1), NOW() - INTERVAL 366 DAY),
    ('BERAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Bercy' ORDER BY id LIMIT 1), NOW() - INTERVAL 307 DAY),
    ('BERPO001', 'Porte palière quai 1', 'PORTE', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Bercy' ORDER BY id LIMIT 1), NOW() - INTERVAL 768 DAY),
    ('MONCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Montparnasse – Bienvenüe' ORDER BY id LIMIT 1), NOW() - INTERVAL 375 DAY),
    ('MONCA002', 'Caméra quai direction sud', 'CAMERA', 'EN_MAINTENANCE', (SELECT id FROM station WHERE nom = 'Montparnasse – Bienvenüe' ORDER BY id LIMIT 1), NOW() - INTERVAL 845 DAY),
    ('MONAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Montparnasse – Bienvenüe' ORDER BY id LIMIT 1), NOW() - INTERVAL 187 DAY),
    ('MONAS002', 'Ascenseur salle des billets ↔ quai', 'ASCENSEUR', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Montparnasse – Bienvenüe' ORDER BY id LIMIT 1), NOW() - INTERVAL 695 DAY),
    ('MONES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Montparnasse – Bienvenüe' ORDER BY id LIMIT 1), NOW() - INTERVAL 473 DAY),
    ('MONES002', 'Escalator descendant sortie 1', 'ESCALATOR', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Montparnasse – Bienvenüe' ORDER BY id LIMIT 1), NOW() - INTERVAL 889 DAY),
    ('MONPO001', 'Porte palière quai 1', 'PORTE', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Montparnasse – Bienvenüe' ORDER BY id LIMIT 1), NOW() - INTERVAL 654 DAY),
    ('DENCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Denfert-Rochereau' ORDER BY id LIMIT 1), NOW() - INTERVAL 679 DAY),
    ('DENES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Denfert-Rochereau' ORDER BY id LIMIT 1), NOW() - INTERVAL 162 DAY),
    ('ITACA001', 'Caméra quai direction nord', 'CAMERA', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Place d''Italie' ORDER BY id LIMIT 1), NOW() - INTERVAL 673 DAY),
    ('ITAAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'HORS_SERVICE', (SELECT id FROM station WHERE nom = 'Place d''Italie' ORDER BY id LIMIT 1), NOW() - INTERVAL 509 DAY),
    ('ITAES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Place d''Italie' ORDER BY id LIMIT 1), NOW() - INTERVAL 655 DAY),
    ('PORCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Porte d''Orléans' ORDER BY id LIMIT 1), NOW() - INTERVAL 540 DAY),
    ('PORPO001', 'Porte palière quai 1', 'PORTE', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Porte d''Orléans' ORDER BY id LIMIT 1), NOW() - INTERVAL 597 DAY),
    ('SDUCA001', 'Caméra quai direction nord', 'CAMERA', 'HORS_SERVICE', (SELECT id FROM station WHERE nom = 'Saint-Denis – Université' ORDER BY id LIMIT 1), NOW() - INTERVAL 348 DAY),
    ('SDUES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_MAINTENANCE', (SELECT id FROM station WHERE nom = 'Saint-Denis – Université' ORDER BY id LIMIT 1), NOW() - INTERVAL 402 DAY),
    ('CHACA001', 'Caméra quai direction nord', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Châtelet' ORDER BY id LIMIT 1), NOW() - INTERVAL 825 DAY),
    ('CHAES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Châtelet' ORDER BY id LIMIT 1), NOW() - INTERVAL 682 DAY),
    ('CHAES002', 'Escalator descendant sortie 1', 'ESCALATOR', 'EN_PANNE', (SELECT id FROM station WHERE nom = 'Châtelet' ORDER BY id LIMIT 1), NOW() - INTERVAL 162 DAY),
    ('CHAPO001', 'Porte palière quai 1', 'PORTE', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Châtelet' ORDER BY id LIMIT 1), NOW() - INTERVAL 689 DAY),
    ('GDLCA001', 'Caméra quai direction nord', 'CAMERA', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Gare de Lyon' ORDER BY id LIMIT 1), NOW() - INTERVAL 620 DAY),
    ('GDLAS001', 'Ascenseur rue ↔ salle des billets', 'ASCENSEUR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Gare de Lyon' ORDER BY id LIMIT 1), NOW() - INTERVAL 696 DAY),
    ('GDLES001', 'Escalator montant sortie 1', 'ESCALATOR', 'EN_SERVICE', (SELECT id FROM station WHERE nom = 'Gare de Lyon' ORDER BY id LIMIT 1), NOW() - INTERVAL 377 DAY);

-- ========== Incidents (et leur évaluation juste après, via LAST_INSERT_ID()) ==========
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLAAS001'), 'Bruit de frottement important pendant la montée.', NOW() - INTERVAL 127180 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'admin'), NOW() - INTERVAL 120110 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, NULL, DATE(NOW() - INTERVAL 119305 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ITACA001'), 'Image floue et figée par intermittence, impossible d''identifier les voyageurs.', NOW() - INTERVAL 126796 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 123576 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, NULL, DATE(NOW() - INTERVAL 120819 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNCA001'), 'Image floue et figée par intermittence, impossible d''identifier les voyageurs.', NOW() - INTERVAL 125872 MINUTE, 'RESOLU', 'MOYENNE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 120046 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOES001'), 'Peigne de sortie cassé, risque de coincement des chaussures.', NOW() - INTERVAL 123907 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 119856 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, 'Deux passages nécessaires pour résoudre la panne.', DATE(NOW() - INTERVAL 118471 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOCA002'), 'Coupure du flux vidéo après l''orage, voyant d''alimentation éteint.', NOW() - INTERVAL 123183 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 118092 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BERCA001'), 'Objectif tagué : le champ de vision est masqué aux trois quarts.', NOW() - INTERVAL 123081 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 121450 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Problème résolu, compte rendu clair.', DATE(NOW() - INTERVAL 120135 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BASAS001'), 'Arrêt décalé d''une dizaine de centimètres par rapport au palier, risque de chute.', NOW() - INTERVAL 122593 MINUTE, 'RESOLU', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 117814 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 5, 'Intervention rapide et soignée, équipement remis en service dans l''heure.', DATE(NOW() - INTERVAL 116605 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BELPO001'), 'Portillon PMR qui ne s''ouvre plus avec le passe.', NOW() - INTERVAL 119944 MINUTE, 'RESOLU', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 118769 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Travail correct, nettoyage de la zone à améliorer.', DATE(NOW() - INTERVAL 116587 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPES001'), 'Arrêt d''urgence déclenché à répétition sans raison apparente.', NOW() - INTERVAL 116885 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 109996 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Intervention correcte mais délai long.', DATE(NOW() - INTERVAL 108778 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DENES001'), 'Main courante qui avance moins vite que les marches.', NOW() - INTERVAL 116265 MINUTE, 'RESOLU', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 114463 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, 'Délai d''intervention trop long pour une panne bloquante.', DATE(NOW() - INTERVAL 114173 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DEFCA001'), 'Image noire sur le moniteur du poste de contrôle depuis la relève.', NOW() - INTERVAL 115607 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 109130 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDLCA001'), 'Image floue et figée par intermittence, impossible d''identifier les voyageurs.', NOW() - INTERVAL 112032 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'admin'), NOW() - INTERVAL 108910 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPCA002'), 'Image floue et figée par intermittence, impossible d''identifier les voyageurs.', NOW() - INTERVAL 110938 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 106948 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'INVAS001'), 'Bruit de frottement important pendant la montée.', NOW() - INTERVAL 108013 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 101844 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Travail correct, nettoyage de la zone à améliorer.', DATE(NOW() - INTERVAL 99754 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPCA001'), 'La caméra ne répond plus aux commandes de rotation.', NOW() - INTERVAL 106899 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 101603 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, NULL, DATE(NOW() - INTERVAL 101513 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'CHAES002'), 'Arrêt d''urgence déclenché à répétition sans raison apparente.', NOW() - INTERVAL 106671 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 104832 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Réparation provisoire, à surveiller.', DATE(NOW() - INTERVAL 102915 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BERAS001'), 'Cabine arrêtée avec message « hors service » sur l''afficheur.', NOW() - INTERVAL 105963 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 98907 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Bonne intervention, un peu de retard à l''arrivée.', DATE(NOW() - INTERVAL 97329 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLACA001'), 'Objectif tagué : le champ de vision est masqué aux trois quarts.', NOW() - INTERVAL 105551 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 103046 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Intervention correcte mais délai long.', DATE(NOW() - INTERVAL 102089 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPCA001'), 'Coupure du flux vidéo après l''orage, voyant d''alimentation éteint.', NOW() - INTERVAL 104902 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 103904 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Résolu, mais il a fallu relancer le prestataire.', DATE(NOW() - INTERVAL 102664 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'CHAES001'), 'Escalator à l''arrêt, bruit métallique signalé par plusieurs voyageurs.', NOW() - INTERVAL 102127 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 99807 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Résolu, mais il a fallu relancer le prestataire.', DATE(NOW() - INTERVAL 99083 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLAPO002'), 'Porte bloquée en position ouverte après le passage de la rame.', NOW() - INTERVAL 100385 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 97096 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DENCA001'), 'Enregistrements absents depuis deux jours sur le serveur de la station.', NOW() - INTERVAL 87870 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 87423 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BERAS001'), 'Bruit de frottement important pendant la montée.', NOW() - INTERVAL 87299 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 82271 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNAS002'), 'Interphone de la cabine muet : aucune liaison avec le poste de contrôle.', NOW() - INTERVAL 87028 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 83790 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Travail correct, nettoyage de la zone à améliorer.', DATE(NOW() - INTERVAL 83405 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNAS001'), 'Les portes se rouvrent en boucle sans que la cabine ne démarre.', NOW() - INTERVAL 86771 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'admin'), NOW() - INTERVAL 84742 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BELES001'), 'Marche abîmée en haut de l''escalator, zone balisée par l''agent.', NOW() - INTERVAL 86705 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 83197 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'PORPO001'), 'Porte bloquée en position ouverte après le passage de la rame.', NOW() - INTERVAL 85514 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'), (SELECT id FROM utilisateur WHERE login = 'admin'), NOW() - INTERVAL 83743 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Bonne intervention, un peu de retard à l''arrivée.', DATE(NOW() - INTERVAL 83272 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONES002'), 'Vibrations fortes et odeur de brûlé pendant le fonctionnement.', NOW() - INTERVAL 85212 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 83311 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, NULL, DATE(NOW() - INTERVAL 82078 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOCA001'), 'Objectif tagué : le champ de vision est masqué aux trois quarts.', NOW() - INTERVAL 85109 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 82201 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, NULL, DATE(NOW() - INTERVAL 80296 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLAAS001'), 'Bouton d''appel du niveau quai inopérant.', NOW() - INTERVAL 79664 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 79322 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 5, 'Diagnostic précis et pièce remplacée immédiatement.', DATE(NOW() - INTERVAL 77825 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BERPO001'), 'Alarme de la porte de secours qui sonne sans intervention.', NOW() - INTERVAL 79402 MINUTE, 'RESOLU', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 77497 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Bonne intervention, un peu de retard à l''arrivée.', DATE(NOW() - INTERVAL 77306 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'INVAS001'), 'Bouton d''appel du niveau quai inopérant.', NOW() - INTERVAL 78053 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 77037 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 5, 'Intervention rapide et soignée, équipement remis en service dans l''heure.', DATE(NOW() - INTERVAL 74180 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ITAES001'), 'Escalator à l''arrêt, bruit métallique signalé par plusieurs voyageurs.', NOW() - INTERVAL 77264 MINUTE, 'RESOLU', 'BASSE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 72882 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPES001'), 'Escalator à l''arrêt, bruit métallique signalé par plusieurs voyageurs.', NOW() - INTERVAL 75693 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 73462 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, 'Délai d''intervention trop long pour une panne bloquante.', DATE(NOW() - INTERVAL 71300 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONES002'), 'Démarrage impossible ce matin malgré la remise sous tension.', NOW() - INTERVAL 75026 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 73701 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, 'Peu d''informations transmises sur la cause de la panne.', DATE(NOW() - INTERVAL 73656 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLAPO002'), 'Détecteur d''obstacle défaillant : la porte ne se rouvre pas.', NOW() - INTERVAL 73676 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 72266 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Problème résolu, compte rendu clair.', DATE(NOW() - INTERVAL 71483 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DEFCA001'), 'Image noire sur le moniteur du poste de contrôle depuis la relève.', NOW() - INTERVAL 72124 MINUTE, 'RESOLU', 'HAUTE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 65862 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DEFES001'), 'Marche abîmée en haut de l''escalator, zone balisée par l''agent.', NOW() - INTERVAL 70491 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'admin'), NOW() - INTERVAL 68926 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, 'Deux passages nécessaires pour résoudre la panne.', DATE(NOW() - INTERVAL 68693 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONPO001'), 'Alarme de la porte de secours qui sonne sans intervention.', NOW() - INTERVAL 66494 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 65063 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 5, 'Diagnostic précis et pièce remplacée immédiatement.', DATE(NOW() - INTERVAL 64713 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'CHAPO001'), 'Porte bloquée en position ouverte après le passage de la rame.', NOW() - INTERVAL 65035 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 58451 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNPO001'), 'Vitrage fissuré sur le vantail gauche, zone sécurisée par l''agent.', NOW() - INTERVAL 64795 MINUTE, 'RESOLU', 'BASSE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 59303 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPCA002'), 'Enregistrements absents depuis deux jours sur le serveur de la station.', NOW() - INTERVAL 62413 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 60275 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Bonne intervention, un peu de retard à l''arrivée.', DATE(NOW() - INTERVAL 58630 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOES001'), 'Arrêt d''urgence déclenché à répétition sans raison apparente.', NOW() - INTERVAL 60988 MINUTE, 'RESOLU', 'BASSE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 58921 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'INVAS001'), 'Ascenseur bloqué entre deux niveaux, aucun voyageur à l''intérieur.', NOW() - INTERVAL 59882 MINUTE, 'RESOLU', 'BASSE', NULL, (SELECT id FROM utilisateur WHERE login = 'admin'), NOW() - INTERVAL 55117 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOCA002'), 'Image floue et figée par intermittence, impossible d''identifier les voyageurs.', NOW() - INTERVAL 58446 MINUTE, 'RESOLU', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 55233 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DEFAS001'), 'Arrêt décalé d''une dizaine de centimètres par rapport au palier, risque de chute.', NOW() - INTERVAL 56213 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'admin'), NOW() - INTERVAL 51497 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 5, 'Intervention rapide et soignée, équipement remis en service dans l''heure.', DATE(NOW() - INTERVAL 49141 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNCA001'), 'La caméra ne répond plus aux commandes de rotation.', NOW() - INTERVAL 53581 MINUTE, 'RESOLU', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 48950 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Travail correct, nettoyage de la zone à améliorer.', DATE(NOW() - INTERVAL 46554 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONAS002'), 'Arrêt décalé d''une dizaine de centimètres par rapport au palier, risque de chute.', NOW() - INTERVAL 52145 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 50680 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 5, 'Diagnostic précis et pièce remplacée immédiatement.', DATE(NOW() - INTERVAL 48858 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLAPO002'), 'Porte bloquée en position ouverte après le passage de la rame.', NOW() - INTERVAL 51715 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 46856 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Réparation provisoire, à surveiller.', DATE(NOW() - INTERVAL 45288 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONES002'), 'Arrêt d''urgence déclenché à répétition sans raison apparente.', NOW() - INTERVAL 44944 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 41760 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Bonne intervention, un peu de retard à l''arrivée.', DATE(NOW() - INTERVAL 40692 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPES001'), 'Vibrations fortes et odeur de brûlé pendant le fonctionnement.', NOW() - INTERVAL 39602 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 33645 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Intervention correcte mais délai long.', DATE(NOW() - INTERVAL 32830 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNCA002'), 'La caméra ne répond plus aux commandes de rotation.', NOW() - INTERVAL 33348 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 27534 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLACA001'), 'Image noire sur le moniteur du poste de contrôle depuis la relève.', NOW() - INTERVAL 32184 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'admin'), NOW() - INTERVAL 28971 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Problème résolu, compte rendu clair.', DATE(NOW() - INTERVAL 28834 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLACA001'), 'Objectif tagué : le champ de vision est masqué aux trois quarts.', NOW() - INTERVAL 30754 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 30180 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Intervention correcte mais délai long.', DATE(NOW() - INTERVAL 30166 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOAS001'), 'Bruit de frottement important pendant la montée.', NOW() - INTERVAL 30712 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 26077 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DEFES001'), 'Escalator à l''arrêt, bruit métallique signalé par plusieurs voyageurs.', NOW() - INTERVAL 29255 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 22381 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 1, NULL, DATE(NOW() - INTERVAL 19896 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLACA001'), 'Vision nocturne inopérante : image saturée de bruit en soirée.', NOW() - INTERVAL 28401 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 26220 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'CHAES001'), 'Main courante qui avance moins vite que les marches.', NOW() - INTERVAL 24140 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 21086 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONES002'), 'Vibrations fortes et odeur de brûlé pendant le fonctionnement.', NOW() - INTERVAL 23872 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 21691 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, 'Délai d''intervention trop long pour une panne bloquante.', DATE(NOW() - INTERVAL 21265 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLAAS001'), 'Cabine arrêtée avec message « hors service » sur l''afficheur.', NOW() - INTERVAL 22659 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 20320 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Travail correct, nettoyage de la zone à améliorer.', DATE(NOW() - INTERVAL 19815 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOAS001'), 'Arrêt décalé d''une dizaine de centimètres par rapport au palier, risque de chute.', NOW() - INTERVAL 21716 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 17677 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPAS001'), 'Cabine arrêtée avec message « hors service » sur l''afficheur.', NOW() - INTERVAL 19414 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 12862 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Intervention correcte mais délai long.', DATE(NOW() - INTERVAL 12682 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'INVCA001'), 'Image noire sur le moniteur du poste de contrôle depuis la relève.', NOW() - INTERVAL 18056 MINUTE, 'EN_COURS', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'admin'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BELCA001'), 'Objectif tagué : le champ de vision est masqué aux trois quarts.', NOW() - INTERVAL 15616 MINUTE, 'EN_COURS', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BASPO001'), 'Porte bloquée en position ouverte après le passage de la rame.', NOW() - INTERVAL 13840 MINUTE, 'EN_COURS', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BELES001'), 'Vibrations fortes et odeur de brûlé pendant le fonctionnement.', NOW() - INTERVAL 13641 MINUTE, 'RESOLU', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 12539 MINUTE);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNAS001'), 'Bouton d''appel du niveau quai inopérant.', NOW() - INTERVAL 13347 MINUTE, 'RESOLU', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 11272 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 5, 'Diagnostic précis et pièce remplacée immédiatement.', DATE(NOW() - INTERVAL 9206 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONES002'), 'Démarrage impossible ce matin malgré la remise sous tension.', NOW() - INTERVAL 13027 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 7296 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, 'Peu d''informations transmises sur la cause de la panne.', DATE(NOW() - INTERVAL 5866 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONES002'), 'Démarrage impossible ce matin malgré la remise sous tension.', NOW() - INTERVAL 12616 MINUTE, 'OUVERT', 'HAUTE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONCA001'), 'Coupure du flux vidéo après l''orage, voyant d''alimentation éteint.', NOW() - INTERVAL 12538 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 11123 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 2, 'Peu d''informations transmises sur la cause de la panne.', DATE(NOW() - INTERVAL 10694 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONAS002'), 'Bouton d''appel du niveau quai inopérant.', NOW() - INTERVAL 12091 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 10329 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Intervention correcte mais délai long.', DATE(NOW() - INTERVAL 7475 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOCA001'), 'La caméra ne répond plus aux commandes de rotation.', NOW() - INTERVAL 11452 MINUTE, 'EN_COURS', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'admin'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOCA002'), 'Image noire sur le moniteur du poste de contrôle depuis la relève.', NOW() - INTERVAL 10598 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Maintenance Démo'), (SELECT id FROM utilisateur WHERE login = 'admin'), NOW() - INTERVAL 7113 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Intervention correcte mais délai long.', DATE(NOW() - INTERVAL 6742 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOCA001'), 'Objectif tagué : le champ de vision est masqué aux trois quarts.', NOW() - INTERVAL 10059 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent2'), NOW() - INTERVAL 7015 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Intervention correcte mais délai long.', DATE(NOW() - INTERVAL 6694 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BASPO001'), 'Portillon PMR qui ne s''ouvre plus avec le passe.', NOW() - INTERVAL 9017 MINUTE, 'EN_COURS', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNCA001'), 'Vision nocturne inopérante : image saturée de bruit en soirée.', NOW() - INTERVAL 7847 MINUTE, 'OUVERT', 'CRITIQUE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SDUES001'), 'Démarrage impossible ce matin malgré la remise sous tension.', NOW() - INTERVAL 7224 MINUTE, 'EN_COURS', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'admin'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'BELCA001'), 'Image floue et figée par intermittence, impossible d''identifier les voyageurs.', NOW() - INTERVAL 6818 MINUTE, 'RESOLU', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 2714 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Travail correct, nettoyage de la zone à améliorer.', DATE(NOW() - INTERVAL 2693 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPAS001'), 'Arrêt décalé d''une dizaine de centimètres par rapport au palier, risque de chute.', NOW() - INTERVAL 6007 MINUTE, 'OUVERT', 'HAUTE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ITAAS001'), 'Ascenseur bloqué entre deux niveaux, aucun voyageur à l''intérieur.', NOW() - INTERVAL 5807 MINUTE, 'EN_COURS', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Ascenseurs Express'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONAS002'), 'Bouton d''appel du niveau quai inopérant.', NOW() - INTERVAL 5425 MINUTE, 'OUVERT', 'MOYENNE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent2'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DEFCA002'), 'Image floue et figée par intermittence, impossible d''identifier les voyageurs.', NOW() - INTERVAL 5205 MINUTE, 'RESOLU', 'BASSE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 2478 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 4, 'Travail correct, nettoyage de la zone à améliorer.', DATE(NOW() - INTERVAL 1427 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPES001'), 'Main courante qui avance moins vite que les marches.', NOW() - INTERVAL 4806 MINUTE, 'OUVERT', 'CRITIQUE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPPO001'), 'Portillon PMR qui ne s''ouvre plus avec le passe.', NOW() - INTERVAL 4540 MINUTE, 'OUVERT', 'HAUTE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent2'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOCA001'), 'Objectif tagué : le champ de vision est masqué aux trois quarts.', NOW() - INTERVAL 4345 MINUTE, 'RESOLU', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NOW() - INTERVAL 1838 MINUTE);
INSERT INTO evaluation (incidentId, note, commentaire, dateEvaluation)
VALUES (LAST_INSERT_ID(), 3, 'Réparation provisoire, à surveiller.', DATE(NOW() - INTERVAL 491 MINUTE));
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'MONCA002'), 'La caméra ne répond plus aux commandes de rotation.', NOW() - INTERVAL 4228 MINUTE, 'EN_COURS', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLAPO002'), 'Alarme de la porte de secours qui sonne sans intervention.', NOW() - INTERVAL 4160 MINUTE, 'OUVERT', 'HAUTE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DENES001'), 'Main courante qui avance moins vite que les marches.', NOW() - INTERVAL 3797 MINUTE, 'OUVERT', 'BASSE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent2'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DEFAS001'), 'Bouton d''appel du niveau quai inopérant.', NOW() - INTERVAL 3483 MINUTE, 'OUVERT', 'MOYENNE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'OPEAS001'), 'Ascenseur bloqué entre deux niveaux, aucun voyageur à l''intérieur.', NOW() - INTERVAL 3322 MINUTE, 'OUVERT', 'MOYENNE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'INVPO001'), 'Vitrage fissuré sur le vantail gauche, zone sécurisée par l''agent.', NOW() - INTERVAL 3133 MINUTE, 'EN_COURS', 'MOYENNE', (SELECT id FROM prestataire WHERE nom = 'Portes & Accès Sécurité'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'PORPO001'), 'Portillon PMR qui ne s''ouvre plus avec le passe.', NOW() - INTERVAL 3027 MINUTE, 'OUVERT', 'HAUTE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DEFES001'), 'Escalator à l''arrêt, bruit métallique signalé par plusieurs voyageurs.', NOW() - INTERVAL 2794 MINUTE, 'OUVERT', 'BASSE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent2'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOES001'), 'Main courante qui avance moins vite que les marches.', NOW() - INTERVAL 2640 MINUTE, 'EN_COURS', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'admin'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SDUES001'), 'Main courante qui avance moins vite que les marches.', NOW() - INTERVAL 2426 MINUTE, 'OUVERT', 'MOYENNE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'REPCA001'), 'Image noire sur le moniteur du poste de contrôle depuis la relève.', NOW() - INTERVAL 1980 MINUTE, 'EN_COURS', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'DEFES001'), 'Marche abîmée en haut de l''escalator, zone balisée par l''agent.', NOW() - INTERVAL 1812 MINUTE, 'EN_COURS', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLACA001'), 'Coupure du flux vidéo après l''orage, voyant d''alimentation éteint.', NOW() - INTERVAL 1575 MINUTE, 'OUVERT', 'CRITIQUE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SLAES002'), 'Main courante qui avance moins vite que les marches.', NOW() - INTERVAL 1229 MINUTE, 'EN_COURS', 'HAUTE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'CHAES002'), 'Marche abîmée en haut de l''escalator, zone balisée par l''agent.', NOW() - INTERVAL 932 MINUTE, 'OUVERT', 'MOYENNE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent2'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNES001'), 'Main courante qui avance moins vite que les marches.', NOW() - INTERVAL 575 MINUTE, 'EN_COURS', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Escaliers Mécaniques Services'), (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'GDNES002'), 'Vibrations fortes et odeur de brûlé pendant le fonctionnement.', NOW() - INTERVAL 480 MINUTE, 'OUVERT', 'BASSE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'SDUCA001'), 'Coupure du flux vidéo après l''orage, voyant d''alimentation éteint.', NOW() - INTERVAL 310 MINUTE, 'EN_COURS', 'CRITIQUE', (SELECT id FROM prestataire WHERE nom = 'Vidéo Services'), (SELECT id FROM utilisateur WHERE login = 'admin'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ITACA001'), 'Image floue et figée par intermittence, impossible d''identifier les voyageurs.', NOW() - INTERVAL 214 MINUTE, 'OUVERT', 'MOYENNE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent'), NULL);
INSERT INTO incident (equipementId, description, dateOuverture, statut, priorite, prestataireId, utilisateurId, dateResolution)
VALUES ((SELECT id FROM equipement WHERE reference = 'ETOCA002'), 'Vision nocturne inopérante : image saturée de bruit en soirée.', NOW() - INTERVAL 62 MINUTE, 'OUVERT', 'HAUTE', NULL, (SELECT id FROM utilisateur WHERE login = 'agent2'), NULL);
