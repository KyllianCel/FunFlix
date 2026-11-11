package funflix.dao;

import funflix.bdd.ConnexionBD;
import funflix.bdd.DaoException;
import funflix.modele.Emprunt;
import funflix.modele.Reservation;
import funflix.modele.StatutReservation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDao implements Dao<Reservation> {

    @Override
    public Optional<Reservation> trouver(int id) {
        String sql = "SELECT * FROM reservation WHERE id_reservation = ?";
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(c, rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Lecture de la réservation " + id + " impossible", e);
        }
    }

    @Override
    public List<Reservation> trouverTous() {
        String sql = "SELECT * FROM reservation ORDER BY date_reservation DESC";
        List<Reservation> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultat.add(mapper(c, rs));
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des réservations impossible", e);
        }
    }

    public List<Reservation> trouverParUtilisateur(int idUtilisateur) {
        String sql = "SELECT r.* FROM reservation r " +
                     "JOIN co_reservataire cr ON r.id_reservation = cr.id_reservation " +
                     "WHERE cr.id_utilisateur = ? ORDER BY r.date_reservation DESC";
        List<Reservation> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultat.add(mapper(c, rs));
                }
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des réservations de l'utilisateur impossible", e);
        }
    }

    @Override
    public int creer(Reservation r) {
        try (Connection c = ConnexionBD.get()) {
            boolean autoCommitInitial = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                int idGenere = creer(c, r);
                c.commit();
                return idGenere;
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(autoCommitInitial);
            }
        } catch (SQLException ex) {
            throw new DaoException("Création de la réservation impossible", ex);
        }
    }

    public int creer(Connection c, Reservation r) {
        if (r.getIdUtilisateurs() == null || r.getIdUtilisateurs().isEmpty()) {
            throw new IllegalArgumentException("Une réservation doit concerner au moins un utilisateur (1..*).");
        }

        String sqlRes = "INSERT INTO reservation (date_reservation, statut, id_media) VALUES (?, ?, ?)";
        String sqlCoRes = "INSERT INTO co_reservataire (id_reservation, id_utilisateur) VALUES (?, ?)";

        try (PreparedStatement ps = c.prepareStatement(sqlRes, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(r.getDateReservation()));
            StatutReservation statut = r.getStatut() != null ? r.getStatut() : StatutReservation.EN_ATTENTE;
            ps.setString(2, statut.name());
            ps.setInt(3, r.getIdMedia());
            ps.executeUpdate();

            int idReservation;
            try (ResultSet cles = ps.getGeneratedKeys()) {
                if (!cles.next()) {
                    throw new SQLException("Échec de récupération de l'ID de réservation généré.");
                }
                idReservation = cles.getInt(1);
            }

            try (PreparedStatement psCo = c.prepareStatement(sqlCoRes)) {
                for (int idUser : r.getIdUtilisateurs()) {
                    psCo.setInt(1, idReservation);
                    psCo.setInt(2, idUser);
                    psCo.addBatch();
                }
                psCo.executeBatch();
            }

            return idReservation;
        } catch (SQLException ex) {
            throw new DaoException("Création de la réservation et des co-réservataires impossible", ex);
        }
    }

    public void mettreAJourStatut(Connection c, int idReservation, StatutReservation nouveauStatut) {
        String sql = "UPDATE reservation SET statut = ? WHERE id_reservation = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut.name());
            ps.setInt(2, idReservation);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Mise à jour du statut de la réservation impossible", ex);
        }
    }

    public int transformerEnEmprunt(int idReservation, LocalDate dateRetourPrevue, EmpruntDao empruntDao) {
        try (Connection c = ConnexionBD.get()) {
            boolean autoCommitInitial = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                Reservation res = trouver(idReservation)
                        .orElseThrow(() -> new DaoException("Réservation introuvable : " + idReservation, null));

                Emprunt nouvelEmprunt = new Emprunt(
                        0,
                        res.getIdUtilisateurs(),
                        res.getIdMedia(),
                        LocalDate.now(),
                        dateRetourPrevue,
                        null,
                        false,
                        null,
                        idReservation
                );

                int idEmprunt = empruntDao.creer(c, nouvelEmprunt);
                mettreAJourStatut(c, idReservation, StatutReservation.HONOREE);

                c.commit();
                return idEmprunt;
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(autoCommitInitial);
            }
        } catch (SQLException ex) {
            throw new DaoException("Transformation de la réservation en emprunt impossible", ex);
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM reservation WHERE id_reservation = ?";
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression de la réservation impossible", e);
        }
    }

    private static List<Integer> trouverCoReservataires(Connection c, int idReservation) throws SQLException {
        String sql = "SELECT id_utilisateur FROM co_reservataire WHERE id_reservation = ?";
        List<Integer> utilisateurs = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idReservation);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(rs.getInt("id_utilisateur"));
                }
            }
        }
        return utilisateurs;
    }

    private static Reservation mapper(Connection c, ResultSet rs) throws SQLException {
        int idReservation = rs.getInt("id_reservation");
        List<Integer> coReservataires = trouverCoReservataires(c, idReservation);

        return new Reservation(
                idReservation,
                rs.getDate("date_reservation").toLocalDate(),
                StatutReservation.valueOf(rs.getString("statut")),
                coReservataires,
                rs.getInt("id_media")
        );
    }
}