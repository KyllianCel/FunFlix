package funflix.model;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private LocalDate dateReservation;
    private StatutReservation statut;
    private Utilisateur utilisateur;
    private Media media;

    public Reservation() {
    }

    public Reservation(int id, LocalDate dateReservation, StatutReservation statut, Utilisateur utilisateur, Media media) {
        this.id = id;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.utilisateur = utilisateur;
        this.media = media;
    }

    public void annuler() {
        this.statut = StatutReservation.ANNULEE;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }
}