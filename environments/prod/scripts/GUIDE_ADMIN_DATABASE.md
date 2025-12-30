# 🔧 Guide Administration Base de Données PostgreSQL - Production

## 📋 Table des matières
1. [Accès à la base de données](#accès-à-la-base-de-données)
2. [Backup manuel](#backup-manuel)
3. [Restauration](#restauration)
4. [Gestion des données](#gestion-des-données)
5. [PgAdmin (Interface graphique)](#pgadmin-interface-graphique)
6. [Scripts d'administration](#scripts-dadministration)

---

## 🔐 Accès à la Base de Données

### Méthode 1 : Accès direct via psql (Ligne de commande)

#### Sous Windows (PowerShell)
```powershell
# Depuis le dossier environments/prod/scripts
.\connect-db.ps1

# Ou directement
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker
```

#### Sous Linux/WSL (Bash)
```bash
# Accès direct à PostgreSQL
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker
```

### Commandes psql utiles

Une fois connecté à `psql`, vous pouvez utiliser :

```sql
-- Lister toutes les tables
\dt

-- Décrire une table spécifique
\d tickets
\d stores
\d users

-- Afficher toutes les bases de données
\l

-- Se connecter à une autre base de données
\c shoptracker

-- Lister les utilisateurs
\du

-- Quitter psql
\q
```

---

## 💾 Backup Manuel

### Option 1 : Backup complet (recommandé)

#### Windows PowerShell
```powershell
# Créer un backup avec horodatage
$date = Get-Date -Format "yyyyMMdd_HHmmss"
docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c -b -v -f "/backups/backup_$date.dump"

# Le fichier sera dans : environments/prod/backups/
```

#### Linux/WSL Bash
```bash
# Créer un backup avec horodatage
docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c -b -v -f "/backups/backup_$(date +%Y%m%d_%H%M%S).dump"
```

### Option 2 : Backup au format SQL (lisible)

#### Windows PowerShell
```powershell
$date = Get-Date -Format "yyyyMMdd_HHmmss"
docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker > "../backups/backup_$date.sql"
```

#### Linux/WSL Bash
```bash
docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker > "../backups/backup_$(date +%Y%m%d_%H%M%S).sql"
```

### Backup automatique planifié

**Windows - Tâche planifiée** :
```powershell
# Créer une tâche qui s'exécute tous les jours à 2h du matin
$action = New-ScheduledTaskAction -Execute "PowerShell.exe" -Argument "-File C:\Users\MHA25660\IdeaProjects\TicketCompare\environments\prod\scripts\backup-db.ps1"
$trigger = New-ScheduledTaskTrigger -Daily -At 2am
Register-ScheduledTask -Action $action -Trigger $trigger -TaskName "ShopTracker_Backup" -Description "Backup quotidien de la base de données ShopTracker"
```

---

## 🔄 Restauration

### Restaurer depuis un fichier .dump

#### Windows PowerShell
```powershell
# Restaurer un backup (remplacer la date)
docker exec -i shoptracker-db pg_restore -U shoptracker_admin -d shoptracker -v -c /backups/backup_20251230_140000.dump
```

#### Linux/WSL Bash
```bash
# Restaurer un backup
docker exec -i shoptracker-db pg_restore -U shoptracker_admin -d shoptracker -v -c /backups/backup_20251230_140000.dump
```

### Restaurer depuis un fichier .sql

#### Windows PowerShell
```powershell
# Restaurer depuis un fichier SQL
Get-Content "../backups/backup_20251230_140000.sql" | docker exec -i shoptracker-db psql -U shoptracker_admin -d shoptracker
```

#### Linux/WSL Bash
```bash
# Restaurer depuis un fichier SQL
docker exec -i shoptracker-db psql -U shoptracker_admin -d shoptracker < ../backups/backup_20251230_140000.sql
```

---

## 📊 Gestion des Données

### Consulter les données

```powershell
# Utiliser le script de requêtes rapides
.\query-db.ps1

# Ou se connecter directement
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker
```

### Requêtes SQL courantes

```sql
-- Voir tous les tickets
SELECT * FROM tickets ORDER BY purchase_date DESC LIMIT 10;

-- Voir tous les magasins
SELECT * FROM stores;

-- Voir tous les utilisateurs
SELECT id, username, email, role, enabled FROM users;

-- Statistiques par magasin
SELECT s.name, COUNT(t.id) as nb_tickets, SUM(t.total_amount) as total
FROM stores s
LEFT JOIN tickets t ON t.store_id = s.id
GROUP BY s.name
ORDER BY total DESC;

-- Statistiques par catégorie
SELECT category, COUNT(*) as nb_items, SUM(total_price) as total
FROM ticket_items
GROUP BY category
ORDER BY total DESC;

-- Statistiques du jour
SELECT COUNT(*) as tickets_aujourdhui, SUM(total_amount) as total 
FROM tickets 
WHERE DATE(purchase_date) = CURRENT_DATE;

-- Top 10 produits les plus achetés
SELECT product_name, COUNT(*) as nb_achats, SUM(quantity) as quantite_totale
FROM ticket_items
GROUP BY product_name
ORDER BY nb_achats DESC
LIMIT 10;
```

### Ajouter des données manuellement

```sql
-- Ajouter un magasin
INSERT INTO stores (name, address, created_at)
VALUES ('Carrefour Centre', '123 Rue de la Paix, Paris', NOW());

-- Ajouter un utilisateur
INSERT INTO users (username, email, password, role, enabled, created_at)
VALUES ('admin', 'admin@shoptracker.local', '$2a$10$...', 'ADMIN', true, NOW());

-- Mettre à jour un ticket
UPDATE tickets
SET total_amount = 125.50
WHERE id = 1;

-- Changer le rôle d'un utilisateur
UPDATE users SET role = 'ADMIN' WHERE username = 'john.doe';

-- Activer/désactiver un utilisateur
UPDATE users SET enabled = true WHERE id = 5;

-- Supprimer un ticket (attention!)
DELETE FROM tickets WHERE id = 999;
```

---

## 🖥️ PgAdmin (Interface Graphique)

### Démarrer PgAdmin

```powershell
# Depuis le dossier scripts
.\start-pgadmin.ps1

# Ou manuellement
cd ..
docker-compose --profile admin up -d
```

### Accéder à PgAdmin

1. **URL** : http://localhost:5050
2. **Email** : `admin@shoptracker.local`
3. **Mot de passe** : `AdminSecure2025!`

### Configurer la connexion dans PgAdmin

1. Cliquer sur **"Add New Server"**
2. **General** :
   - Name : `ShopTracker Production`
3. **Connection** :
   - Host : `postgres` (nom du service Docker)
   - Port : `5432`
   - Database : `shoptracker`
   - Username : `shoptracker_admin`
   - Password : `ShopTracker2025!Secure`
4. Sauvegarder

### Arrêter PgAdmin

```powershell
# Depuis le dossier scripts
.\stop-pgadmin.ps1

# Ou manuellement
cd ..
docker-compose --profile admin stop pgadmin
```

---

## 🔍 Vérifications et Maintenance

### Vérifier l'état de la base de données

```powershell
# Se connecter et vérifier
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "\dt"
```

### Taille de la base de données

```sql
-- Connecté à psql
SELECT pg_size_pretty(pg_database_size('shoptracker'));

-- Taille de chaque table
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### Vacuum (nettoyage et optimisation)

```sql
-- Optimiser la base de données
VACUUM ANALYZE;

-- Vacuum complet (plus long)
VACUUM FULL ANALYZE;
```

### Logs PostgreSQL

```powershell
# Voir les logs de PostgreSQL
docker logs shoptracker-db --tail 100 -f

# Sauvegarder les logs
docker logs shoptracker-db > ../data/logs/postgres_$(Get-Date -Format 'yyyyMMdd_HHmmss').log
```

---

## 📜 Scripts d'Administration

Tous les scripts sont dans le dossier `environments/prod/scripts/` :

### 1. **connect-db.ps1**
Connexion rapide à PostgreSQL via psql
```powershell
.\connect-db.ps1
```

### 2. **query-db.ps1**
Menu interactif pour requêtes courantes
```powershell
.\query-db.ps1
```

### 3. **start-pgadmin.ps1**
Démarrage de l'interface PgAdmin
```powershell
.\start-pgadmin.ps1
```

### 4. **stop-pgadmin.ps1**
Arrêt de PgAdmin
```powershell
.\stop-pgadmin.ps1
```

### 5. **backup-db.ps1**
Backup automatique de la base de données
```powershell
.\backup-db.ps1
```

### 6. **restore-db.ps1**
Restauration de la base de données
```powershell
.\restore-db.ps1 backup_20251230_140000.dump
```

---

## 🚨 Dépannage

### Erreur "role root does not exist"

❌ **Mauvaise commande** :
```bash
docker exec -it shoptracker-db psql
```

✅ **Bonne commande** :
```bash
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker
```

### Mot de passe requis

Si le système demande un mot de passe :
- Mot de passe par défaut : `ShopTracker2025!Secure`
- Ou vérifier la variable d'environnement `DB_PASSWORD` dans `.env`

### Conteneur non démarré

```powershell
# Vérifier l'état
docker ps -a | Select-String "shoptracker"

# Redémarrer
cd ..
docker-compose restart postgres
```

### Connexion refusée

```powershell
# Vérifier les logs
docker logs shoptracker-db --tail 50

# Vérifier que PostgreSQL écoute
docker exec shoptracker-db pg_isready -U shoptracker_admin
```

---

## 📝 Variables d'Environnement

Les variables sont définies dans `environments/prod/.env` ou `docker-compose.yml` :

```env
# Base de données
DB_NAME=shoptracker
DB_USER=shoptracker_admin
DB_PASSWORD=ShopTracker2025!Secure

# PgAdmin
ADMIN_EMAIL=admin@shoptracker.local
ADMIN_PASSWORD=AdminSecure2025!
```

Pour changer les mots de passe :

1. Modifier le fichier `.env`
2. Recréer les conteneurs :
```powershell
cd ..
docker-compose down
docker-compose up -d
```

---

## 🎯 Cas d'Usage Fréquents

### 1. Export complet pour migration

```powershell
# Backup complet avec structure + données
docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c > migration_complete.dump
```

### 2. Export CSV d'une table

```sql
-- Connecté à psql
\copy (SELECT * FROM tickets) TO '/backups/tickets_export.csv' WITH CSV HEADER;
```

```powershell
# Récupérer le fichier depuis le conteneur
docker cp shoptracker-db:/backups/tickets_export.csv ../backups/
```

### 3. Import CSV dans une table

```sql
-- Connecté à psql
\copy tickets FROM '/backups/tickets_import.csv' WITH CSV HEADER;
```

```powershell
# D'abord copier le fichier dans le conteneur
docker cp ../backups/tickets_import.csv shoptracker-db:/backups/
```

### 4. Créer un utilisateur en lecture seule

```sql
-- Utilisateur pour les rapports
CREATE USER readonly_user WITH PASSWORD 'ReadOnly2025!';
GRANT CONNECT ON DATABASE shoptracker TO readonly_user;
GRANT USAGE ON SCHEMA public TO readonly_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_user;

-- Pour les nouvelles tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO readonly_user;
```

### 5. Réinitialiser le mot de passe d'un utilisateur

```sql
-- Connecté à psql
UPDATE users 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' 
WHERE username = 'john.doe';
-- Mot de passe ci-dessus = "password123" (exemple)
```

---

## 🔒 Sécurité

### Bonnes pratiques

1. **Changer les mots de passe par défaut** dans `.env`
2. **Ne jamais commiter** le fichier `.env` dans Git
3. **Faire des backups réguliers** (quotidiens en production)
4. **Limiter l'accès** au port PostgreSQL (5432) - uniquement en local
5. **Utiliser des connexions chiffrées** (SSL/TLS) en production externe

### Audit des connexions

```sql
-- Voir les connexions actives
SELECT pid, usename, application_name, client_addr, backend_start 
FROM pg_stat_activity 
WHERE datname = 'shoptracker';
```

---

## 📚 Ressources

- [Documentation PostgreSQL](https://www.postgresql.org/docs/)
- [Guide psql](https://www.postgresql.org/docs/current/app-psql.html)
- [PgAdmin Documentation](https://www.pgadmin.org/docs/)
- [pg_dump Documentation](https://www.postgresql.org/docs/current/app-pgdump.html)

---

**✅ Guide créé le 2025-12-30**
**📍 Emplacement : environments/prod/scripts/**

