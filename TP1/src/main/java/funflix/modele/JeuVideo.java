package funflix.modele;

public class JeuVideo extends Media {
    private String plateforme;

    public JeuVideo(int id, String titre, String description, String genre,
                    String auteurEditeur, int annee, int ageMinimum, int nbExemplaires, String plateforme) {
        super(id, titre, description, genre, auteurEditeur, annee, ageMinimum, nbExemplaires);
        this.plateforme = plateforme;
    }

    @Override
    public String getType() {
        return TypeMedia.JEU_VIDEO.name();
    }

    public String getPlateforme() { return plateforme; }
    public void setPlateforme(String plateforme) { this.plateforme = plateforme; }
}