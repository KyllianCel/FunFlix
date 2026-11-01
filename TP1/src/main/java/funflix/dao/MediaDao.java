package funflix.dao;

import funflix.bdd.ConnexionBD;
import funflix.bdd.DaoException;
import funflix.modele.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaDao {

    private static final String SQL_SELECT_BASE =
        "SELECT m.*, t.Libelle AS type_libelle, jv.plateforme, js.nb_personnes " +
        "FROM media m " +
        "JOIN type_media t ON m.id_type = t.id_type " +
        "LEFT JOIN jeu_video jv ON m.id_media = jv.id " +
        "LEFT JOIN jeu_societe js ON m.id_media = js.id";

    public Media trouverParId(int id) {
        String sql = SQL_SELECT_BASE + " WHERE m.id_media = ?";
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

    public int compterDisponibles(Connection c, int idMedia) throws SQLException {
        String sql =
            "SELECT m.nb_exemplaires - COUNT(e.id_emprunt) AS disponibles " +
            "FROM media m " +
            "LEFT JOIN emprunt e ON m.id_media = e.id_media AND e.date_retour_effective IS NULL " +
            "WHERE m.id_media = ? " +
            "GROUP BY m.id_media, m.nb_exemplaires";

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

    private Media mapper(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_media");
        String titre = rs.getString("titre");
        String desc = rs.getString("description");
        String genre = rs.getString("genre");
        String auteur = rs.getString("auteur_editeur");
        int annee = rs.getInt("annee");
        int ageMin = rs.getInt("age_minimum");
        int nbEx = rs.getInt("nb_exemplaires");
        String type = rs.getString("type_libelle").toLowerCase();

        return switch (type) {
            case "jeu_video" -> new JeuVideo(id, titre, desc, genre, auteur, annee, ageMin, nbEx, rs.getString("plateforme"));
            case "jeu_societe" -> new JeuSociete(id, titre, desc, genre, auteur, annee, ageMin, nbEx, rs.getString("nb_personnes"));
            case "bd", "bande_dessinee" -> new BandeDessinee(id, titre, desc, genre, auteur, annee, ageMin, nbEx);
            case "manga", "livre", "dvd" -> new Manga(id, titre, desc, genre, auteur, annee, ageMin, nbEx);
            default -> throw new IllegalArgumentException("Type de média inconnu : " + type);
        };
    }
}