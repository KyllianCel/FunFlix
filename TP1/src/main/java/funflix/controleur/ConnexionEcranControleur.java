package funflix.controleur;

import java.util.function.Consumer;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class ConnexionEcranControleur {
    public record Saisie(boolean inscription, String prenom, String nom, String email,
                          String motDePasse, boolean memoriserEmail) {
    }

    private static final PseudoClass SELECTIONNE = PseudoClass.getPseudoClass("selectionne");
    private static final PseudoClass ERREUR = PseudoClass.getPseudoClass("erreur");

    @FXML private Button connexion;
    @FXML private Button inscription;
    @FXML private Button valider;
    @FXML private TextField email;
    @FXML private TextField prenom;
    @FXML private TextField nom;
    @FXML private PasswordField motDePasse;
    @FXML private CheckBox memoriser;
    @FXML private VBox formulaire;
    @FXML private VBox identite;
    @FXML private HBox options;
    @FXML private Label titre;
    @FXML private Label sousTitre;
    @FXML private Label admin;
    @FXML private Label message;
    private final Parent racine;
    private boolean creation;

    public ConnexionEcranControleur(String emailMemorise, Consumer<Saisie> envoyer) {
        racine = ChargeurVue.charger("connexion.fxml", this);
        memoriser.setSelected(!emailMemorise.isEmpty());
        connexion.setOnAction(evenement -> afficherMode(false));
        inscription.setOnAction(evenement -> afficherMode(true));
        valider.setOnAction(evenement -> envoyer.accept(new Saisie(creation, prenom.getText(), nom.getText(),
            email.getText(), motDePasse.getText(), memoriser.isSelected())));
        afficherConnexion(emailMemorise);
    }

    Parent racine() {
        return racine;
    }

    public void afficherConnexion(String adresse) {
        email.setText(adresse);
        prenom.clear();
        nom.clear();
        afficherMode(false);
    }

    private void afficherMode(boolean creation) {
        this.creation = creation;
        connexion.pseudoClassStateChanged(SELECTIONNE, !creation);
        inscription.pseudoClassStateChanged(SELECTIONNE, creation);
        motDePasse.clear();
        afficherMessage("", false);
        titre.setText(creation ? "Créer un compte" : "Bon retour !");
        sousTitre.setText(creation ? "Crée ton compte pour accéder à FunFlix."
            : "Connectez-vous pour emprunter des médias.");
        afficher(identite, creation);
        afficher(options, !creation);
        afficher(admin, !creation);
        valider.setText(creation ? "Créer mon compte" : "Se connecter");
    }

    public void setEnCours(boolean enCours) {
        connexion.setDisable(enCours);
        inscription.setDisable(enCours);
        formulaire.setDisable(enCours);
        if (enCours) {
            afficherMessage("Connexion à MariaDB…", false);
        }
    }

    public void afficherMessage(String texte, boolean erreur) {
        message.setText(texte);
        afficher(message, !texte.isEmpty());
        message.pseudoClassStateChanged(ERREUR, erreur);
    }

    private static void afficher(Node composant, boolean visible) {
        composant.setManaged(visible);
        composant.setVisible(visible);
    }
}
