# 💾 Persistance des Données H2 avec Docker

## 🎯 Objectif

Configurer H2 pour **sauvegarder physiquement les données** sur le disque au lieu de les garder en mémoire. Les données seront conservées après les redémarrages.

---

## ⚙️ Configuration par Défaut

### ✅ Mode Activé : PERSISTANCE FICHIER

Par défaut, l'application est maintenant configurée pour **sauvegarder les données** :

```yaml
# docker-compose-h2.yml
H2_DB_URL: jdbc:h2:file:/app/data/ticketcomparedb
JPA_DDL_AUTO: update  # Conserve les données existantes
```

**Résultat :**
- ✅ Les données sont sauvegardées dans un volume Docker
- ✅ Les données survivent aux redémarrages
- ✅ Les tickets, produits, et statistiques sont conservés

---

## 🚀 Démarrage Rapide

### 1. Démarrer avec Persistance (Mode par défaut)

```bash
cd C:\Users\pheni\IdeaProjects\TicketCompare
docker-compose -f docker/docker-compose-h2.yml up -d
```

**C'est tout ! Les données seront automatiquement persistées.**

### 2. Vérifier que ça fonctionne

1. **Créer des données** : Scannez un ticket ou ajoutez des produits
2. **Arrêter** : `docker-compose -f docker/docker-compose-h2.yml down`
3. **Redémarrer** : `docker-compose -f docker/docker-compose-h2.yml up -d`
4. **Vérifier** : Les données sont toujours là ! ✅

---

## 🔧 Configuration Avancée

### Option 1 : Utiliser le Fichier .env

**Copier le template :**
```bash
cd docker
copy .env.h2 .env
```

**Éditer `.env` pour personnaliser :**
```bash
# MODE PERSISTANT (par défaut)
H2_DB_URL=jdbc:h2:file:/app/data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
JPA_DDL_AUTO=update

# Credentials
H2_DB_USERNAME=sa
H2_DB_PASSWORD=monMotDePasse123
```

**Démarrer avec le .env :**
```bash
docker-compose -f docker/docker-compose-h2.yml --env-file docker/.env up -d
```

### Option 2 : Variables d'Environnement Inline

```bash
# Mode persistant (défaut)
H2_DB_URL="jdbc:h2:file:/app/data/ticketcomparedb" docker-compose -f docker/docker-compose-h2.yml up -d

# Mode mémoire (données perdues au redémarrage)
H2_DB_URL="jdbc:h2:mem:ticketcomparedb" docker-compose -f docker/docker-compose-h2.yml up -d
```

---

## 🔄 Modes Disponibles

### Mode 1 : Persistance Fichier (Recommandé) ✅

```bash
H2_DB_URL=jdbc:h2:file:/app/data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
JPA_DDL_AUTO=update
```

**Avantages :**
- ✅ Données sauvegardées physiquement
- ✅ Conservées après redémarrage
- ✅ Backup possible
- ✅ Parfait pour développement et tests

**Utilisation :**
- Développement local
- Tests avec données réelles
- Démonstration
- Environnement de staging

### Mode 2 : En Mémoire (Test Jetable) 🔄

```bash
H2_DB_URL=jdbc:h2:mem:ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE
JPA_DDL_AUTO=create
```

**Avantages :**
- ⚡ Plus rapide
- 🧪 Tests unitaires
- 🔄 Reset automatique

**Utilisation :**
- Tests automatisés
- CI/CD
- Développement rapide sans persistence

---

## 📊 Stratégies JPA/Hibernate

### update (Recommandé pour Persistance) ✅

```bash
JPA_DDL_AUTO=update
```

**Comportement :**
- ✅ Conserve les données existantes
- ✅ Met à jour le schéma automatiquement
- ✅ Ajoute nouvelles colonnes/tables
- ⚠️ Ne supprime PAS les colonnes obsolètes

**Parfait pour :**
- Développement avec persistance
- Tests avec données conservées
- Environnement de staging

### create (Reset à chaque démarrage) 🔄

```bash
JPA_DDL_AUTO=create
```

**Comportement :**
- ❌ Supprime toutes les données
- 🔄 Recrée les tables
- 🆕 Démarre avec base vide

**Parfait pour :**
- Tests automatisés
- Développement initial
- Reset complet nécessaire

### validate (Production) 🔒

```bash
JPA_DDL_AUTO=validate
```

