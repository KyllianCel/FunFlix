package funflix.modele;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private int idUtilisateur;
    private int idMedia;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private boolean prolonge;
    private String etatRetour;

    public Emprunt(int id, int idUtilisateur, int idMedia, LocalDate dateEmprunt,
                   LocalDate dateRetourPrevue, LocalDate dateRetourEffective,
                   boolean prolonge, String etatRetour) {
        if (dateRetourPrevue.isBefore(dateEmprunt)) {
            throw new IllegalArgumentException("La date de retour prévue ne peut pas précéder la date d'emprunt.");
        }
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idMedia = idMedia;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
        this.prolonge = prolonge;
        this.etatRetour = etatRetour;
    }

    public boolean estEnRetard() {
        return this.dateRetourEffective == null && LocalDate.now().isAfter(this.dateRetourPrevue);
    }

    public StatutEmprunt getStatut() {
        if (this.dateRetourEffective != null) {
            return StatutEmprunt.RENDU;
        }
        return estEnRetard() ? StatutEmprunt.EN_RETARD : StatutEmprunt.EN_COURS;
    }

    public void prolonger(int jours) {
        if (this.prolonge) {
            throw new IllegalStateException("C-05 : Cet emprunt a déjà été prolongé une fois.");
        }
        if (this.dateRetourEffective != null) {
            throw new IllegalStateException("Impossible de prolonger un emprunt déjà rendu.");
        }
        if (jours <= 0) {
            throw new IllegalArgumentException("Le nombre de jours de prolongation doit être positif.");
        }
        this.dateRetourPrevue = this.dateRetourPrevue.plusDays(jours);
        this.prolonge = true;
    }

    public void cloturer(String etat) {
        this.dateRetourEffective = LocalDate.now();
        this.etatRetour = etat;
    }

    // Getters
    public int getId() { return id; }
    public int getIdUtilisateur() { return idUtilisateur; }
    public int getIdMedia() { return idMedia; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public boolean isProlonge() { return prolonge; }
    public String getEtatRetour() { return etatRetour; }

    // Setters
    public void setId(int id) { this.id = id; }
}