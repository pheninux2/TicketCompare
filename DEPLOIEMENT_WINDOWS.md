# 📦 GUIDE DE DÉPLOIEMENT WINDOWS - ShopTracker

## Date : 28 Décembre 2025

---

## 🎯 OBJECTIF

Déployer ShopTracker sur un PC Windows en local avec :
- ✅ Base de données PostgreSQL persistante
- ✅ Données sauvegardées physiquement sur disque
- ✅ Accès BDD réservé à l'administrateur uniquement
- ✅ Backup/Restore facilité
- ✅ Prêt pour migration vers VPS/Render

---

## 📋 PRÉREQUIS

### 1. Docker Desktop pour Windows
```
Télécharger et installer Docker Desktop:
https://www.docker.com/products/docker-desktop

Après installation:
- Démarrer Docker Desktop
- S'assurer que Docker Engine est démarré
```

### 2. PowerShell
```
Déjà installé sur Windows 10/11
Exécuter PowerShell en tant qu'administrateur
```

---

## 🚀 DÉPLOIEMENT

### Étape 1 : Préparer la configuration

```powershell
# 1. Ouvrir PowerShell
# 2. Naviguer vers le projet
cd C:\Users\pheni\IdeaProjects\TicketCompare

# 3. Copier le fichier d'environnement
cd docker
copy .env.prod.example .env

# 4. Modifier le fichier .env avec vos informations
notepad .env
```

**Important** : Changez AU MINIMUM ces valeurs dans `.env` :
```env
DB_PASSWORD=VotreMotDePasseSecurise123!
ADMIN_PASSWORD=AdminPasswordSecure456!
ADMIN_EMAIL=votre.email@example.com
```

---

### Étape 2 : Déployer l'application

```powershell
# Depuis le dossier racine du projet
cd C:\Users\pheni\IdeaProjects\TicketCompare

# Lancer le script de déploiement
.\deploy-windows.ps1
```

**Ce script va :**
1. ✅ Vérifier Docker
2. ✅ Construire l'image Docker
3. ✅ Créer les conteneurs PostgreSQL + Application
4. ✅ Démarrer l'application
5. ✅ Créer les dossiers de données

**Durée estimée** : 5-10 minutes (première fois)

---

### Étape 3 : Accéder à l'application

Une fois le déploiement terminé :

```
🌐 Application ShopTracker:
http://localhost:8080

📊 Créer un compte ou se connecter
🛒 Commencer à utiliser ShopTracker !
```

---

## 💾 GESTION DES DONNÉES

### Emplacement des données sur disque

Toutes les données sont sauvegardées dans :
```
C:\Users\pheni\IdeaProjects\TicketCompare\docker\data\
│
├── postgres\      ← Base de données PostgreSQL
├── uploads\       ← Images de tickets uploadés
├── logs\          ← Logs de l'application
└── pgadmin\       ← Configuration PgAdmin (si utilisé)
```

### Emplacement des backups
```
C:\Users\pheni\IdeaProjects\TicketCompare\docker\backups\
```

---

## 🔐 ACCÈS À LA BASE DE DONNÉES (ADMIN UNIQUEMENT)

### Option 1 : PgAdmin (Interface graphique)

```powershell
# Démarrer PgAdmin
cd C:\Users\pheni\IdeaProjects\TicketCompare\docker
docker-compose -f docker-compose-prod-windows.yml --profile admin up -d

# Ouvrir dans le navigateur
start http://localhost:5050
```

**Connexion PgAdmin :**
- Email : Celui configuré dans `.env` (ADMIN_EMAIL)
- Mot de passe : Celui configuré dans `.env` (ADMIN_PASSWORD)

**Ajouter un serveur dans PgAdmin :**
- Host : `postgres`
- Port : `5432`
- Database : `shoptracker`
- Username : `shoptracker_admin`
- Password : Celui configuré dans `.env` (DB_PASSWORD)

---

### Option 2 : Ligne de commande PostgreSQL

```powershell
# Se connecter directement à PostgreSQL
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker

# Exemples de commandes SQL:
\dt                  # Lister les tables
\d users             # Décrire la table users
SELECT * FROM users; # Voir les utilisateurs
\q                   # Quitter
```

---

## 💾 BACKUP / RESTAURATION

### Créer un backup

```powershell
# Lancer le script de backup
.\backup-db.ps1
```

**Résultat :**
- Backup compressé dans `docker/backups/shoptracker_backup_YYYYMMDD_HHMMSS.sql.zip`
- Garde les 30 derniers backups automatiquement

---

### Restaurer un backup

```powershell
# Lancer le script de restauration
.\restore-db.ps1

# Sélectionner le backup dans la liste
# Confirmer avec "CONFIRMER"
```

⚠️ **ATTENTION** : La restauration ÉCRASE toutes les données actuelles !

---

## 🔧 COMMANDES UTILES

### Gérer l'application

