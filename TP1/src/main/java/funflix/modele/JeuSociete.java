package funflix.model;

public class JeuSociete extends Media {
    private String nbJoueurs;

    public JeuSociete() {
        super();
    }

    public JeuSociete(int id, String titre, String description, String genre, String auteurEditeur, int annee, int ageMinimum, int nbExemplaires, int nbDisponibles, String nbJoueurs) {
        super(id, titre, description, genre, auteurEditeur, annee, ageMinimum, nbExemplaires, nbDisponibles);
        this.nbJoueurs = nbJoueurs;
    }

    @Override
    public String getType() {
        return "Jeu de société";
    }

    public String getNbJoueurs() {
        return nbJoueurs;
    }

    public void setNbJoueurs(String nbJoueurs) {
        this.nbJoueurs = nbJoueurs;
    }
}