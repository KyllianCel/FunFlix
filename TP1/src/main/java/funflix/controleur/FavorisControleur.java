package funflix.controleur;

import static funflix.controleur.NavigationControleur.etiquette;

import java.util.List;

import funflix.modele.FicheCatalogue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class FavorisControleur {
    @FXML private Label compteur;
    @FXML private VBox liste;

    public VBox creer(List<FicheCatalogue> favoris) {
        VBox racine = ChargeurVue.charger("favoris.fxml", this);
        compteur.setText(favoris.size() + (favoris.size() <= 1 ? " média favori" : " médias favoris"));
        for (FicheCatalogue fiche : favoris) {
            liste.getChildren().add(ligne(fiche));
        }
        return racine;
    }

    private HBox ligne(FicheCatalogue fiche) {
        Region couverture = new Region();
        couverture.getStyleClass().add("mini-couverture");
        couverture.setStyle("-fx-background-color: " + fiche.couleur() + ";");
        VBox details = new VBox(5, etiquette(fiche.titre(), "titre-ligne"),
                               etiquette(fiche.categorie() + " · " + fiche.auteur(), "sous-titre-ligne"));
        HBox.setHgrow(details, Priority.ALWAYS);

        boolean disponible = fiche.disponible();
        Label statut = etiquette(disponible ? "Disponible" : "Indisponible", "statut-media");
        if (!disponible) {
            statut.getStyleClass().add("statut-indisponible");
        }
        HBox ligne = new HBox(18, couverture, details, statut);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.getStyleClass().add("panneau-compte");
        return ligne;
    }
}
