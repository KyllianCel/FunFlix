package funflix.model;

public class BandeDessinee extends Media {

    public BandeDessinee() {
        super();
    }

    public BandeDessinee(int id, String titre, String description, String genre, String auteurEditeur, int annee, int ageMinimum, int nbExemplaires, int nbDisponibles) {
        super(id, titre, description, genre, auteurEditeur, annee, ageMinimum, nbExemplaires, nbDisponibles);
    }

    @Override
    public String getType() {
        return "Bande dessinée";
    }
}