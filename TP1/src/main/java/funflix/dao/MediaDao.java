package funflix.dao;

import funflix.bdd.ConnexionBD;
import funflix.bdd.DaoException;
import funflix.modele.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaDao {

    private static final String SQL_SELECT_BASE = """
        SELECT m.*, t.libelle AS type_libelle
        FROM media m
        JOIN type_media t ON m.id_type = t.id
        """;

    public Media trouverParId(int id) {
        String sql = SQL_SELECT_BASE + " WHERE m.id = ?";
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapper(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lecture média id=" + id, e);
        }
    }

    public List<Media> listerTous() {
        List<Media> liste = new ArrayList<>();
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(SQL_SELECT_BASE);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur listage des médias", e);
        }
        return liste;
    }

    /**
     * Calcule dynamiquement le nombre d'exemplaires disponibles (3FN : exemplaires - emprunts actifs).
     */
    public int compterDisponibles(Connection c, int idMedia) throws SQLException {
        String sql = """
            SELECT m.nb_exemplaires - COUNT(e.id) AS disponibles
            FROM media m
            LEFT JOIN emprunt e ON m.id = e.id_media AND e.date_retour_effective IS NULL
            WHERE m.id = ?
            GROUP BY m.id, m.nb_exemplaires
            """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMedia);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Média introuvable : id=" + idMedia);
                }
                return rs.getInt("disponibles");
            }
        }
    }

    // Factory qui instancie la bonne sous-classe selon la table type_media
    private Media mapper(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String titre = rs.getString("titre");
        String desc = rs.getString("description");
        String genre = rs.getString("genre");
        String auteur = rs.getString("auteur_editeur");
        int annee = rs.getInt("annee");
        int ageMin = rs.getInt("age_minimum");
        int nbEx = rs.getInt("nb_exemplaires");
        String type = rs.getString("type_libelle");

        return switch (type) {
            case "JEU_VIDEO" -> new JeuVideo(id, titre, desc, genre, auteur, annee, ageMin, nbEx, rs.getString("plateforme"));
            case "JEU_SOCIETE" -> new JeuSociete(id, titre, desc, genre, auteur, annee, ageMin, nbEx, rs.getString("nb_joueurs"));
            case "BANDE_DESSINEE" -> new BandeDessinee(id, titre, desc, genre, auteur, annee, ageMin, nbEx);
            case "MANGA" -> new Manga(id, titre, desc, genre, auteur, annee, ageMin, nbEx);
            default -> throw new IllegalArgumentException("Type de média inconnu : " + type);
        };
    }
}