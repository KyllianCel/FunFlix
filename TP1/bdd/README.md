# Base de données - Application Ludothèque (Projet FunFlix)

Ce dossier contient l'ensemble des scripts SQL et de la documentation nécessaires pour initialiser et configurer la base de données MariaDB de l'application, en respectant les principes de normalisation et les choix de conception du projet.

## 1. Structure et Modélisation (3FN)
- **Héritage (Table par sous-classe) :** La table mère `media` centralise les attributs communs (titre, description, année, etc.). Les tables filles `jeu_video` et `jeu_societe` étendent cette table via une relation de clé primaire et étrangère.
- **Co-emprunts et Co-réservations (Relations N-M) :** Les tables associatives `co_emprunteur` et `co_reservataire` permettent d'associer un ou plusieurs adhérents (`1..*`) à un même emprunt ou à une même réservation.
- **Traçabilité Réservation vers Emprunt :** La table `emprunt` intègre une clé étrangère optionnelle `id_reservation` (`0..1`) permettant de lier un emprunt à la réservation initiale qu'il concrétise.
- **Données non stockées :** Aucune donnée calculable (comme le stock disponible ou le retard d'un emprunt) n'est enregistrée en base. Tout est calculé dynamiquement ou vérifié par des transactions strictes dans la couche service Java.

## 2. Lancement de la base de données avec Docker Compose

Ce guide explique comment démarrer et gérer le conteneur MariaDB de l'application à l'aide de Docker Compose.

### 1. Prérequis
- Avoir installé **Docker** et **Docker Desktop** sur votre machine.
- S'assurer que le service Docker est bien en cours d'exécution.

### 2. Démarrage du conteneur MariaDB
Placez-vous à la racine du projet (là où se trouve le fichier `docker-compose.yml`) et exécutez la commande suivante en arrière-plan :

```bash
docker compose up -d
```

Cette commande va :
- Télécharger l'image officielle de MariaDB.
- Créer un réseau dédié.
- Créer un volume persistant (`tp1_db_data`) pour que vos données ne soient pas perdues si le conteneur s'arrête.
- Démarrer le serveur MariaDB.

### 3. Réinitialisation complète de l'environnement (Hard Reset)
Si vous souhaitez supprimer complètement le conteneur ainsi que toutes ses données pour repartir d'une base totalement vide :

```bash
docker compose down -v
```

Puis relancez le conteneur propre avec :

```bash
docker compose up -d
```

### 4. Arrêt simple du conteneur
Pour stopper le conteneur sans effacer les données :

```bash
docker compose stop
```

## 3. Procédure pour recréer la base de zéro
Pour réinitialiser complètement l'environnement de base de données (par exemple sur un conteneur Docker), suivez ces étapes :

1. **Supprimer et recréer la structure (Schéma) :**
   Exécutez le script `schema.sql` qui supprime les anciennes tables dans l'ordre des contraintes, recrée la base `ludotheque`, installe les tables et configure les index d'optimisation.

   ```bash
   docker exec -i ludotheque_db mariadb -u root -proot < schema.sql
   ```

2. **Insérer le jeu de données de test :**
   Exécutez le script `donnees_test.sql` pour peupler la base avec les utilisateurs (administrateurs et adhérents), le catalogue de médias pluridisciplinaire, les réservations et l'historique des emprunts (incluant des co-emprunteurs).

   ```bash
   docker exec -i ludotheque_db mariadb -u root -proot < donnees_test.sql
   ```

## 4. Configuration de l'application Java (JDBC)
Pour connecter votre application Java à votre instance MariaDB :
1. Rendez-vous dans le dossier `src/main/resources/`.
2. Dupliquez le fichier `db.properties.example` et renommez la copie en **`db.properties`**.
3. Renseignez vos accès locaux :

   ```properties
   db.url=jdbc:mariadb://localhost:3306/ludotheque
   db.user=root
   db.password=votre_mot_de_passe
   ```

## 5. Sécurité
- Les mots de passe des utilisateurs insérés dans les scripts de test sont hachés à l'aide de l'algorithme **BCrypt** (géré par la librairie Java intégrée au projet).