package funflix.securite;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Hacheur {

    /**
     * Hache le mot de passe avec BCrypt.
     * Le "sel" est généré automatiquement et inclus dans le hash retourné.
     */
    public static String hacher(String motDePasse) {
        return BCrypt.withDefaults().hashToString(12, motDePasse.toCharArray());
    }

    /**
     * Vérifie si un mot de passe en clair correspond au hash stocké en base.
     */
    public static boolean verifier(String motDePasse, String hashBDD) {
        BCrypt.Result result = BCrypt.verifyer().verify(motDePasse.toCharArray(), hashBDD);
        return result.verified;
    }
}