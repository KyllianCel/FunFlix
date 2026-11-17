package funflix.controleur;

import funflix.modele.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public final class ProfilControleur {
    @FXML private Label avatar;
    @FXML private Label identite;
    @FXML private Label role;
    @FXML private TextField prenom;
    @FXML private TextField nom;
    @FXML private TextField email;
    @FXML private TextField telephone;

    public VBox creer(Utilisateur utilisateur, String initiales) {
        VBox racine = ChargeurVue.charger("profil.fxml", this);
        avatar.setText(initiales);
        identite.setText(utilisateur.getPrenom() + " " + utilisateur.getNom());
        role.setText(utilisateur.estAdmin() ? "Administrateur" : "Adhérent");
        prenom.setText(utilisateur.getPrenom());
        nom.setText(utilisateur.getNom());
        email.setText(utilisateur.getEmail());
        String numero = utilisateur.getTelephone();
        telephone.setText(numero == null || numero.isBlank() ? "Non renseigné" : numero);
        return racine;
    }
}
