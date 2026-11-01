package funflix.service;

import funflix.bdd.ConnexionBD;
import funflix.dao.EmpruntDao;

import java.sql.*;
import java.time.LocalDate;

public class EmpruntService {

    private final EmpruntDao empruntDao;

    public EmpruntService(EmpruntDao empruntDao) {
        this.empruntDao = empruntDao;
    }

    public void emprunter(int idUtilisateur, int idMedia, int dureeJours) throws Exception {
        if (dureeJours <= 0 || dureeJours > 15) {
            throw new IllegalArgumentException("C-02 : La durée d'emprunt doit être comprise entre 1 et 15 jours.");
        }

        try (Connection conn = ConnexionBD.get()) {
            conn.setAutoCommit(false);
            try {
                String sqlUser = "SELECT actif FROM utilisateur WHERE id_utilisateur = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                    ps.setInt(1, idUtilisateur);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean("actif")) {
                            throw new IllegalStateException("C-04 : Le compte utilisateur est inactif ou inexistant.");
                        }
                    }
                }

                String sqlCountUser = "SELECT COUNT(*) FROM emprunt WHERE id_utilisateur = ? AND date_retour_effective IS NULL";
                try (PreparedStatement ps = conn.prepareStatement(sqlCountUser)) {
                    ps.setInt(1, idUtilisateur);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) >= 3) {
                            throw new IllegalStateException("C-01 : Limite de 3 emprunts simultanés atteinte.");
                        }
                    }
                }

                String sqlLockMedia = "SELECT nb_exemplaires FROM media WHERE id_media = ? FOR UPDATE";
                int nbExemplaires;
                try (PreparedStatement ps = conn.prepareStatement(sqlLockMedia)) {
                    ps.setInt(1, idMedia);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Média introuvable.");
                        }
                        nbExemplaires = rs.getInt("nb_exemplaires");
                    }
                }

                String sqlEmpruntsActifs = "SELECT COUNT(*) FROM emprunt WHERE id_media = ? AND date_retour_effective IS NULL";
                int nbEmpruntes = 0;
                try (PreparedStatement ps = conn.prepareStatement(sqlEmpruntsActifs)) {
                    ps.setInt(1, idMedia);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            nbEmpruntes = rs.getInt(1);
                        }
                    }
                }

                if ((nbExemplaires - nbEmpruntes) <= 0) {
                    throw new MediaIndisponibleException("C-03 : Aucun exemplaire disponible pour le média id=" + idMedia);
                }

                String sqlInsert = "INSERT INTO emprunt (id_utilisateur, id_media, date_emprunt, date_retour_prevue, prolonge) " +
                                   "VALUES (?, ?, ?, ?, FALSE)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                    LocalDate aujourdhui = LocalDate.now();
                    ps.setInt(1, idUtilisateur);
                    ps.setInt(2, idMedia);
                    ps.setDate(3, Date.valueOf(aujourdhui));
                    ps.setDate(4, Date.valueOf(aujourdhui.plusDays(dureeJours)));
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void rendre(int idEmprunt) throws Exception {
        try (Connection conn = ConnexionBD.get()) {
            empruntDao.enregistrerRetour(conn, idEmprunt, LocalDate.now());
        }
    }
}