USE ludotheque;

INSERT INTO utilisateur (nom, prenom, email, mot_de_passe_hash, telephone, role, actif, date_inscription) VALUES
                                                                                                              ('Martin',  'Claire',  'claire.martin@mail.fr',  '$2y$10$hash_claire_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',  '0612345678', 'ADMIN', TRUE,  '2025-09-01'),
                                                                                                              ('Dubois',  'Thomas',  'thomas.dubois@mail.fr',  '$2y$10$hash_thomas_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',  '0623456789', 'ADMIN', TRUE,  '2025-09-01'),
                                                                                                              ('Leroy',   'Emma',    'emma.leroy@mail.fr',     '$2y$10$hash_emma_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',   '0634567890', 'ADHERENT',         TRUE,  '2025-10-12'),
                                                                                                              ('Moreau',  'Lucas',   'lucas.moreau@mail.fr',   '$2y$10$hash_lucas_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',   '0645678901', 'ADHERENT',         TRUE,  '2025-11-03'),
                                                                                                              ('Simon',   'Léa',     'lea.simon@mail.fr',      '$2y$10$hash_lea_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',   NULL,         'ADHERENT',         TRUE,  '2025-11-20'),
                                                                                                              ('Laurent', 'Hugo',    'hugo.laurent@mail.fr',   '$2y$10$hash_hugo_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',   '0667890123', 'ADHERENT',         TRUE,  '2026-01-08'),
                                                                                                              ('Michel',  'Chloé',   'chloe.michel@mail.fr',   '$2y$10$hash_chloe_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',   '0678901234', 'ADHERENT',         TRUE,  '2026-02-14'),
                                                                                                              ('Garcia',  'Nathan',  'nathan.garcia@mail.fr',  '$2y$10$hash_nathan_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',   '0689012345', 'ADHERENT',         FALSE, '2026-03-02'),
                                                                                                              ('Roux',    'Inès',    'ines.roux@mail.fr',      '$2y$10$hash_ines_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',   '0690123456', 'ADHERENT',         TRUE,  '2026-05-19'),
                                                                                                              ('Fournier','Maxime',  'maxime.fournier@mail.fr','$2y$10$hash_maxime_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',   '0601234567', 'ADHERENT',         TRUE,  '2026-09-10');


INSERT INTO type_media (Libelle) VALUES
                                     ('jeu_video'), ('jeu_societe'), ('livre'), ('dvd'), ('bd');


INSERT INTO media (titre, description, genre, auteur_editeur, annee, age_minimum, nb_exemplaires, id_type) VALUES
('The Legend of Zelda: Breath of the Wild', 'Exploration en monde ouvert à Hyrule.', 'Aventure', 'Nintendo', 2017, 12, 2, 1),
('EA Sports FC 24', 'Simulation de football.', 'Sport', 'EA Sports', 2023, 3, 3, 1),
('Elden Ring', 'Action-RPG dans les Terres Intermédiaires.', 'RPG', 'FromSoftware', 2022, 16, 1, 1),
('Mario Kart 8 Deluxe', 'Course arcade entre amis.', 'Course', 'Nintendo', 2017, 3, 3, 1),
('Forza Horizon 5', 'Course en monde ouvert au Mexique.', 'Course', 'Playground Games', 2021, 3, 2, 1),
('God of War Ragnarök', 'Aventure mythologique nordique.', 'Action', 'Santa Monica Studio', 2022, 18, 2, 1),
('Celeste', 'Plateformer exigeant et émouvant.', 'Plateforme', 'Maddy Makes Games', 2018, 7, 1, 1),
('Gran Turismo 7', 'Simulation de conduite.', 'Course', 'Polyphony Digital', 2022, 3, 1, 1),
-- Jeux de société (9 à 15)
('Catan', 'Commerce et développement de colonies.', 'Stratégie', 'Kosmos', 1995, 10, 2, 2),
('Dixit', 'Jeu d''imagination avec cartes illustrées.', 'Ambiance', 'Libellud', 2008, 8, 2, 2),
('Carcassonne', 'Construction de paysages médiévaux.', 'Placement de tuiles', 'Hans im Glück', 2000, 7, 2, 2),
('Dobble', 'Jeu de rapidité et d''observation.', 'Rapidité', 'Asmodee', 2009, 6, 3, 2),
('7 Wonders', 'Développement d''une cité antique.', 'Stratégie', 'Repos Production', 2010, 10, 1, 2),
('Pandemic', 'Jeu coopératif contre les épidémies.', 'Coopératif', 'Z-Man Games', 2008, 8, 1, 2),
('Codenames', 'Retrouver ses agents secrets par indices.', 'Ambiance', 'Czech Games Edition', 2015, 10, 2, 2),
-- Livres (16 à 18)
('Le Petit Prince', 'Conte poétique et philosophique.', 'Conte', 'Antoine de Saint-Exupéry', 1943, 7, 3, 3),
('1984', 'Roman dystopique.', 'Science-fiction', 'George Orwell', 1949, 15, 2, 3),
('Harry Potter à l''école des sorciers', 'Premier tome de la saga.', 'Fantasy', 'J.K. Rowling', 1997, 9, 3, 3),
-- DVD (19 à 20)
('Inception', 'Voyage dans les rêves.', 'Science-fiction', 'Christopher Nolan', 2010, 12, 2, 4),
('Le Voyage de Chihiro', 'Film d''animation japonais.', 'Animation', 'Studio Ghibli', 2001, 6, 2, 4),
-- BD (21 à 22)
('Astérix le Gaulois', 'Premier album d''Astérix.', 'Humour', 'Goscinny et Uderzo', 1961, 6, 2, 5),
('Tintin au Tibet', 'Aventure de Tintin dans l''Himalaya.', 'Aventure', 'Hergé', 1960, 6, 2, 5);

