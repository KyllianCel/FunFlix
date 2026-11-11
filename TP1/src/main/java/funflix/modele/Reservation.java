package funflix.modele;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private int id;
    private LocalDate dateReservation;
    private StatutReservation statut;
    private List<Integer> idUtilisateurs = new ArrayList<>();
    private int idMedia;

    public Reservation() {
    }

    public Reservation(int id, LocalDate dateReservation, StatutReservation statut, List<Integer> idUtilisateurs, int idMedia) {
        this.id = id;
        this.dateReservation = dateReservation;
        this.statut = statut;
        if (idUtilisateurs != null) {
            this.idUtilisateurs = new ArrayList<>(idUtilisateurs);
        }
        this.idMedia = idMedia;
    }

    public void annuler() {
        this.statut = StatutReservation.ANNULEE;
    }

    public void ajouterCoReservataire(int idUtilisateur) {
        if (!this.idUtilisateurs.contains(idUtilisateur)) {
            this.idUtilisateurs.add(idUtilisateur);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDate dateReservation) { this.dateReservation = dateReservation; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public List<Integer> getIdUtilisateurs() { return idUtilisateurs; }
    public void setIdUtilisateurs(List<Integer> idUtilisateurs) { this.idUtilisateurs = idUtilisateurs; }

    public int getIdMedia() { return idMedia; }
    public void setIdMedia(int idMedia) { this.idMedia = idMedia; }
}