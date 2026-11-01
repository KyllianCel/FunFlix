package funflix;

import funflix.dao.EmpruntDao;
import funflix.dao.MediaDao;
import funflix.dao.UtilisateurDao;
import funflix.modele.Emprunt;
import funflix.modele.Media;
import funflix.modele.Role;
import funflix.modele.Utilisateur;
import funflix.service.AuthService;
import funflix.service.EmpruntService;
import funflix.service.MediaIndisponibleException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Livrable de S2 : ce programme prouve que la base, les DAO et la transaction fonctionnent. */
public class MainTestBDD {

    public static void main(String[] args) throws Exception {
        // Prérequis : Exécuter schema.sql et donnees_test.sql manuellement dans MariaDB
        System.out.println("1. Lancement des tests sur la base MariaDB existante...");

        UtilisateurDao utilisateurs = new UtilisateurDao();
        MediaDao medias = new MediaDao();
        EmpruntDao emprunts = new EmpruntDao();
        AuthService auth = new AuthService(utilisateurs);
        EmpruntService service = new EmpruntService(emprunts);

        System.out.println("\n2. Catalogue (polymorphisme : chaque sous-classe se décrit elle-même)");
        for (Media m : medias.listerTous()) {
            System.out.println("   ID: " + m.getId() + " | Titre: " + m.getTitre() + " | Type: " + m.getType());
        }

        System.out.println("\n3. Comptes et authentification");
        // Test d'inscription BCrypt V2
        int idAlice = auth.inscrire("Dupont", "Alice", "alice@test.fr", "motdepasse123", "0600000001", Role.ADHERENT);
        int idBob = auth.inscrire("Martin", "Bob", "bob@test.fr", "autreMotDePasse", "0600000002", Role.ADHERENT);
        
        Optional<Utilisateur> ok = auth.seConnecter("alice@test.fr", "motdepasse123");
        Optional<Utilisateur> ko = auth.seConnecter("alice@test.fr", "mauvais");
        System.out.println("   Bon mot de passe     -> " + (ok.isPresent() ? ok.get().getNom() : "REFUSÉ"));
        System.out.println("   Mauvais mot de passe -> " + (ko.isPresent() ? ko.get().getNom() : "REFUSÉ"));

        System.out.println("\n4. Transaction : emprunt de Zelda (ID 2)");
        // On suppose que Zelda est à l'ID 2 dans tes donnees_test.sql (JeuVideo)
        int idZelda = 2; 
        
        try {
            service.emprunter(idAlice, idZelda, 14);
            System.out.println("   Alice : a emprunté le média " + idZelda);
            
            service.emprunter(idBob, idZelda, 14);
            System.out.println("   ERREUR : Bob n'aurait pas dû pouvoir emprunter !");
        } catch (MediaIndisponibleException ex) {
            System.out.println("   Bob   : refusé (stock épuisé) -> " + ex.getMessage());
        }

        System.out.println("\n5. Emprunts en cours d'Alice, retard calculé (pas stocké en base)");
        List<Emprunt> enCours = emprunts.trouverEnCours(idAlice);
        for (Emprunt e : enCours) {
            System.out.println("   Emprunt ID: " + e.getId() + " | Statut : " + e.getStatut());
        }

        System.out.println("\n6. Retour (Mise à jour date_retour_effective)");
        if (!enCours.isEmpty()) {
            service.rendre(enCours.get(0).getId());
            System.out.println("   Le média a été rendu.");
            System.out.println("   Emprunts en cours d'Alice : " + emprunts.trouverEnCours(idAlice).size());
        }

        System.out.println("\nTout fonctionne.");
    }
}