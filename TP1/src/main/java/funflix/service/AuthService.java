package funflix.service;

import funflix.dao.UtilisateurDao;
import funflix.modele.Role;
import funflix.modele.Utilisateur;
import funflix.securite.Hacheur;

import java.time.LocalDate;
import java.util.Optional;

public class AuthService {

    // 1. Déclaration de la dépendance
    private final UtilisateurDao utilisateurDao;

    // 2. Initialisation via le constructeur
    public AuthService(UtilisateurDao utilisateurDao) {
        this.utilisateurDao = utilisateurDao;
    }

    public int inscrire(String nom, String prenom, String email, String mdp, String tel, Role role) {
        // Hachage du mot de passe en clair avant l'insertion en BDD
        String hash = Hacheur.hacher(mdp); 
        
        Utilisateur u = new Utilisateur(0, nom, prenom, email, hash, tel, role, true, LocalDate.now());
        return utilisateurDao.creer(u);
    }

    public Optional<Utilisateur> seConnecter(String email, String mdp) {
        Optional<Utilisateur> opt = utilisateurDao.trouverParEmail(email);
        
        // Vérification Bcrypt entre le mot de passe tapé et le hash stocké
        if (opt.isPresent() && Hacheur.verifier(mdp, opt.get().getMotDePasseHash())) {
            return opt;
        }
        return Optional.empty();
    }
}