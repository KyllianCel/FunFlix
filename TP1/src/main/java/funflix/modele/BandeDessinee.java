package funflix.modele;

public class BandeDessinee extends Media {
    
    public BandeDessinee(int id, String titre, String description, String genre, 
                         String auteurEditeur, int annee, int ageMinimum, 
                         int nbExemplaires) {
        super(id, titre, description, genre, auteurEditeur, annee, ageMinimum, nbExemplaires);
    }

    @Override
    public String getType() {
        return TypeMedia.BANDE_DESSINEE.name();
    }
}