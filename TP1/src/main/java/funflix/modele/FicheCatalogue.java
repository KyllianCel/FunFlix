package funflix.modele;

public record FicheCatalogue(String categorie, String titre, String titreCouverture, String auteur,
                             String couleur, boolean texteClair, boolean disponible, String dateRetour) {
}