**Comportement :**
- ✅ Vérifie la cohérence du schéma
- ❌ Ne modifie rien
- 💥 Échoue si incohérence

**Parfait pour :**
- Production
- Migration vers PostgreSQL

### none (Manuel) 🛠️

```bash
JPA_DDL_AUTO=none
```

**Comportement :**
- ❌ Aucune action automatique
- 🛠️ Gestion manuelle du schéma
- 📜 Utilisation de scripts SQL

---

## 🗄️ Accès à la Console H2

### URL de Connexion

1. **Ouvrir** : http://localhost:8080/h2-console

2. **Paramètres de connexion :**
   ```
   Driver Class: org.h2.Driver
   JDBC URL: jdbc:h2:file:/app/data/ticketcomparedb
   User Name: sa
   Password: (vide par défaut)
   ```

3. **Cliquer** : "Connect"

### Requêtes Utiles

```sql
-- Voir toutes les tables
SELECT * FROM INFORMATION_SCHEMA.TABLES;

-- Compter les tickets
SELECT COUNT(*) FROM TICKETS;

-- Compter les produits
SELECT COUNT(*) FROM PRODUCTS;

-- Voir les catégories
SELECT DISTINCT category FROM PRODUCTS;

-- Produits les plus chers
SELECT name, category, price 
FROM PRODUCTS 
ORDER BY price DESC 
LIMIT 10;
```

---

## 💾 Gestion des Volumes Docker

### Voir les Volumes

```bash
# Lister tous les volumes
docker volume ls

# Voir les détails du volume H2
docker volume inspect ticketcompare_h2_data
```

### Backup de la Base

**Méthode 1 : Via Docker**
```bash
# Backup
docker run --rm -v ticketcompare_h2_data:/data -v ${PWD}:/backup ubuntu tar czf /backup/h2-backup-$(date +%Y%m%d-%H%M%S).tar.gz /data

# Restore
docker run --rm -v ticketcompare_h2_data:/data -v ${PWD}:/backup ubuntu tar xzf /backup/h2-backup-YYYYMMDD-HHMMSS.tar.gz -C /
```

**Méthode 2 : Export SQL depuis H2 Console**
```sql
-- Dans H2 Console
SCRIPT TO 'backup.sql';

-- Pour restaurer
RUNSCRIPT FROM 'backup.sql';
```

### Supprimer les Données (Reset)

```bash
# 1. Arrêter l'application
docker-compose -f docker/docker-compose-h2.yml down

# 2. Supprimer le volume
docker volume rm ticketcompare_h2_data

# 3. Redémarrer (nouvelle base vide)
docker-compose -f docker/docker-compose-h2.yml up -d
```

### Nettoyer Complètement

```bash
# Supprimer tous les volumes H2
docker volume rm ticketcompare_h2_data ticketcompare_h2_uploads ticketcompare_h2_logs

# Ou tout supprimer (attention : perte de données)
docker-compose -f docker/docker-compose-h2.yml down -v
```

---

## 🔄 Scénarios d'Utilisation

### Scénario 1 : Développement avec Persistance

**Configuration :**
```bash
H2_DB_URL=jdbc:h2:file:/app/data/ticketcomparedb
JPA_DDL_AUTO=update
```

**Workflow :**
1. Démarrer l'application
2. Scanner des tickets
3. Arrêter pour modifier le code
4. Redémarrer → Données toujours là ✅

### Scénario 2 : Tests avec Reset

**Configuration :**
```bash
H2_DB_URL=jdbc:h2:mem:ticketcomparedb
JPA_DDL_AUTO=create
```

**Workflow :**
1. Démarrer l'application
2. Tester avec données de test
3. Arrêter → Données effacées
4. Redémarrer → Base vide pour nouveaux tests

### Scénario 3 : Démonstration

**Configuration :**
```bash
H2_DB_URL=jdbc:h2:file:/app/data/ticketcomparedb
JPA_DDL_AUTO=update
```

**Workflow :**
1. Pré-remplir avec données de démo
2. Faire la démonstration
3. Backup avant la démo
4. Restore après la démo si besoin

### Scénario 4 : Migration vers PostgreSQL

**Étape 1 : Exporter depuis H2**
```sql
-- Dans H2 Console
SCRIPT TO 'export.sql';
```

**Étape 2 : Adapter le SQL pour PostgreSQL**
```bash
# Modifier export.sql si nécessaire
```

**Étape 3 : Basculer sur PostgreSQL**
```bash
docker-compose -f docker/docker-compose.yml up -d
```

