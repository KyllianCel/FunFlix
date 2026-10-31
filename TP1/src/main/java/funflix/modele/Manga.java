package funflix.model;

public class Manga extends Media {

    public Manga() {
        super();
    }

    public Manga(int id, String titre, String description, String genre, String auteurEditeur, int annee, int ageMinimum, int nbExemplaires, int nbDisponibles) {
        super(id, titre, description, genre, auteurEditeur, annee, ageMinimum, nbExemplaires, nbDisponibles);
    }

    @Override
    public String getType() {
        return "Manga";
    }
}