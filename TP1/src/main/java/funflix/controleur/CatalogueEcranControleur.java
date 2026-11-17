package funflix.controleur;

import static funflix.controleur.NavigationControleur.etiquette;

import java.util.ArrayList;
import java.util.List;

import funflix.modele.FicheCatalogue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CatalogueEcranControleur {
    private static final double LARGEUR_CARTE = 126;

    private final List<FicheCatalogue> medias;

    public CatalogueEcranControleur(List<FicheCatalogue> medias) {
        this.medias = List.copyOf(medias);
    }

    @FXML private VBox vedette;
    @FXML private VBox catalogue;
    @FXML private Separator separation;
    @FXML private Label compteur;
    @FXML private HBox filtres;

    public VBox creer() {
        VBox racine = ChargeurVue.charger("catalogue.fxml", this);
        List<FicheCatalogue> jeuxVideo = medias.stream()
            .filter(fiche -> fiche.categorie().equals("Jeux vidéo")).limit(5).toList();
        if (jeuxVideo.isEmpty()) {
            racine.getChildren().removeAll(vedette, separation);
        } else {
            vedette.getChildren().add(grille(jeuxVideo, false));
        }
        compteur.setText(medias.size() + (medias.size() == 1 ? " média" : " médias"));

        List<String> categories = new ArrayList<>(List.of("Tous"));
        categories.addAll(medias.stream().map(FicheCatalogue::categorie).distinct().toList());
        for (String nom : categories) {
            Button filtre = bouton(nom, "filtre");
            if (nom.equals("Tous")) {
                filtre.getStyleClass().add("filtre-actif");
            }
            filtres.getChildren().add(filtre);
        }

        Node resultat = medias.isEmpty() ? etiquette("Aucun média dans le catalogue.", "compteur") : grille(medias, true);
        catalogue.getChildren().add(resultat);
        return racine;
    }

    private Node grille(List<FicheCatalogue> fiches, boolean afficherDetails) {
        FlowPane grille = new FlowPane(21, 24);
        grille.setPrefWrapLength(NavigationControleur.LARGEUR_CONTENU);
        for (FicheCatalogue fiche : fiches) {
            grille.getChildren().add(carte(fiche, afficherDetails));
        }
        return grille;
    }

    private Node carte(FicheCatalogue fiche, boolean afficherDetails) {
        Label categorie = etiquette(fiche.categorie(), "categorie-couverture");
        Label titreCouverture = etiquette(fiche.titreCouverture(), "titre-couverture");
        titreCouverture.setWrapText(true);
        VBox couverture = new VBox(4, categorie, titreCouverture);
        couverture.setAlignment(Pos.BOTTOM_LEFT);
        couverture.setMinHeight(189);
        couverture.getStyleClass().add("couverture");
        if (fiche.texteClair()) {
            couverture.getStyleClass().add("couverture-claire");
        }
        couverture.setStyle("-fx-background-color: " + fiche.couleur() + ";");

        VBox carte = new VBox(7, couverture);
        carte.setMinWidth(LARGEUR_CARTE);
        carte.setMaxWidth(LARGEUR_CARTE);
        if (afficherDetails) {
            Label titre = etiquette(fiche.titre(), "titre-media");
            titre.setWrapText(true);
            titre.setMinHeight(28);
            titre.setAlignment(Pos.TOP_LEFT);
            Label auteur = etiquette(fiche.auteur(), "auteur");
            auteur.setWrapText(true);
            auteur.setMaxWidth(Double.MAX_VALUE);
            auteur.setAlignment(Pos.TOP_CENTER);
            VBox details = new VBox(3, titre, auteur);
            details.setMinHeight(60);
            carte.getChildren().add(details);
        }

        boolean disponible = fiche.disponible();
        Button emprunter = bouton(disponible ? "Emprunter" : "Indisponible",
                                  disponible ? "emprunter" : "indisponible");
        Button reserver = bouton("Réserver", "reserver");
        emprunter.setMaxWidth(Double.MAX_VALUE);
        reserver.setMaxWidth(Double.MAX_VALUE);
        carte.getChildren().addAll(emprunter, reserver);
        if (afficherDetails && !disponible && fiche.dateRetour() != null) {
            Label retour = etiquette("Retour prévu le " + fiche.dateRetour(), "date-retour");
            retour.setMaxWidth(Double.MAX_VALUE);
            retour.setAlignment(Pos.CENTER);
            carte.getChildren().add(retour);
        }
        return carte;
    }

    private static Button bouton(String texte, String style) {
        Button bouton = new Button(texte);
        bouton.getStyleClass().add(style);
        return bouton;
    }

}
