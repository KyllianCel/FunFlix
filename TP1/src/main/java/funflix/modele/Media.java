package funflix.model;

public abstract class Media {
    private int id;
    private String titre;
    private String description;
    private String genre;
    private String auteurEditeur;
    private int annee;
    private int ageMinimum;
    private int nbExemplaires;
    private int nbDisponibles;

    public Media() {
    }

    public Media(int id, String titre, String description, String genre, String auteurEditeur, int annee, int ageMinimum, int nbExemplaires, int nbDisponibles) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.genre = genre;
        this.auteurEditeur = auteurEditeur;
        this.annee = annee;
        this.ageMinimum = ageMinimum;
        this.nbExemplaires = nbExemplaires;
        this.nbDisponibles = nbDisponibles;
    }

    public boolean estDisponible() {
        return this.nbDisponibles > 0;
    }

    public void retirerExemplaire() {
        if (this.nbDisponibles > 0) {
            this.nbDisponibles--;
        }
    }

    public void rendreExemplaire() {
        if (this.nbDisponibles < this.nbExemplaires) {
            this.nbDisponibles++;
        }
    }

    public abstract String getType();

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAuteurEditeur() {
        return auteurEditeur;
    }

    public void setAuteurEditeur(String auteurEditeur) {
        this.auteurEditeur = auteurEditeur;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public int getAgeMinimum() {
        return ageMinimum;
    }

    public void setAgeMinimum(int ageMinimum) {
        this.ageMinimum = ageMinimum;
    }

    public int getNbExemplaires() {
        return nbExemplaires;
    }

    public void setNbExemplaires(int nbExemplaires) {
        this.nbExemplaires = nbExemplaires;
    }

    public int getNbDisponibles() {
        return nbDisponibles;
    }

    public void setNbDisponibles(int nbDisponibles) {
        this.nbDisponibles = nbDisponibles;
    }
}