```powershell
# Naviguer vers le dossier docker
cd C:\Users\pheni\IdeaProjects\TicketCompare\docker

# Voir les logs en temps réel
docker-compose -f docker-compose-prod-windows.yml logs -f

# Voir les logs de l'application uniquement
docker-compose -f docker-compose-prod-windows.yml logs -f app

# Voir les logs de la base de données
docker-compose -f docker-compose-prod-windows.yml logs -f postgres

# Arrêter l'application
docker-compose -f docker-compose-prod-windows.yml down

# Redémarrer l'application
docker-compose -f docker-compose-prod-windows.yml restart

# Démarrer l'application (si arrêtée)
docker-compose -f docker-compose-prod-windows.yml up -d

# Voir le statut des conteneurs
docker ps --filter "name=shoptracker"
```

---

### Gestion de l'espace disque

```powershell
# Voir l'espace utilisé par Docker
docker system df

# Nettoyer les images inutilisées
docker system prune

# Nettoyer complètement (ATTENTION: supprime tout ce qui n'est pas utilisé)
docker system prune -a
```

---

## 📊 MONITORING

### Vérifier la santé de l'application

```powershell
# Health check de l'application
curl http://localhost:8080/actuator/health

# Ou dans le navigateur
start http://localhost:8080/actuator/health
```

**Résultat attendu :**
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

---

## 🔒 SÉCURITÉ

### Ports exposés

| Port | Service | Accessible de | Description |
|------|---------|--------------|-------------|
| 8080 | Application | Partout | ShopTracker web |
| 5432 | PostgreSQL | Localhost uniquement | Base de données |
| 5050 | PgAdmin | Localhost uniquement | Admin BDD |

**Configuration sécurisée :**
- PostgreSQL n'est accessible QUE depuis `localhost:5432`
- PgAdmin n'est accessible QUE depuis `localhost:5050`
- Seul le port 8080 est accessible depuis le réseau local

---

### Recommandations de sécurité

```
✅ Changez TOUS les mots de passe dans .env
✅ Ne partagez JAMAIS le fichier .env
✅ Faites des backups réguliers
✅ Gardez Docker Desktop à jour
✅ Limitez l'accès physique au PC
```

---

## 🚨 DÉPANNAGE

### Problème : Docker n'est pas démarré
```powershell
# Démarrer Docker Desktop manuellement
# Ou redémarrer le service Docker
Restart-Service docker
```

### Problème : Port 8080 déjà utilisé
```powershell
# Trouver le processus utilisant le port
netstat -ano | findstr :8080

# Arrêter le processus (remplacer PID)
taskkill /PID <PID> /F

# Ou changer le port dans docker-compose-prod-windows.yml
```

### Problème : L'application ne démarre pas
```powershell
# Voir les logs détaillés
docker-compose -f docker-compose-prod-windows.yml logs app

# Reconstruire l'image
docker-compose -f docker-compose-prod-windows.yml build --no-cache app
docker-compose -f docker-compose-prod-windows.yml up -d
```

### Problème : Données perdues après redémarrage
```powershell
# Vérifier que les volumes sont bien montés
docker volume ls

# Vérifier que les dossiers existent
dir C:\Users\pheni\IdeaProjects\TicketCompare\docker\data
```

---

## 📤 EXPORT POUR DÉPLOIEMENT CLIENT

### Créer une image Docker prête à l'emploi

```powershell
# 1. Construire l'image
docker build -f docker/Dockerfile.prod -t shoptracker:1.0.0 .

# 2. Sauvegarder l'image
docker save shoptracker:1.0.0 -o shoptracker-1.0.0.tar

# 3. Compresser (optionnel)
Compress-Archive shoptracker-1.0.0.tar shoptracker-1.0.0.zip
```

### Déployer sur un autre PC Windows

Sur le PC client :
```powershell
# 1. Installer Docker Desktop

# 2. Charger l'image
docker load -i shoptracker-1.0.0.tar

# 3. Copier les fichiers de configuration
# - docker-compose-prod-windows.yml
# - .env
# - deploy-windows.ps1

# 4. Déployer
.\deploy-windows.ps1
```

---

## 🌐 MIGRATION VERS VPS/RENDER

Les fichiers suivants sont déjà prêts pour le cloud :
- ✅ `docker-compose-prod-windows.yml` (peut être adapté pour Linux)
- ✅ `Dockerfile.prod`
- ✅ `application-prod.properties`

Pour migrer :
1. Exporter les données avec `backup-db.ps1`
2. Déployer sur le VPS avec Docker
3. Restaurer les données avec `restore-db.ps1`

---

## 📞 SUPPORT

En cas de problème :
- 📧 Email : adil.haddad.xdev@gmail.com
- 📖 Documentation : `docs/`
- 🐛 Logs : `docker/data/logs/`

---

## ✅ CHECKLIST DE DÉPLOIEMENT

```
☐ Docker Desktop installé et démarré
☐ Fichier .env configuré avec mots de passe personnalisés
☐ Script deploy-windows.ps1 exécuté sans erreur
☐ Application accessible sur http://localhost:8080
☐ Compte administrateur créé
☐ Premier backup effectué
☐ PgAdmin testé (optionnel)
☐ Documentation lue et comprise
```

---

**Date :** 28 Décembre 2025  
**Application :** 🛒 ShopTracker  
**Version :** 1.0.0-SNAPSHOT  
**Type :** 📦 Déploiement Production Windows Local  
**Status :** ✅ PRÊT À DÉPLOYER

