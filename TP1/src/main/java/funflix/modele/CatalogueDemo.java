package funflix.modele;

import java.util.List;

public final class CatalogueDemo {
    private CatalogueDemo() {
    }

    public static final List<FicheCatalogue> MEDIAS = List.of(
        fiche("Jeux vidéo", "Zelda : Tears of the Kingdom", "Zelda : Tears\nof the Kingdom",
                  "Nintendo", "#2a594c", true, null),
        fiche("Mangas", "One Piece — Tome 1", "One Piece —\nTome 1",
                  "Eiichirō Oda", "#e3593e", false, null),
        fiche("BD", "Astérix le Gaulois", "Astérix le\nGaulois",
                  "Goscinny & Uderzo", "#f1bd49", false, "24/11"),
        fiche("Jeux de société", "Catan", "Catan",
                  "Klaus Teuber", "#ae512d", true, null),
        fiche("Mangas", "Frieren — Tome 1", "Frieren — Tome\n1",
                  "Kanehito Yamada", "#384b85", true, null),
        fiche("Jeux vidéo", "Mario Kart 8 Deluxe", "Mario Kart 8\nDeluxe",
                  "Nintendo", "#d92f3b", true, "19/11")
    );

    private static FicheCatalogue fiche(String categorie, String titre, String titreCouverture, String auteur,
                                        String couleur, boolean texteClair, String dateRetour) {
        return new FicheCatalogue(categorie, titre, titreCouverture, auteur, couleur, texteClair,
            dateRetour == null, dateRetour);
    }

}
