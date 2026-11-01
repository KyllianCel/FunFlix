# Optimisation de la Base de Données

Pour répondre aux exigences du projet, nous avons identifié deux requêtes importantes qui nécessitaient une optimisation via des index, notamment suite au retrait des données calculées (comme `nb_disponibles`).

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