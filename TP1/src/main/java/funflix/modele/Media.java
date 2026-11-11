package funflix.modele;

public abstract class Media {
    private int id;
    private String titre;
    private String description;
    private String genre;
    private String auteurEditeur;
    private int annee;
    private int ageMinimum;
    private int nbExemplaires;

    public Media(int id, String titre, String description, String genre,
                 String auteurEditeur, int annee, int ageMinimum, int nbExemplaires) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.genre = genre;
        this.auteurEditeur = auteurEditeur;
        this.annee = annee;
        this.ageMinimum = ageMinimum;
        this.nbExemplaires = nbExemplaires;
    }

    public boolean estDisponible(int nbEmpruntsEnCours) {
        return (this.nbExemplaires - nbEmpruntsEnCours) > 0;
    }

    public boolean estDisponible() {
        return this.nbExemplaires > 0;
    }

    public void retirerExemplaire() {
        if (this.nbExemplaires <= 0) {
            throw new IllegalStateException("Aucun exemplaire à retirer.");
        }
        this.nbExemplaires--;
    }

    public void rendreExemplaire() {
        this.nbExemplaires++;
    }

    public abstract String getType();

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public String getAuteurEditeur() { return auteurEditeur; }
    public int getAnnee() { return annee; }
    public int getAgeMinimum() { return ageMinimum; }
    public int getNbExemplaires() { return nbExemplaires; }
    public void setNbExemplaires(int nbExemplaires) { this.nbExemplaires = nbExemplaires; }
}