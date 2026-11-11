package funflix.modele;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Emprunt {
    private int id;
    private List<Integer> idUtilisateurs = new ArrayList<>();
    private int idMedia;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private boolean prolonge;
    private String etatRetour;
    private Integer idReservation;

    public Emprunt(int id, List<Integer> idUtilisateurs, int idMedia, LocalDate dateEmprunt,
                   LocalDate dateRetourPrevue, LocalDate dateRetourEffective,
                   boolean prolonge, String etatRetour, Integer idReservation) {
        if (dateRetourPrevue.isBefore(dateEmprunt)) {
            throw new IllegalArgumentException("La date de retour prévue ne peut pas précéder la date d'emprunt.");
        }
        this.id = id;
        if (idUtilisateurs != null) {
            this.idUtilisateurs = new ArrayList<>(idUtilisateurs);
        }
        this.idMedia = idMedia;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
        this.prolonge = prolonge;
        this.etatRetour = etatRetour;
        this.idReservation = idReservation;
    }

    public Emprunt(int id, int idUtilisateur, int idMedia, LocalDate dateEmprunt,
                   LocalDate dateRetourPrevue, LocalDate dateRetourEffective,
                   boolean prolonge, String etatRetour) {
        this(id, List.of(idUtilisateur), idMedia, dateEmprunt, dateRetourPrevue, dateRetourEffective, prolonge, etatRetour, null);
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
            throw new IllegalStateException("Cet emprunt a déjà été prolongé une fois.");
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

    public void ajouterCoEmprunteur(int idUtilisateur) {
        if (!this.idUtilisateurs.contains(idUtilisateur)) {
            this.idUtilisateurs.add(idUtilisateur);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<Integer> getIdUtilisateurs() { return idUtilisateurs; }
    public void setIdUtilisateurs(List<Integer> idUtilisateurs) { this.idUtilisateurs = idUtilisateurs; }

    public int getIdMedia() { return idMedia; }
    public void setIdMedia(int idMedia) { this.idMedia = idMedia; }

    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public boolean isProlonge() { return prolonge; }
    public String getEtatRetour() { return etatRetour; }

    public Integer getIdReservation() { return idReservation; }
    public void setIdReservation(Integer idReservation) { this.idReservation = idReservation; }
}