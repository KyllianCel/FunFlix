package funflix.controleur;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import funflix.modele.Role;
import funflix.modele.Utilisateur;
import funflix.service.AuthService;
import funflix.controleur.ConnexionEcranControleur.Saisie;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public final class ConnexionControleur {
    private static final String CLE_EMAIL = "email-memorise";
    private static final Pattern EMAIL = Pattern.compile("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
    private final AuthService auth;
    private final StackPane racine = new StackPane();
    private ConnexionEcranControleur vue;

    public ConnexionControleur(AuthService auth) {
        this.auth = auth;
        afficherConnexion();
    }

    public Parent creerVue() {
        return racine;
    }

    private void afficherConnexion() {
        vue = new ConnexionEcranControleur(lireEmail(), this::envoyer);
        racine.getChildren().setAll(vue.racine());
    }

    private void envoyer(Saisie saisie) {
        String email = saisie.email().strip();
        String prenom = saisie.prenom().strip();
        String nom = saisie.nom().strip();
        String motDePasse = saisie.motDePasse();
        if (email.isEmpty() || motDePasse.isEmpty()) {
            vue.afficherMessage("Renseigne ton adresse e-mail et ton mot de passe.", true);
            return;
        }
        if (email.length() > 255 || motDePasse.getBytes(StandardCharsets.UTF_8).length > 72) {
            vue.afficherMessage("L’adresse e-mail ou le mot de passe est trop long.", true);
            return;
        }
        if (saisie.inscription() && (!EMAIL.matcher(email).matches() || prenom.isEmpty()
                || nom.isEmpty() || prenom.length() > 50 || nom.length() > 50 || motDePasse.length() < 8)) {
            vue.afficherMessage("Vérifie le nom, le prénom, l’e-mail et le mot de passe (8 caractères minimum).", true);
            return;
        }

        vue.setEnCours(true);
        Task<Optional<Utilisateur>> requete = new Task<>() {
            @Override
            protected Optional<Utilisateur> call() {
                if (saisie.inscription()) {
                    auth.inscrire(nom, prenom, email, motDePasse, null, Role.ADHERENT);
                    return Optional.empty();
                }
                return auth.seConnecter(email, motDePasse);
            }
        };
        requete.setOnSucceeded(evenement -> {
            vue.setEnCours(false);
            if (saisie.inscription()) {
                vue.afficherConnexion(email);
                vue.afficherMessage("Compte créé. Tu peux te connecter.", false);
            } else {
                requete.getValue().ifPresentOrElse(utilisateur -> {
                    memoriserEmail(saisie.memoriserEmail() ? email : "");
                    racine.getChildren().setAll(new CatalogueControleur(utilisateur, this::afficherConnexion).creerVue());
                    vue = null;
                }, () -> vue.afficherMessage("Adresse e-mail ou mot de passe incorrect, ou compte inactif.", true));
            }
        });
        requete.setOnFailed(evenement -> {
            vue.setEnCours(false);
            Throwable erreur = requete.getException();
            if (saisie.inscription() && erreur.getCause() instanceof SQLException sql && sql.getErrorCode() == 1062) {
                vue.afficherMessage("Cette adresse e-mail est déjà utilisée.", true);
            } else {
                vue.afficherMessage("Impossible de terminer l’opération. Vérifie que MariaDB est démarré.", true);
            }
        });
        Thread thread = new Thread(requete, "authentification");
        thread.setDaemon(true);
        thread.start();
    }

    private static String lireEmail() {
        try {
            return Preferences.userNodeForPackage(ConnexionControleur.class).get(CLE_EMAIL, "");
        } catch (SecurityException e) {
            return "";
        }
    }

    private static void memoriserEmail(String email) {
        try {
            Preferences preferences = Preferences.userNodeForPackage(ConnexionControleur.class);
            if (email.isEmpty()) {
                preferences.remove(CLE_EMAIL);
            } else {
                preferences.put(CLE_EMAIL, email);
            }
        } catch (SecurityException e) {
            // L’authentification reste utilisable si les préférences locales sont indisponibles.
        }
    }
}
