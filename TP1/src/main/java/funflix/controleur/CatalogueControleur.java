package funflix.controleur;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import funflix.dao.MediaDao;
import funflix.dao.MediaDao.EntreeCatalogue;
import funflix.modele.CatalogueDemo;
import funflix.modele.FicheCatalogue;
import funflix.modele.Utilisateur;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CatalogueControleur {
    private static final DateTimeFormatter JOUR_MOIS = DateTimeFormatter.ofPattern("dd/MM");

    private final MediaDao mediaDao = new MediaDao();
    private Task<List<FicheCatalogue>> chargementCatalogue;
    private final Utilisateur utilisateur;
    private final String initiales;
    private final NavigationControleur vue;

    public CatalogueControleur(Utilisateur utilisateur, Runnable deconnecter) {
        this.utilisateur = utilisateur;
        initiales = initiale(utilisateur.getPrenom()) + initiale(utilisateur.getNom());
        vue = new NavigationControleur(initiales, this::afficherCatalogue, this::afficherEmprunts,
            this::afficherFavoris, this::afficherProfil, () -> {
                annulerChargement();
                deconnecter.run();
            });
        afficherCatalogue();
    }

    private static String initiale(String nom) {
        String texte = nom == null ? "" : nom.strip();
        return texte.isEmpty() ? "" : texte.substring(0, texte.offsetByCodePoints(0, 1)).toUpperCase(Locale.ROOT);
    }

    public Parent creerVue() {
        return vue.racine();
    }

    private void afficherCatalogue() {
        annulerChargement();
        vue.afficher(messageCatalogue("Chargement des médias…"));
        Task<List<FicheCatalogue>> chargement = new Task<>() {
            @Override
            protected List<FicheCatalogue> call() {
                return mediaDao.listerCatalogue().stream().map(CatalogueControleur::fiche).toList();
            }
        };
        chargementCatalogue = chargement;
        chargement.setOnSucceeded(evenement -> {
            if (chargementCatalogue == chargement) {
                chargementCatalogue = null;
                vue.afficher(new CatalogueEcranControleur(chargement.getValue()).creer());
            }
        });
        chargement.setOnFailed(evenement -> {
            if (chargementCatalogue == chargement) {
                chargementCatalogue = null;
                vue.afficher(messageCatalogue(
                    "Impossible de charger les médias. Vérifie que MariaDB tourne et que db.properties est configuré, puis clique sur Catalogue."));
            }
        });
        Thread thread = new Thread(chargement, "chargement-catalogue");
        thread.setDaemon(true);
        thread.start();
    }

    private void annulerChargement() {
        if (chargementCatalogue != null) {
            chargementCatalogue.cancel();
            chargementCatalogue = null;
        }
    }

    private static VBox messageCatalogue(String texte) {
        Label titre = new Label("Catalogue");
        titre.getStyleClass().add("titre-section");
        Label message = new Label(texte);
        message.setWrapText(true);
        return new VBox(10, titre, message);
    }

    private static FicheCatalogue fiche(EntreeCatalogue media) {
        String categorie = switch (media.type().toLowerCase(Locale.ROOT)) {
            case "jeu_video" -> "Jeux vidéo";
            case "jeu_societe" -> "Jeux de société";
            case "bd", "bande_dessinee" -> "BD";
            case "manga" -> "Mangas";
            case "livre" -> "Livres";
            case "dvd" -> "DVD";
            default -> media.type();
        };
        String couleur = switch (categorie) {
            case "Jeux vidéo" -> "#2a594c";
            case "Jeux de société" -> "#ae512d";
            case "BD" -> "#f1bd49";
            case "Mangas" -> "#e3593e";
            case "Livres" -> "#384b85";
            default -> "#75549c";
        };
        String dateRetour = media.retourPrevu() == null ? null : JOUR_MOIS.format(media.retourPrevu());
        return new FicheCatalogue(categorie, media.titre(), media.titre(), media.auteur(),
            couleur, !categorie.equals("BD") && !categorie.equals("Mangas"), media.disponible(), dateRetour);
    }

    private void afficherEmprunts() {
        annulerChargement();
        vue.afficher(new EmpruntsControleur().creer());
    }

    private void afficherFavoris() {
        annulerChargement();
        List<String> titresFavoris = List.of("Zelda : Tears of the Kingdom", "Frieren — Tome 1",
                                            "Mario Kart 8 Deluxe");
        var favoris = CatalogueDemo.MEDIAS.stream()
            .filter(fiche -> titresFavoris.contains(fiche.titre()))
            .toList();
        vue.afficher(new FavorisControleur().creer(favoris));
    }

    private void afficherProfil() {
        annulerChargement();
        vue.afficher(new ProfilControleur().creer(utilisateur, initiales));
    }
}
