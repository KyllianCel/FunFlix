package funflix.controleur;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class NavigationControleur {
    static final double LARGEUR_CONTENU = 864;

    @FXML private Button catalogue;
    @FXML private Button emprunts;
    @FXML private Button favoris;
    @FXML private Button profil;
    @FXML private Button deconnexion;
    @FXML private Label avatar;
    private final BorderPane racine;

    public NavigationControleur(String initiales, Runnable ouvrirCatalogue, Runnable ouvrirEmprunts,
                                  Runnable ouvrirFavoris, Runnable ouvrirProfil, Runnable quitter) {
        racine = ChargeurVue.charger("navigation.fxml", this);
        avatar.setText(initiales);
        catalogue.setOnAction(e -> ouvrirCatalogue.run());
        emprunts.setOnAction(e -> ouvrirEmprunts.run());
        favoris.setOnAction(e -> ouvrirFavoris.run());
        profil.setOnAction(e -> ouvrirProfil.run());
        deconnexion.setOnAction(e -> quitter.run());
    }

    BorderPane racine() {
        return racine;
    }

    public void afficher(VBox contenu) {
        contenu.getStyleClass().add("contenu");
        contenu.setMaxWidth(LARGEUR_CONTENU);
        StackPane page = new StackPane(contenu);
        page.getStyleClass().add("page");
        page.setAlignment(Pos.TOP_CENTER);
        ScrollPane defilement = new ScrollPane(page);
        defilement.setFitToWidth(true);
        defilement.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        racine.setCenter(defilement);
    }

    static Label etiquette(String texte, String style) {
        Label etiquette = new Label(texte);
        etiquette.getStyleClass().add(style);
        return etiquette;
    }

}
