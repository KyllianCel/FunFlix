CREATE DATABASE IF NOT EXISTS ludotheque;
USE ludotheque;

DROP TABLE IF EXISTS co_emprunteur;
DROP TABLE IF EXISTS co_reservataire;
DROP TABLE IF EXISTS jeu_societe;
DROP TABLE IF EXISTS jeu_video;
DROP TABLE IF EXISTS emprunt;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS media;
DROP TABLE IF EXISTS type_media;
DROP TABLE IF EXISTS utilisateur;

CREATE TABLE utilisateur (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    telephone VARCHAR(20),
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN','ADHERENT')),
    actif BOOLEAN NOT NULL,
    date_inscription DATE NOT NULL
);

CREATE TABLE type_media (
    id_type INT AUTO_INCREMENT PRIMARY KEY,
    Libelle VARCHAR(50) NOT NULL
);

CREATE TABLE media (
    id_media INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(100),
    auteur_editeur VARCHAR(255),
    annee YEAR,
    age_minimum INT,
    nb_exemplaires INT NOT NULL,
    id_type INT NOT NULL,
    FOREIGN KEY (id_type) REFERENCES type_media(id_type)
);

CREATE TABLE reservation (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    date_reservation DATE NOT NULL,
    statut VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE', 'DISPONIBLE', 'HONOREE', 'ANNULEE')),
    id_media INT NOT NULL,
    FOREIGN KEY (id_media) REFERENCES media(id_media)
);

CREATE TABLE emprunt (
    id_emprunt INT AUTO_INCREMENT PRIMARY KEY,
    id_media INT NOT NULL,
    date_emprunt DATE NOT NULL,
    date_retour_prevue DATE NOT NULL,
    date_retour_effective DATE,
    prolonge BOOLEAN NOT NULL,
    etat_retour VARCHAR(255),
    id_reservation INT NULL,
    FOREIGN KEY (id_media) REFERENCES media(id_media),
    FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation)
);

CREATE TABLE co_emprunteur (
    id_emprunt INT NOT NULL,
    id_utilisateur INT NOT NULL,
    PRIMARY KEY (id_emprunt, id_utilisateur),
    FOREIGN KEY (id_emprunt) REFERENCES emprunt(id_emprunt) ON DELETE CASCADE,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE
);

CREATE TABLE co_reservataire (
    id_reservation INT NOT NULL,
    id_utilisateur INT NOT NULL,
    PRIMARY KEY (id_reservation, id_utilisateur),
    FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation) ON DELETE CASCADE,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE
);

CREATE TABLE jeu_video (
    id INT PRIMARY KEY,
    plateforme VARCHAR(50) CHECK (plateforme IN ('pc','ps5','ps4','switch','xbox')),
    FOREIGN KEY (id) REFERENCES media(id_media)
);

CREATE TABLE jeu_societe (
    id INT PRIMARY KEY,
    nb_personnes INT NOT NULL,
    FOREIGN KEY (id) REFERENCES media(id_media)
);

-- INDEX
CREATE INDEX idx_media_type_genre ON media(id_type, genre);
CREATE INDEX idx_emprunt_media_actif ON emprunt(id_media, date_retour_effective);
CREATE INDEX idx_co_emprunteur_user ON co_emprunteur(id_utilisateur);

-- VUES

-- 1. Vue pour afficher le catalogue avec le calcul dynamique des stocks
CREATE OR REPLACE VIEW vue_media_disponibilite AS
SELECT 
    m.id_media,
    m.titre,
    t.Libelle AS type_media,
    m.nb_exemplaires,
    (m.nb_exemplaires - COALESCE(COUNT(e.id_emprunt), 0)) AS nb_disponibles
FROM media m
JOIN type_media t ON m.id_type = t.id_type
LEFT JOIN emprunt e ON m.id_media = e.id_media AND e.date_retour_effective IS NULL
GROUP BY m.id_media, m.titre, t.Libelle, m.nb_exemplaires;

-- 2. Vue pour identifier immédiatement les adhérents en retard
CREATE OR REPLACE VIEW vue_emprunts_en_retard AS
SELECT 
    e.id_emprunt,
    u.nom,
    u.prenom,
    u.email,
    m.titre,
    e.date_retour_prevue,
    DATEDIFF(CURRENT_DATE, e.date_retour_prevue) AS jours_de_retard
FROM emprunt e
JOIN co_emprunteur ce ON e.id_emprunt = ce.id_emprunt
JOIN utilisateur u ON ce.id_utilisateur = u.id_utilisateur
JOIN media m ON e.id_media = m.id_media
WHERE e.date_retour_effective IS NULL 
AND e.date_retour_prevue < CURRENT_DATE;

-- TRIGGERS

DELIMITER //

-- 1. Trigger : Bloquer si l'utilisateur a déjà 3 emprunts en cours
CREATE TRIGGER trg_verif_limite_emprunts
BEFORE INSERT ON co_emprunteur
FOR EACH ROW
BEGIN
    DECLARE v_nb_emprunts INT;
    
    SELECT COUNT(*) INTO v_nb_emprunts 
    FROM co_emprunteur ce
    JOIN emprunt e ON ce.id_emprunt = e.id_emprunt
    WHERE ce.id_utilisateur = NEW.id_utilisateur 
    AND e.date_retour_effective IS NULL;
    
    IF v_nb_emprunts >= 3 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Erreur : L''utilisateur a déjà atteint la limite de 3 emprunts en cours.';
    END IF;
END //

-- 2. Trigger : Bloquer si le compte de l'utilisateur est inactif
CREATE TRIGGER trg_verif_compte_actif
BEFORE INSERT ON co_emprunteur
FOR EACH ROW
BEGIN
    DECLARE v_actif BOOLEAN;
    
    SELECT actif INTO v_actif 
    FROM utilisateur 
    WHERE id_utilisateur = NEW.id_utilisateur;
    
    IF v_actif = FALSE THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Erreur : Ce compte utilisateur est désactivé.';
    END IF;
END //

DELIMITER ;