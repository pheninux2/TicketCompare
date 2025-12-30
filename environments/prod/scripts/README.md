# 📜 Scripts d'Administration - Production

Scripts PowerShell pour l'administration de la base de données PostgreSQL en environnement de production.

## 📁 Emplacement

```
environments/prod/scripts/
├── GUIDE_ADMIN_DATABASE.md    # Guide complet d'administration
├── README.md                  # Ce fichier
├── connect-db.ps1             # Connexion PostgreSQL
├── query-db.ps1               # Requêtes rapides
├── backup-db.ps1              # Backup de la BDD
├── restore-db.ps1             # Restauration de la BDD
├── start-pgadmin.ps1          # Démarrer PgAdmin
└── stop-pgadmin.ps1           # Arrêter PgAdmin
```

---

## 🚀 Utilisation

### 1️⃣ **connect-db.ps1** - Connexion à PostgreSQL

Ouvre une session interactive `psql` pour administrer directement la base de données.

```powershell
.\connect-db.ps1
```

**Fonctionnalités** :
- ✅ Vérification automatique du conteneur
- ✅ Connexion avec les bons identifiants
- ✅ Affichage des commandes utiles
- ✅ Mode interactif complet

**Exemples de commandes psql** :
```sql
\dt                              -- Lister les tables
\d tickets                       -- Décrire la table tickets
SELECT * FROM tickets LIMIT 10;  -- Voir les données
\q                               -- Quitter
```

---

### 2️⃣ **query-db.ps1** - Requêtes rapides

Menu interactif avec requêtes SQL pré-configurées.

```powershell
.\query-db.ps1
```

**Options disponibles** :
1. Lister toutes les tables
2. Voir les derniers tickets (10)
3. Voir tous les magasins
4. Voir tous les utilisateurs
5. Statistiques par magasin
6. Statistiques par catégorie
7. Taille de la base de données
8. Nombre total de tickets
9. Statistiques du jour
10. Top 10 produits les plus achetés
11. Connexions actives
12. Taille de chaque table
13. Exécuter une requête personnalisée

**Idéal pour** :
- ✅ Consultation rapide des données
- ✅ Statistiques en un clic
- ✅ Monitoring de production
- ✅ Débogage

---

### 3️⃣ **backup-db.ps1** - Backup de la base de données

Créer un backup complet de la base de données.

```powershell
# Backup par défaut (format .dump)
.\backup-db.ps1

# Backup au format SQL (lisible)
.\backup-db.ps1 -Format sql

# Backup SQL avec compression
.\backup-db.ps1 -Format sql -Compress
```

**Paramètres** :
- `-Format` : `dump` (défaut) ou `sql`
- `-Compress` : Compression ZIP (uniquement pour SQL)

**Fonctionnalités** :
- ✅ Horodatage automatique
- ✅ Vérification de l'espace disque
- ✅ Affichage de la progression
- ✅ Liste des backups existants
- ✅ Nettoyage automatique des anciens backups (>30 jours)

**Destination** :
```
environments/prod/backups/backup_HOSTNAME_YYYYMMDD_HHMMSS.dump
```

---

### 4️⃣ **restore-db.ps1** - Restauration de la base de données

Restaurer la base de données depuis un backup.

```powershell
# Mode interactif (sélection du backup)
.\restore-db.ps1

# Restaurer un backup spécifique
.\restore-db.ps1 backup_20251230_140000.dump
```

**Fonctionnalités** :
- ✅ Backup de sécurité automatique avant restauration
- ✅ Confirmation obligatoire (tape "RESTAURER")
- ✅ Support des formats `.dump` et `.sql`
- ✅ Décompression automatique des `.zip`
- ✅ Vérification post-restauration

**⚠️ ATTENTION** :
- Cette opération **supprime toutes les données actuelles**
- Un backup de sécurité est créé automatiquement
- Confirmation obligatoire pour éviter les erreurs

---

### 5️⃣ **start-pgadmin.ps1** - Interface graphique d'administration

Démarrer PgAdmin, l'interface web d'administration de PostgreSQL.

```powershell
.\start-pgadmin.ps1
```

**Accès** :
- **URL** : http://localhost:5050
- **Email** : `admin@shoptracker.local`
- **Mot de passe** : `AdminSecure2025!`

**Configuration de la connexion** :
1. Cliquer sur "Add New Server"
2. **General** → Name : `ShopTracker Production`
3. **Connection** :
   - Host : `postgres`
   - Port : `5432`
   - Database : `shoptracker`
   - Username : `shoptracker_admin`
   - Password : `ShopTracker2025!Secure`

**Fonctionnalités** :
- ✅ Démarrage automatique du conteneur
- ✅ Ouverture du navigateur
- ✅ Interface graphique complète
- ✅ Query Tool intégré
- ✅ Export de données
- ✅ Diagrammes ER

---

### 6️⃣ **stop-pgadmin.ps1** - Arrêter PgAdmin

Arrêter proprement PgAdmin.

```powershell
.\stop-pgadmin.ps1
```

**Options** :
- Arrêt simple (conteneur en pause)
- Suppression complète du conteneur (optionnel)

---

## 🔐 Identifiants

### PostgreSQL
```
Base de données : shoptracker
Utilisateur     : shoptracker_admin
Mot de passe    : ShopTracker2025!Secure
Port            : 5432 (localhost uniquement)
```

### PgAdmin
```
URL         : http://localhost:5050
Email       : admin@shoptracker.local
Mot de passe: AdminSecure2025!
```

---

## 🎯 Cas d'Usage Fréquents

### Backup quotidien automatique

Planifier un backup tous les jours à 2h du matin :

