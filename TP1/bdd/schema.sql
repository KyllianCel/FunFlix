CREATE DATABASE IF NOT EXISTS base_projet;
USE base_projet;

DROP TABLE IF EXISTS emprunt;
DROP TABLE IF EXISTS media;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS Type_media;
DROP TABLE IF EXISTS jeu_video;
DROP TABLE IF EXISTS jeu_societe;

CREATE TABLE utilisateur (
                             id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
                             nom VARCHAR(50) NOT NULL,
                             prenom VARCHAR(50) NOT NULL,
                             email VARCHAR(255) NOT NULL UNIQUE,
                             mot_de_passe_hash VARCHAR(255) NOT NULL,
                             telephone VARCHAR(20),
                             role VARCHAR(10) NOT NULL CHECK ( role IN ('Administrateur','Client')),
                             actif BOOLEAN NOT NULL,
                             date_inscription DATE NOT NULL
);

CREATE TABLE Type_media (
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
                       id_type INT not null,
                    FOREIGN KEY (id_type) references Type_media(id_type)
);

CREATE TABLE emprunt (
                         id_emprunt INT AUTO_INCREMENT PRIMARY KEY,
                         id_utilisateur INT NOT NULL,
                         id_media INT NOT NULL,
                         date_emprunt DATE NOT NULL,
                         date_retour_prevue DATE NOT NULL,
                         date_retour_effective DATE,
                         prolonge BOOLEAN NOT NULL,
                         etat_retour VARCHAR(255),
                         FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur),
                         FOREIGN KEY (id_media) REFERENCES media(id_media)
);

CREATE TABLE reservation (
                             id_reservattion INT AUTO_INCREMENT PRIMARY KEY,
                             date_reservation DATE NOT NULL,
                             id_utilisateur int not null,
                             id_media int not null,
                            FOREIGN KEY (id_utilisateur) references utilisateur(id_utilisateur),
                            FOREIGN KEY (id_media) references media(id_media)

);

CREATE TABLE jeu_video (
        id INT primary key,
        plateforme VARCHAR (50) CHECK ( plateforme in ('pc','ps5','ps4','switch','xbox') ),
        foreign key (id) references media(id_media)
);

CREATE TABLE jeu_societe (
    id int primary key,
    nb_personnes INT NOT NULL,
    FOREIGN KEY (id) references media(id_media)
);