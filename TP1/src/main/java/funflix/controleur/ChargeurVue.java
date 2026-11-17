package funflix.controleur;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

final class ChargeurVue {
    private ChargeurVue() {
    }

    static <T extends Parent> T charger(String fichier, Object controleur) {
        FXMLLoader chargeur = new FXMLLoader(ChargeurVue.class.getResource("/funflix/vue/" + fichier));
        chargeur.setController(controleur);
        try {
            return chargeur.load();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de charger la vue " + fichier, e);
        }
    }
}
