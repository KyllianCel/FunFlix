package funflix;

import funflix.controleur.ConnexionControleur;
import funflix.dao.UtilisateurDao;
import funflix.service.AuthService;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Parent racine = new ConnexionControleur(new AuthService(new UtilisateurDao())).creerVue();
        Scene scene = new Scene(racine, 1080, 590);
        scene.getStylesheets().add(getClass().getResource("/funflix/vue/funflix.css").toExternalForm());
        stage.setTitle("FunFlix");
        stage.setMinWidth(760);
        stage.setMinHeight(500);
        stage.setScene(scene);
        racine.setFocusTraversable(true);
        racine.requestFocus();
        stage.show();
    }

}
