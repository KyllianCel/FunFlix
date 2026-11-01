package funflix.service;

import funflix.dao.UtilisateurDao;
import funflix.modele.Role;
import funflix.modele.Utilisateur;
import funflix.securite.Hacheur;

import java.time.LocalDate;
import java.util.Optional;

public class AuthService {
    private final UtilisateurDao utilisateurs;

    public AuthService(UtilisateurDao utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    /** Crée un compte : le hash BCrypt inclut automatiquement son propre sel. */
    public int inscrire(String nom, String prenom, String email, String motDePasse, String telephone, Role role) {
        String hash = Hacheur.hacher(motDePasse); // Hacheur doit avoir la méthode BCrypt hacher(mdp)
        Utilisateur u = new Utilisateur(0, nom, prenom, email, hash, telephone, role, true, LocalDate.now());
        return utilisateurs.creer(u);
    }

    /**
     * Retourne l'utilisateur si les identifiants sont bons, sinon Optional.empty().
     */
    public Optional<Utilisateur> seConnecter(String email, String motDePasse) {
        return utilisateurs.trouverParEmail(email)
                .filter(u -> u.verifierMotDePasse(motDePasse));
    }
}