-- SOUS-TABLES
INSERT INTO jeu_video (id, plateforme) VALUES
                                           (1, 'switch'), (2, 'ps5'), (3, 'pc'), (4, 'switch'),
                                           (5, 'xbox'), (6, 'ps5'), (7, 'pc'), (8, 'ps4');

INSERT INTO jeu_societe (id, nb_personnes) VALUES
                                               (9, 4), (10, 6), (11, 5), (12, 8), (13, 7), (14, 4), (15, 8);

-- EMPRUNTS (certains rendus, d'autres en cours ou en retard)
INSERT INTO emprunt (id_utilisateur, id_media, date_emprunt, date_retour_prevue, date_retour_effective, prolonge, etat_retour) VALUES
                                                                                                                                   (3,  1,  '2026-08-01', '2026-08-15', '2026-08-14', FALSE, 'Bon état'),
                                                                                                                                   (3,  16, '2026-09-02', '2026-09-30', '2026-09-28', FALSE, 'Bon état'),
                                                                                                                                   (4,  2,  '2026-08-10', '2026-08-24', '2026-08-30', TRUE,  'Boîtier abîmé'),
                                                                                                                                   (4,  9,  '2026-09-15', '2026-09-29', '2026-09-29', FALSE, 'Bon état'),
                                                                                                                                   (5,  4,  '2026-09-05', '2026-09-19', '2026-09-18', FALSE, 'Bon état'),
                                                                                                                                   (5,  19, '2026-10-01', '2026-10-15', '2026-10-14', FALSE, 'Bon état'),
                                                                                                                                   (6,  3,  '2026-09-20', '2026-10-04', '2026-10-10', TRUE,  'Disque rayé'),
                                                                                                                                   (6,  10, '2026-10-05', '2026-10-19', '2026-10-19', FALSE, 'Bon état'),
                                                                                                                                   (7,  21, '2026-09-25', '2026-10-23', '2026-10-20', FALSE, 'Bon état'),
                                                                                                                                   (7,  12, '2026-10-12', '2026-10-26', NULL,         FALSE, NULL),
                                                                                                                                   (9,  6,  '2026-10-10', '2026-10-24', NULL,         FALSE, NULL),
                                                                                                                                   (9,  18, '2026-10-15', '2026-11-12', NULL,         FALSE, NULL),
                                                                                                                                   (10, 5,  '2026-10-20', '2026-11-03', NULL,         FALSE, NULL),
                                                                                                                                   (10, 13, '2026-10-25', '2026-11-08', NULL,         FALSE, NULL),
                                                                                                                                   (3,  20, '2026-10-28', '2026-11-11', NULL,         FALSE, NULL),
                                                                                                                                   (4,  7,  '2026-10-29', '2026-11-12', NULL,         TRUE,  NULL);


INSERT INTO reservation (date_reservation, id_utilisateur, id_media) VALUES
                                                                         ('2026-10-18', 5,  6),
                                                                         ('2026-10-22', 3,  6),
                                                                         ('2026-10-24', 4,  12),
                                                                         ('2026-10-27', 6,  5),
                                                                         ('2026-10-29', 7,  13),
                                                                         ('2026-10-30', 9,  22);