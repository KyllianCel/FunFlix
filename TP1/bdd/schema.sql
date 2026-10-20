/*CREATE DATABASE IF NOT EXISTS base_projet;
USE base_projet; */

DROP TABLE IF EXISTS emprunt;
DROP TABLE IF EXISTS media;
DROP TABLE IF EXISTS type_media;
DROP TABLE IF EXISTS utilisateur;

CREATE TABLE utilisateur (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL,
                             prenom VARCHAR(100) NOT NULL,
                             email VARCHAR(255) NOT NULL UNIQUE,
                             mot_de_passe_hash VARCHAR(255) NOT NULL,
                             telephone VARCHAR(20),
                             role VARCHAR(10) NOT NULL,
                             actif BOOLEAN NOT NULL,
                             date_inscription DATE NOT NULL
);

CREATE TABLE type_media (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            libelle VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE media (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       titre VARCHAR(255) NOT NULL,
                       description TEXT,
                       genre VARCHAR(100),
                       auteur_editeur VARCHAR(255),
                       annee SMALLINT,
                       age_minimum TINYINT,
                       plateforme VARCHAR(100),
                       nb_joueurs VARCHAR(20),
                       nb_exemplaires INT NOT NULL,
                       id_type INT NOT NULL,
                       FOREIGN KEY (id_type) REFERENCES type_media(id)
);

CREATE TABLE emprunt (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         id_utilisateur INT NOT NULL,
                         id_media INT NOT NULL,
                         date_emprunt DATE NOT NULL,
                         date_retour_prevue DATE NOT NULL,
                         date_retour_effective DATE,
                         prolonge BOOLEAN NOT NULL,
                         etat_retour VARCHAR(255),
                         FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id),
                         FOREIGN KEY (id_media) REFERENCES media(id)
);
