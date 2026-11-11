# Optimisation de la Base de Données

Pour répondre aux exigences du projet, nous avons identifié les requêtes importantes qui nécessitaient une optimisation via des index, notamment suite au retrait des données calculées (comme `nb_disponibles`) et à la mise en place des tables associatives de co-emprunt.

## 1. Optimisation du calcul dynamique des stocks
Puisque le nombre d'exemplaires disponibles n'est pas stocké en base, le système doit compter les emprunts en cours à chaque vérification de disponibilité.

**Requête métier :**
```sql
SELECT COUNT(id_emprunt) FROM emprunt 
WHERE id_media = 3 AND date_retour_effective IS NULL;
```

**EXPLAIN AVANT l'indexation :**
* **Type de jointure :** `ALL` (Full Table Scan). 
* **Problème :** La base de données doit parcourir toutes les lignes de la table `emprunt` pour trouver celles qui correspondent au média demandé. Cela ralentit considérablement la transaction si la table contient des milliers d'historiques.

**Création de l'index :**
```sql
CREATE INDEX idx_emprunt_media_actif ON emprunt(id_media, date_retour_effective);
```

**EXPLAIN APRÈS l'indexation :**
* **Type de jointure :** `ref`. 
* **Résultat :** La base utilise directement l'index `idx_emprunt_media_actif`. Le parcours de la table entière est évité, ce qui garantit que la transaction d'emprunt restera instantanée, même avec un gros volume de données.

---

## 2. Optimisation de la recherche dans le catalogue
Les adhérents filtrent très régulièrement le catalogue par type de média et par genre.

**Requête métier :**
```sql
SELECT * FROM media 
WHERE id_type = 1 AND genre = 'Aventure';
```

**EXPLAIN AVANT l'indexation :**
* **Type de jointure :** `ALL`. 
* **Problème :** Un scan complet de la table `media` est effectué pour trouver les correspondances.

**Création de l'index :**
```sql
CREATE INDEX idx_media_type_genre ON media(id_type, genre);
```

**EXPLAIN APRÈS l'indexation :**
* **Type de jointure :** `ref`. 
* **Résultat :** La base utilise l'index `idx_media_type_genre`, permettant de récupérer instantanément les médias ciblés sans lire le reste du catalogue.

---

## 3. Optimisation des jointures sur les co-emprunteurs
Avec la gestion des co-emprunteurs via la table associative `co_emprunteur` (dont la clé primaire composite est `(id_emprunt, id_utilisateur)`), les recherches filtrées uniquement par `id_utilisateur` ne peuvent pas exploiter le premier préfixe de la clé primaire.

**Requête métier :**
```sql
SELECT COUNT(*) FROM emprunt e
JOIN co_emprunteur ce ON e.id_emprunt = ce.id_emprunt
WHERE ce.id_utilisateur = 3 AND e.date_retour_effective IS NULL;
```

**Création de l'index :**
```sql
CREATE INDEX idx_co_emprunteur_user ON co_emprunteur(id_utilisateur);
```

**Résultat :**
* Accélère la vérification du quota de 3 emprunts actifs par adhérent (dans le service Java et dans le trigger SQL) ainsi que le listage des emprunts en cours d'un utilisateur sans parcourir toute la table de jointure.

---

## 4. Déportation de la logique métier au niveau SGBD

Pour consolider notre architecture, nous avons ajouté dans le schéma de base de données des vues et des triggers afin de garantir la sécurité et la cohérence des données, indépendamment du code Java.

### A. Création de Vues (Views)
Des vues ont été mises en place pour simplifier et optimiser les requêtes de lecture complexes :
* **`vue_emprunts_en_retard`** : Permet d'identifier immédiatement tous les adhérents et co-emprunteurs en retard (via une jointure sur `co_emprunteur`) et calcule dynamiquement le nombre de jours de retard sans alourdir le code applicatif.
* **`vue_media_disponibilite`** : Simplifie l'affichage du catalogue en pré-calculant le stock réel disponible (soustraction des emprunts en cours au nombre d'exemplaires total).

### B. Création de Déclencheurs (Triggers)
Pour garantir l'intégrité des règles métier, nous avons implémenté des Triggers positionnés `BEFORE INSERT` sur la table **`co_emprunteur`**. Ainsi, chaque co-emprunteur rattaché à un emprunt est individuellement contrôlé au niveau du SGBD :
* **`trg_verif_limite_emprunts`** : Bloque automatiquement le rattachement d'un utilisateur ou co-emprunteur à un emprunt s'il a déjà atteint la limite stricte de 3 emprunts simultanés en cours.
* **`trg_verif_compte_actif`** : Empêche un utilisateur ou co-emprunteur dont le compte a été désactivé d'être associé à un nouvel emprunt.