```powershell
$action = New-ScheduledTaskAction -Execute "PowerShell.exe" `
  -Argument "-File C:\Users\MHA25660\IdeaProjects\TicketCompare\environments\prod\scripts\backup-db.ps1"
$trigger = New-ScheduledTaskTrigger -Daily -At 2am
Register-ScheduledTask -Action $action -Trigger $trigger `
  -TaskName "ShopTracker_Backup" `
  -Description "Backup quotidien ShopTracker Production"
```

### Consultation rapide des statistiques

```powershell
# Ouvrir le menu de requêtes
.\query-db.ps1
# Sélectionner l'option 5 (Statistiques par magasin)
```

### Export CSV pour analyse

```powershell
# Se connecter à psql
.\connect-db.ps1

# Exporter les tickets
\copy (SELECT * FROM tickets) TO '/backups/tickets_export.csv' WITH CSV HEADER;

# Récupérer le fichier
docker cp shoptracker-db:/backups/tickets_export.csv ../backups/
```

### Changer le mot de passe d'un utilisateur

```powershell
# Se connecter
.\connect-db.ps1

# Exécuter la requête
UPDATE users SET password = '$2a$10$...' WHERE username = 'john.doe';
```

### Migration vers un autre serveur

```powershell
# 1. Créer un backup complet
.\backup-db.ps1

# 2. Copier le fichier backup_*.dump vers le nouveau serveur

# 3. Sur le nouveau serveur, restaurer
.\restore-db.ps1 backup_20251230_140000.dump
```

---

## 🚨 Dépannage

### Le conteneur n'est pas démarré

```powershell
cd ..\..
.\start-prod.ps1
```

### Erreur "role root does not exist"

Utilisez toujours `-U shoptracker_admin` :
```bash
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker
```

### Mot de passe incorrect

Vérifiez le fichier `../.env` ou utilisez le mot de passe par défaut :
```
ShopTracker2025!Secure
```

### Logs PostgreSQL

```powershell
docker logs shoptracker-db --tail 100 -f
```

### Vérifier l'état de la base

```powershell
docker exec shoptracker-db pg_isready -U shoptracker_admin
```

---

## 📚 Documentation Complète

Consultez le **GUIDE_ADMIN_DATABASE.md** pour :
- Documentation détaillée de chaque script
- Commandes SQL avancées
- Maintenance et optimisation
- Sécurité et bonnes pratiques
- Troubleshooting complet

---

## 🛡️ Sécurité

### Bonnes pratiques

1. **Changer les mots de passe par défaut** dans `../.env`
2. **Ne jamais commiter** les fichiers `.env` et backups dans Git
3. **Faire des backups réguliers** (quotidiens recommandés)
4. **Tester les restaurations** régulièrement
5. **Limiter l'accès** aux scripts (permissions Windows)
6. **Audit des logs** : vérifier régulièrement les connexions

### Variables d'environnement

Créez un fichier `../.env` pour personnaliser :

```env
# Base de données
DB_PASSWORD=VotreMotDePasseSecurise2025!

# PgAdmin
ADMIN_EMAIL=votre.email@exemple.com
ADMIN_PASSWORD=VotreMotDePasseAdmin2025!
```

---

## 📊 Monitoring

### Vérifications régulières

```powershell
# Taille de la base
.\query-db.ps1  # Option 7

# Connexions actives
.\query-db.ps1  # Option 11

# Statistiques des tables
.\query-db.ps1  # Option 12

# Logs
docker logs shoptracker-db --tail 50
```

### Alertes recommandées

- Taille de la base > 1 GB
- Nombre de connexions > 50
- Temps de réponse > 5s
- Échec de backup quotidien

---

## 🔄 Workflow Recommandé

### Maintenance hebdomadaire

1. **Lundi** : Vérifier les backups de la semaine
2. **Mercredi** : Analyser les statistiques
3. **Vendredi** : Nettoyer les anciens backups
4. **Dimanche** : Tester une restauration

### Avant une mise à jour

1. **Backup complet** : `.\backup-db.ps1`
2. **Vérifier le backup** : taille et date
3. **Faire la mise à jour**
4. **Vérifier les données** : `.\query-db.ps1`

### En cas de problème

1. **Consulter les logs** : `docker logs shoptracker-db`
2. **Vérifier les connexions** : `.\query-db.ps1` (option 11)
3. **Restaurer si nécessaire** : `.\restore-db.ps1`

---

## 📞 Support

### Ressources

- 📖 **Guide complet** : `GUIDE_ADMIN_DATABASE.md`
- 🌐 **PostgreSQL** : https://www.postgresql.org/docs/
- 🖥️ **PgAdmin** : https://www.pgadmin.org/docs/

### Commandes de base

```powershell
# Statut des conteneurs
docker ps

# Logs en temps réel
docker logs shoptracker-db -f

# Redémarrer PostgreSQL
docker restart shoptracker-db

# Espace disque
docker system df
```

---

**✅ Scripts créés le 2025-12-30**  
**📍 Version : Production 1.0**  
**🔒 Environnement : Production**

---

## 🎓 Formation

### Pour les débutants

1. Commencez par `connect-db.ps1` pour vous familiariser avec psql
2. Utilisez `query-db.ps1` pour des requêtes simples
3. Pratiquez les backups avec `backup-db.ps1`
4. Explorez PgAdmin avec `start-pgadmin.ps1`

### Pour les administrateurs expérimentés

- Automatisez les backups avec Windows Task Scheduler
- Créez des scripts personnalisés basés sur ces exemples
- Intégrez avec des outils de monitoring (Prometheus, Grafana)
- Configurez des alertes email en cas d'erreur

---

**🚀 Prêt à administrer votre base de données !**

