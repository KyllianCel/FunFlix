package funflix.modele;

public class JeuSociete extends Media {
    private String nbJoueurs;

    public JeuSociete(int id, String titre, String description, String genre, 
                      String auteurEditeur, int annee, int ageMinimum, 
                      int nbExemplaires, String nbJoueurs) {
        super(id, titre, description, genre, auteurEditeur, annee, ageMinimum, nbExemplaires);
        this.nbJoueurs = nbJoueurs;
    }

    @Override
    public String getType() {
        return "JEU_SOCIETE";
    }

    public String getNbJoueurs() { return nbJoueurs; }
    public void setNbJoueurs(String nbJoueurs) { this.nbJoueurs = nbJoueurs; }
}