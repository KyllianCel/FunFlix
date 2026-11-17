package funflix.modele;

import java.time.LocalDate;
import java.util.List;

public final class EmpruntsDemo {
    private EmpruntsDemo() {
    }

    public record EnCours(String titre, String categorie, String auteur, String couleur,
                          LocalDate debut, LocalDate retourPrevu) {
    }

    public record Termine(String titre, String categorie, LocalDate debut, LocalDate retour) {
    }

    public static final int LIMITE = 3;
    public static final LocalDate DATE_MAQUETTE = LocalDate.of(2026, 11, 16);
    public static final List<EnCours> EN_COURS = List.of(
        new EnCours("Mario Kart 8 Deluxe", "Jeu vidéo", "Nintendo", "#d92f3b",
                    LocalDate.of(2026, 10, 29), LocalDate.of(2026, 11, 19)),
        new EnCours("Astérix le Gaulois", "BD", "Goscinny & Uderzo", "#f1bd49",
                    LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 24))
    );
    public static final List<Termine> HISTORIQUE = List.of(
        new Termine("Catan", "Jeux de société", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 20)),
        new Termine("Frieren — Tome 1", "Mangas", LocalDate.of(2026, 9, 15), LocalDate.of(2026, 10, 2)),
        new Termine("Hades", "Jeux vidéo", LocalDate.of(2026, 8, 28), LocalDate.of(2026, 9, 16))
    );
}
