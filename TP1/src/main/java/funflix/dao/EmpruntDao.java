package funflix.dao;

import funflix.bdd.ConnexionBD;
import funflix.bdd.DaoException;
import funflix.modele.Emprunt;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpruntDao implements Dao<Emprunt> {

    @Override
    public Optional<Emprunt> trouver(int id) {
        String sql = "SELECT * FROM emprunt WHERE id = ?";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(rs)) : Optional.empty();
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
                resultat.add(mapper(rs));
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des emprunts impossible", e);
        }
    }

    public List<Emprunt> trouverEnCours(int idUtilisateur) {
        String sql = "SELECT * FROM emprunt WHERE id_utilisateur = ? AND date_retour_effective IS NULL";
        List<Emprunt> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultat.add(mapper(rs));
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
            return creer(c, e);
        } catch (SQLException ex) {
            throw new DaoException("Création de l'emprunt impossible", ex);
        }
    }

    public int creer(Connection c, Emprunt e) {
        String sql = "INSERT INTO emprunt (id_utilisateur, id_media, date_emprunt, date_retour_prevue, prolonge, etat_retour) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getIdUtilisateur());
            ps.setInt(2, e.getIdMedia());
            ps.setDate(3, Date.valueOf(e.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(e.getDateRetourPrevue()));
            ps.setBoolean(5, e.isProlonge());
            ps.setString(6, e.getEtatRetour());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                cles.next();
                return cles.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DaoException("Création de l'emprunt impossible", ex);
        }
    }

    public void enregistrerRetour(Connection c, int idEmprunt, LocalDate dateRetour) {
        String sql = "UPDATE emprunt SET date_retour_effective = ? WHERE id = ?";
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
        String sql = "DELETE FROM emprunt WHERE id = ?";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression de l'emprunt impossible", e);
        }
    }

    private static Emprunt mapper(ResultSet rs) throws SQLException {
        Date retour = rs.getDate("date_retour_effective");
        return new Emprunt(
                rs.getInt("id"),
                rs.getInt("id_utilisateur"),
                rs.getInt("id_media"),
                rs.getDate("date_emprunt").toLocalDate(),
                rs.getDate("date_retour_prevue").toLocalDate(),
                retour != null ? retour.toLocalDate() : null,
                rs.getBoolean("prolonge"),
                rs.getString("etat_retour")
        );
    }
}