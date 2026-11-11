package funflix.service;

import funflix.bdd.ConnexionBD;
import funflix.dao.EmpruntDao;
import funflix.modele.Emprunt;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class EmpruntService {

    private final EmpruntDao empruntDao;

    public EmpruntService(EmpruntDao empruntDao) {
        this.empruntDao = empruntDao;
    }

    public void emprunter(int idUtilisateur, int idMedia, int dureeJours) throws Exception {
        emprunter(List.of(idUtilisateur), idMedia, dureeJours, null);
    }

    public void emprunter(List<Integer> idUtilisateurs, int idMedia, int dureeJours, Integer idReservation) throws Exception {
        if (dureeJours <= 0 || dureeJours > 15) {
            throw new IllegalArgumentException("La durée d'emprunt doit être comprise entre 1 et 15 jours.");
        }
        if (idUtilisateurs == null || idUtilisateurs.isEmpty()) {
            throw new IllegalArgumentException("Un emprunt doit comporter au moins un emprunteur.");
        }

        try (Connection conn = ConnexionBD.get()) {
            boolean autoCommitInitial = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                for (int idUtilisateur : idUtilisateurs) {
                    // Vérifier si l'utilisateur est actif
                    String sqlUser = "SELECT actif FROM utilisateur WHERE id_utilisateur = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                        ps.setInt(1, idUtilisateur);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next() || !rs.getBoolean("actif")) {
                                throw new IllegalStateException("Le compte utilisateur ID " + idUtilisateur + " est inactif or inexistant.");
                            }
                        }
                    }

                    // Vérifier la limite de 3 emprunts en cours via co_emprunteur
                    String sqlCountUser = "SELECT COUNT(*) FROM emprunt e " +
                                          "JOIN co_emprunteur ce ON e.id_emprunt = ce.id_emprunt " +
                                          "WHERE ce.id_utilisateur = ? AND e.date_retour_effective IS NULL";
                    try (PreparedStatement ps = conn.prepareStatement(sqlCountUser)) {
                        ps.setInt(1, idUtilisateur);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next() && rs.getInt(1) >= 3) {
                                throw new IllegalStateException("L'utilisateur ID " + idUtilisateur + " a atteint la limite de 3 emprunts simultanés.");
                            }
                        }
                    }
                }

                // Vérifier les stocks du média avec verrouillage
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
                    throw new MediaIndisponibleException("Aucun exemplaire disponible pour le média id=" + idMedia);
                }

                // Créer l'objet Emprunt et utiliser le DAO transactionnel
                LocalDate aujourdhui = LocalDate.now();
                Emprunt nouvelEmprunt = new Emprunt(
                        0,
                        idUtilisateurs,
                        idMedia,
                        aujourdhui,
                        aujourdhui.plusDays(dureeJours),
                        null,
                        false,
                        null,
                        idReservation
                );

                empruntDao.creer(conn, nouvelEmprunt);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(autoCommitInitial);
            }
        }
    }

    public void rendre(int idEmprunt) throws Exception {
        try (Connection conn = ConnexionBD.get()) {
            empruntDao.enregistrerRetour(conn, idEmprunt, LocalDate.now());
        }
    }
}