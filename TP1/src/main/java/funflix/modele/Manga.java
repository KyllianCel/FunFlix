package funflix.modele;

public class Manga extends Media {
    
    public Manga(int id, String titre, String description, String genre, 
                 String auteurEditeur, int annee, int ageMinimum, 
                 int nbExemplaires) {
        super(id, titre, description, genre, auteurEditeur, annee, ageMinimum, nbExemplaires);
    }

    @Override
    public String getType() {
        return "MANGA";
    }
}