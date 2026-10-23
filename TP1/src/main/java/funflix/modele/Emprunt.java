package funflix.model;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private boolean prolonge;
    private StatutEmprunt statut;
    private String etatRetour;
    private Utilisateur utilisateur;
    private Media media;

    public Emprunt() {
    }

    public Emprunt(int id, LocalDate dateEmprunt, LocalDate dateRetourPrevue, LocalDate dateRetourEffective, boolean prolonge, StatutEmprunt statut, String etatRetour, Utilisateur utilisateur, Media media) {
        this.id = id;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
        this.prolonge = prolonge;
        this.statut = statut;
        this.etatRetour = etatRetour;
        this.utilisateur = utilisateur;
        this.media = media;
    }

    public boolean estEnRetard() {
        if (this.statut == StatutEmprunt.RENDU) {
            return false;
        }
        return LocalDate.now().isAfter(this.dateRetourPrevue);
    }

    public void prolonger(int jours) {
        if (!this.prolonge) {
            this.dateRetourPrevue = this.dateRetourPrevue.plusDays(jours);
            this.prolonge = true;
        }
    }

    public void cloturer(String etat) {
        this.dateRetourEffective = LocalDate.now();
        this.etatRetour = etat;
        this.statut = StatutEmprunt.RENDU;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public LocalDate getDateRetourEffective() {
        return dateRetourEffective;
    }

    public void setDateRetourEffective(LocalDate dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

    public boolean isProlonge() {
        return prolonge;
    }

    public void setProlonge(boolean prolonge) {
        this.prolonge = prolonge;
    }

    public StatutEmprunt getStatut() {
        return statut;
    }

    public void setStatut(StatutEmprunt statut) {
        this.statut = statut;
    }

    public String getEtatRetour() {
        return etatRetour;
    }

    public void setEtatRetour(String etatRetour) {
        this.etatRetour = etatRetour;
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