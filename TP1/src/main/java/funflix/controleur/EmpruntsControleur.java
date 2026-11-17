package funflix.controleur;

import static funflix.controleur.NavigationControleur.etiquette;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import funflix.modele.EmpruntsDemo;
import funflix.modele.EmpruntsDemo.EnCours;
import funflix.modele.EmpruntsDemo.Termine;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class EmpruntsControleur {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter JOUR_MOIS = DateTimeFormatter.ofPattern("dd/MM");

    @FXML private Label description;
    @FXML private VBox liste;
    @FXML private VBox historique;

    public VBox creer() {
        VBox racine = ChargeurVue.charger("emprunts.fxml", this);
        description.setText("Jusqu’à " + EmpruntsDemo.LIMITE + " médias en même temps, pour 3 semaines.");
        racine.getChildren().add(1, resume());
        for (EnCours emprunt : EmpruntsDemo.EN_COURS) {
            liste.getChildren().add(ligne(emprunt));
        }
        historique.getChildren().add(historique());
        return racine;
    }

    private HBox resume() {
        String prochainRetour = EmpruntsDemo.EN_COURS.stream().map(EnCours::retourPrevu)
            .min(LocalDate::compareTo).map(JOUR_MOIS::format).orElse("—");
        return new HBox(14,
            compteur("En cours", EmpruntsDemo.EN_COURS.size() + " / " + EmpruntsDemo.LIMITE, "valeur-bleue"),
            compteur("Prochain retour", prochainRetour, "valeur-orange"),
            compteur("Emprunts terminés", Integer.toString(EmpruntsDemo.HISTORIQUE.size()), "valeur-neutre"));
    }

    private VBox compteur(String libelle, String valeur, String couleur) {
        Label nombre = etiquette(valeur, "valeur-emprunts");
        nombre.getStyleClass().add(couleur);
        VBox carte = new VBox(4, etiquette(libelle, "libelle-emprunt"), nombre);
        carte.getStyleClass().addAll("panneau-compte", "compteur-emprunts");
        carte.setPrefWidth(0);
        HBox.setHgrow(carte, Priority.ALWAYS);
        return carte;
    }

    private HBox ligne(EnCours emprunt) {
        Region couverture = new Region();
        couverture.getStyleClass().addAll("mini-couverture", "couverture-emprunt");
        couverture.setStyle("-fx-background-color: " + emprunt.couleur() + ";");

        VBox media = new VBox(4, etiquette(emprunt.titre(), "titre-ligne"),
            etiquette(emprunt.categorie() + " · " + emprunt.auteur(), "sous-titre-ligne"));
        HBox.setHgrow(media, Priority.ALWAYS);

        long jours = ChronoUnit.DAYS.between(EmpruntsDemo.DATE_MAQUETTE, emprunt.retourPrevu());
        boolean retourProche = jours >= 0 && jours <= 3;
        Label statut = etiquette(retourProche ? "À rendre dans " + jours + " jours" : "En cours", "statut-media");
        if (retourProche) {
            statut.getStyleClass().add("statut-indisponible");
        }
        HBox statutEmprunt = new HBox(statut);
        statutEmprunt.setMinWidth(125);
        statutEmprunt.setAlignment(Pos.CENTER_LEFT);

        Button prolonger = new Button("Prolonger");
        prolonger.getStyleClass().addAll("reserver", "prolonger");
        HBox ligne = new HBox(12, couverture, media, date("Emprunté le", emprunt.debut()),
            date("Retour prévu", emprunt.retourPrevu()), statutEmprunt, prolonger);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.getStyleClass().addAll("panneau-compte", "ligne-emprunt");
        return ligne;
    }

    private VBox date(String libelle, LocalDate date) {
        VBox colonne = new VBox(3, etiquette(libelle, "libelle-emprunt"),
            etiquette(DATE.format(date), "date-emprunt"));
        colonne.setMinWidth(96);
        return colonne;
    }

    private VBox historique() {
        VBox tableau = new VBox(ligneHistorique("Média", "Catégorie", "Emprunté le", "Rendu le", true));
        tableau.getStyleClass().add("historique-emprunts");
        for (Termine emprunt : EmpruntsDemo.HISTORIQUE) {
            tableau.getChildren().add(ligneHistorique(emprunt.titre(), emprunt.categorie(),
                DATE.format(emprunt.debut()), DATE.format(emprunt.retour()), false));
        }
        return tableau;
    }

    private GridPane ligneHistorique(String media, String categorie, String debut, String retour, boolean entete) {
        GridPane ligne = new GridPane();
        for (double largeur : new double[] {33, 22, 18, 27}) {
            ColumnConstraints colonne = new ColumnConstraints();
            colonne.setPercentWidth(largeur);
            ligne.getColumnConstraints().add(colonne);
        }
        ligne.getStyleClass().add(entete ? "entete-historique" : "ligne-historique");
        ligne.addRow(0, etiquette(media, "media-historique"),
            etiquette(categorie, "categorie-historique"), new Label(debut), new Label(retour));
        return ligne;
    }
}