**Étape 4 : Importer**
```bash
# Via psql ou pgAdmin
```

---

## 📍 Où sont les Fichiers ?

### Dans Docker (Volume)

```
Volume: ticketcompare_h2_data
Emplacement interne: /app/data/
Fichiers:
  - ticketcomparedb.mv.db    (Données)
  - ticketcomparedb.trace.db (Logs)
```

### Sur Windows (Path physique)

```powershell
# Trouver le path du volume
docker volume inspect ticketcompare_h2_data

# Typiquement:
\\wsl$\docker-desktop-data\data\docker\volumes\ticketcompare_h2_data\_data\
```

---

## ⚠️ Avertissements

### ⚠️ H2 n'est PAS pour la Production

**Pourquoi ?**
- ❌ Pas conçu pour haute charge
- ❌ Pas de concurrence optimale
- ❌ Pas de réplication
- ❌ Pas de backup automatique
- ❌ Performance limitée

**Pour la production, utilisez PostgreSQL :**
```bash
docker-compose -f docker/docker-compose.yml up -d
```

### ⚠️ Backup Régulier Recommandé

Même avec persistance, faites des backups :
```bash
# Backup automatique quotidien (exemple)
0 2 * * * docker run --rm -v ticketcompare_h2_data:/data -v /backup:/backup ubuntu tar czf /backup/h2-$(date +\%Y\%m\%d).tar.gz /data
```

---

## 🧪 Tests

### Test 1 : Vérifier la Persistance

```bash
# 1. Démarrer
docker-compose -f docker/docker-compose-h2.yml up -d

# 2. Attendre le démarrage
sleep 30

# 3. Ajouter un ticket via l'interface
curl -X POST http://localhost:8080/tickets/...

# 4. Arrêter
docker-compose -f docker/docker-compose-h2.yml down

# 5. Redémarrer
docker-compose -f docker/docker-compose-h2.yml up -d

# 6. Vérifier dans H2 Console
# Les données doivent être là !
```

### Test 2 : Comparer Mode Fichier vs Mémoire

**Mode Fichier :**
```bash
H2_DB_URL="jdbc:h2:file:/app/data/ticketcomparedb" docker-compose -f docker/docker-compose-h2.yml up -d
# → Données conservées après redémarrage
```

**Mode Mémoire :**
```bash
H2_DB_URL="jdbc:h2:mem:ticketcomparedb" docker-compose -f docker/docker-compose-h2.yml up -d
# → Données perdues après redémarrage
```

---

## 📚 Résumé des Commandes

### Démarrage

```bash
# Mode persistant (défaut)
docker-compose -f docker/docker-compose-h2.yml up -d

# Avec .env personnalisé
docker-compose -f docker/docker-compose-h2.yml --env-file docker/.env up -d

# Mode mémoire ponctuel
H2_DB_URL="jdbc:h2:mem:ticketcomparedb" docker-compose -f docker/docker-compose-h2.yml up -d
```

### Gestion

```bash
# Arrêter
docker-compose -f docker/docker-compose-h2.yml down

# Voir les logs
docker-compose -f docker/docker-compose-h2.yml logs -f

# Reset complet (perte de données)
docker-compose -f docker/docker-compose-h2.yml down -v
```

### Backup

```bash
# Backup du volume
docker run --rm -v ticketcompare_h2_data:/data -v ${PWD}:/backup ubuntu tar czf /backup/h2-backup.tar.gz /data

# Restore du volume
docker run --rm -v ticketcompare_h2_data:/data -v ${PWD}:/backup ubuntu tar xzf /backup/h2-backup.tar.gz -C /
```

---

## ✅ Checklist de Configuration

- [ ] Docker et Docker Compose installés
- [ ] Fichier `docker-compose-h2.yml` mis à jour
- [ ] Fichier `application.properties` mis à jour
- [ ] (Optionnel) Fichier `.env` créé et configuré
- [ ] Volume `ticketcompare_h2_data` créé automatiquement
- [ ] Application démarrée avec `docker-compose up -d`
- [ ] H2 Console accessible : http://localhost:8080/h2-console
- [ ] Test de persistance effectué (ajout données + redémarrage)
- [ ] Backup régulier configuré (si production-like)

---

**Date : 27 Décembre 2024**  
**Fonctionnalité : Persistance H2 avec Docker**  
**Statut : ✅ Configuré et prêt à utiliser**

