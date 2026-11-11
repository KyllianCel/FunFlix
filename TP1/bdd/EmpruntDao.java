package funflix.dao;

import funflix.bdd.ConnexionBD;
import funflix.bdd.DaoException;
import funflix.modele.Emprunt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpruntDao implements Dao<Emprunt> {

    @Override
    public Optional<Emprunt> trouver(int id) {
        String sql = "SELECT * FROM emprunt WHERE id_emprunt = ?";
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(c, rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Lecture de l'emprunt " + id + " impossible", e);
        }
    }

    @Override
    public List<Emprunt> trouverTous() {
        String sql = "SELECT * FROM emprunt ORDER BY date_emprunt DESC";
        List<Emprunt> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultat.add(mapper(c, rs));
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des emprunts impossible", e);
        }
    }

    public List<Emprunt> trouverEnCours(int idUtilisateur) {
        String sql = "SELECT e.* FROM emprunt e " +
                     "JOIN co_emprunteur ce ON e.id_emprunt = ce.id_emprunt " +
                     "WHERE ce.id_utilisateur = ? AND e.date_retour_effective IS NULL";
        List<Emprunt> resultat = new ArrayList<>();
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
            throw new DaoException("Lecture des emprunts en cours impossible", e);
        }
    }

    @Override
    public int creer(Emprunt e) {
        try (Connection c = ConnexionBD.get()) {
            boolean autoCommitInitial = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                int idGenere = creer(c, e);
                c.commit();
                return idGenere;
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(autoCommitInitial);
            }
        } catch (SQLException ex) {
            throw new DaoException("Création de l'emprunt impossible", ex);
        }
    }

    public int creer(Connection c, Emprunt e) {
        if (e.getIdUtilisateurs() == null || e.getIdUtilisateurs().isEmpty()) {
            throw new IllegalArgumentException("Un emprunt doit concerner au moins un utilisateur (1..*).");
        }

        String sqlEmprunt = "INSERT INTO emprunt (id_media, date_emprunt, date_retour_prevue, prolonge, etat_retour, id_reservation) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlCoEmprunteur = "INSERT INTO co_emprunteur (id_emprunt, id_utilisateur) VALUES (?, ?)";

        try (PreparedStatement ps = c.prepareStatement(sqlEmprunt, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getIdMedia());
            ps.setDate(2, Date.valueOf(e.getDateEmprunt()));
            ps.setDate(3, Date.valueOf(e.getDateRetourPrevue()));
            ps.setBoolean(4, e.isProlonge());
            ps.setString(5, e.getEtatRetour());
            if (e.getIdReservation() != null) {
                ps.setInt(6, e.getIdReservation());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.executeUpdate();

            int idEmprunt;
            try (ResultSet cles = ps.getGeneratedKeys()) {
                if (!cles.next()) {
                    throw new SQLException("Échec de récupération de l'ID d'emprunt généré.");
                }
                idEmprunt = cles.getInt(1);
            }

            try (PreparedStatement psCo = c.prepareStatement(sqlCoEmprunteur)) {
                for (int idUser : e.getIdUtilisateurs()) {
                    psCo.setInt(1, idEmprunt);
                    psCo.setInt(2, idUser);
                    psCo.addBatch();
                }
                psCo.executeBatch();
            }

            return idEmprunt;
        } catch (SQLException ex) {
            throw new DaoException("Création de l'emprunt et des co-emprunteurs impossible", ex);
        }
    }

    public void enregistrerRetour(Connection c, int idEmprunt, LocalDate dateRetour) {
        String sql = "UPDATE emprunt SET date_retour_effective = ? WHERE id_emprunt = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dateRetour));
            ps.setInt(2, idEmprunt);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Enregistrement du retour impossible", ex);
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM emprunt WHERE id_emprunt = ?";
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression de l'emprunt impossible", e);
        }
    }

    private static List<Integer> trouverCoEmprunteurs(Connection c, int idEmprunt) throws SQLException {
        String sql = "SELECT id_utilisateur FROM co_emprunteur WHERE id_emprunt = ?";
        List<Integer> utilisateurs = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEmprunt);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(rs.getInt("id_utilisateur"));
                }
            }
        }
        return utilisateurs;
    }

    private static Emprunt mapper(Connection c, ResultSet rs) throws SQLException {
        int idEmprunt = rs.getInt("id_emprunt");
        Date retour = rs.getDate("date_retour_effective");
        Integer idReservation = (Integer) rs.getObject("id_reservation");
        List<Integer> coEmprunteurs = trouverCoEmprunteurs(c, idEmprunt);

        return new Emprunt(
                idEmprunt,
                coEmprunteurs,
                rs.getInt("id_media"),
                rs.getDate("date_emprunt").toLocalDate(),
                rs.getDate("date_retour_prevue").toLocalDate(),
                retour != null ? retour.toLocalDate() : null,
                rs.getBoolean("prolonge"),
                rs.getString("etat_retour"),
                idReservation
        );
    }
}