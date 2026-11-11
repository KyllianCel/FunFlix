package funflix;

import funflix.dao.EmpruntDao;
import funflix.dao.MediaDao;
import funflix.dao.ReservationDao;
import funflix.dao.UtilisateurDao;
import funflix.modele.Emprunt;
import funflix.modele.Media;
import funflix.modele.Reservation;
import funflix.modele.Role;
import funflix.modele.StatutReservation;
import funflix.modele.Utilisateur;
import funflix.service.AuthService;
import funflix.service.EmpruntService;
import funflix.service.MediaIndisponibleException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MainTestBDD {

    public static void main(String[] args) throws Exception {
        System.out.println("1. Lancement des tests sur la base MariaDB existante...");

        UtilisateurDao utilisateurs = new UtilisateurDao();
        MediaDao medias = new MediaDao();
        EmpruntDao emprunts = new EmpruntDao();
        ReservationDao reservations = new ReservationDao();
        AuthService auth = new AuthService(utilisateurs);
        EmpruntService service = new EmpruntService(emprunts);

        System.out.println("\n2. Catalogue (polymorphisme : chaque sous-classe se décrit elle-même)");
        for (Media m : medias.listerTous()) {
            System.out.println("   ID: " + m.getId() + " | Titre: " + m.getTitre() + " | Type: " + m.getType());
        }

        System.out.println("\n3. Comptes et authentification");
        long suffixe = System.currentTimeMillis();
        int idAlice = auth.inscrire("Dupont", "Alice", "alice." + suffixe + "@test.fr", "motdepasse123", "0600000001", Role.ADHERENT);
        int idBob = auth.inscrire("Martin", "Bob", "bob." + suffixe + "@test.fr", "autreMotDePasse", "0600000002", Role.ADHERENT);

        Optional<Utilisateur> ok = auth.seConnecter("alice." + suffixe + "@test.fr", "motdepasse123");
        Optional<Utilisateur> ko = auth.seConnecter("alice." + suffixe + "@test.fr", "mauvais");
        System.out.println("   Bon mot de passe     -> " + (ok.isPresent() ? ok.get().getNom() : "REFUSÉ"));
        System.out.println("   Mauvais mot de passe -> " + (ko.isPresent() ? ko.get().getNom() : "REFUSÉ"));

        System.out.println("\n4. Transaction : emprunt de Zelda (ID 1)");
        int idZelda = 1;

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
            System.out.println("   Emprunt ID: " + e.getId() + " | Statut : " + e.getStatut() + " | Co-emprunteurs : " + e.getIdUtilisateurs());
        }

        System.out.println("\n6. Retour (Mise à jour date_retour_effective)");
        if (!enCours.isEmpty()) {
            service.rendre(enCours.get(0).getId());
            System.out.println("   Le média a été rendu.");
            System.out.println("   Emprunts en cours d'Alice : " + emprunts.trouverEnCours(idAlice).size());
        }

        System.out.println("\n7. Test de Co-Emprunt (Alice et Bob empruntent ensemble Mario Kart ID 4)");
        int idMarioKart = 4;
        service.emprunter(List.of(idAlice, idBob), idMarioKart, 10, null);
        System.out.println("   Emprunts actifs d'Alice : " + emprunts.trouverEnCours(idAlice).size());
        System.out.println("   Emprunts actifs de Bob   : " + emprunts.trouverEnCours(idBob).size());

        System.out.println("\n8. Test Réservation -> Transformation en Emprunt");
        Reservation nouvelleRes = new Reservation(
                0,
                LocalDate.now(),
                StatutReservation.DISPONIBLE,
                List.of(idAlice, idBob),
                idZelda
        );
        int idRes = reservations.creer(nouvelleRes);
        System.out.println("   Réservation créée avec l'ID : " + idRes + " pour les utilisateurs " + nouvelleRes.getIdUtilisateurs());

        int idEmpruntIssuRes = reservations.transformerEnEmprunt(idRes, LocalDate.now().plusDays(14), emprunts);
        Emprunt empVerif = emprunts.trouver(idEmpruntIssuRes).orElseThrow();
        Reservation resVerif = reservations.trouver(idRes).orElseThrow();

        System.out.println("   Emprunt créé ID : " + empVerif.getId() + " | Lié à la réservation ID : " + empVerif.getIdReservation());
        System.out.println("   Nouveau statut de la réservation : " + resVerif.getStatut());

        System.out.println("\nTout fonctionne.");
    }
}