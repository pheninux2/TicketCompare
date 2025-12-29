# 🚀 GUIDE D'UTILISATION - ShopTracker

## 📁 Structure du Projet

```
TicketCompare/
│
├── environments/              ← 🎯 CONFIGURATIONS PAR ENVIRONNEMENT
│   ├── dev/                  ← Développement (H2 en mémoire)
│   │   ├── docker-compose.yml
│   │   ├── Dockerfile.dev
│   │   ├── .env
│   │   └── data/
│   │
│   └── prod/                 ← Production (PostgreSQL persistant)
│       ├── docker-compose.yml
│       ├── Dockerfile
│       ├── .env.example
│       ├── .env (à créer)
│       ├── data/
│       └── backups/
│
├── src/                      ← Code source
├── docker/                   ← 📦 ANCIEN DOSSIER (deprecated)
│
├── start-dev.ps1             ← 🟢 Démarrer DEV
├── start-prod.ps1            ← 🔴 Démarrer PROD
├── backup-db.ps1             ← 💾 Backup (PROD)
├── restore-db.ps1            ← 🔄 Restore (PROD)
│
├── DEPLOIEMENT_WINDOWS.md    ← 📚 Guide complet PROD
├── QUICK_START_WINDOWS.md    ← ⚡ Démarrage rapide
└── README.md                 ← Documentation générale
```

---

## 🎯 CHOISIR SON ENVIRONNEMENT

### 🟢 Mode DÉVELOPPEMENT

**Quand l'utiliser ?**
- ✅ Développer de nouvelles fonctionnalités
- ✅ Tester des modifications
- ✅ Debug avec breakpoints
- ✅ Pas besoin de données persistantes

**Caractéristiques :**
- Base H2 en mémoire (perdue au redémarrage)
- Hot reload activé (modifications appliquées automatiquement)
- Debug port ouvert (5005)
- H2 Console accessible
- Logs en mode DEBUG

**Démarrer :**
```powershell
.\start-dev.ps1
```

**Accès :**
- 🌐 http://localhost:8080
- 🗄️ http://localhost:8080/h2-console
- 🐛 Debug: localhost:5005

---

### 🔴 Mode PRODUCTION

**Quand l'utiliser ?**
- ✅ Déploiement client
- ✅ Démo avec vraies données
- ✅ Tests de performance
- ✅ Données à conserver

**Caractéristiques :**
- PostgreSQL persistant (données sauvegardées)
- Optimisations JVM
- Logs en mode INFO/WARN
- Backups automatisables
- PgAdmin pour administration BDD

**Démarrer :**
```powershell
# Première fois : configurer
cd environments\prod
copy .env.example .env
notepad .env   # Changer les mots de passe !

# Puis démarrer
cd ..\..
.\start-prod.ps1
```

**Accès :**
- 🌐 http://localhost:8080
- 🔐 http://localhost:5050 (PgAdmin - admin uniquement)

---

## 📋 WORKFLOWS COURANTS

### 1. Développer une nouvelle fonctionnalité

```powershell
# 1. Démarrer en mode DEV
.\start-dev.ps1

# 2. Ouvrir le projet dans IntelliJ IDEA
# 3. Modifier le code (hot reload automatique)
# 4. Tester sur http://localhost:8080

# 5. Debug si nécessaire (port 5005)
# 6. Consulter H2 Console pour voir les données
#    http://localhost:8080/h2-console

# 7. Arrêter quand terminé
cd environments\dev
docker-compose down
```

---

### 2. Tester en conditions réelles

```powershell
# 1. Démarrer en mode PROD
.\start-prod.ps1

# 2. Créer des données de test
# 3. Tester l'application

# 4. Si besoin, consulter la BDD
cd environments\prod
docker-compose --profile admin up -d
# Ouvrir http://localhost:5050

# 5. Créer un backup des données
cd ..\..
.\backup-db.ps1

# 6. Arrêter
cd environments\prod
docker-compose down
```

---

### 3. Déployer chez un client

```powershell
# 1. Configurer l'environnement client
cd environments\prod
copy .env.example .env
notepad .env   # Configurer avec les infos client

# 2. Démarrer
cd ..\..
.\start-prod.ps1

# 3. Créer le premier compte admin
# 4. Faire un backup initial
.\backup-db.ps1

# 5. Donner accès au client
# Application: http://localhost:8080
```

---

### 4. Migrer DEV → PROD

```powershell
# Option 1 : Refaire les données en PROD
# (Recommandé pour éviter données de test)

# Option 2 : Exporter/Importer
# 1. En DEV : Exporter via H2 Console
#    http://localhost:8080/h2-console
#    Tools → Backup

# 2. En PROD : Importer via PgAdmin
#    Restore → Sélectionner le fichier
```

---

## 💾 GESTION DES DONNÉES (PROD)

### Créer un backup

```powershell
.\backup-db.ps1
```

**Résultat :**
- Backup dans `environments/prod/backups/`
- Nommage: `shoptracker_backup_YYYYMMDD_HHMMSS.sql.zip`
- Garde les 30 derniers backups

---

### Restaurer un backup

```powershell
.\restore-db.ps1
```

**Étapes :**
1. Liste interactive des backups disponibles
2. Sélectionner le backup
3. Confirmer avec "CONFIRMER"
4. Restauration automatique

---

### Accéder à PostgreSQL (Admin)

```powershell
# Via ligne de commande
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker

# Via PgAdmin (interface graphique)
cd environments\prod
docker-compose --profile admin up -d
# Ouvrir http://localhost:5050
```

---

## 🔄 PASSER D'UN MODE À L'AUTRE

### DEV → PROD

```powershell
# 1. Arrêter DEV
cd environments\dev
docker-compose down

# 2. Démarrer PROD
cd ..\..
.\start-prod.ps1
```

### PROD → DEV

```powershell
# 1. Sauvegarder les données PROD
.\backup-db.ps1

# 2. Arrêter PROD
cd environments\prod
docker-compose down

# 3. Démarrer DEV
cd ..\..
.\start-dev.ps1
```

---

## 🛠️ COMMANDES UTILES

### Mode DEV

```powershell
cd environments\dev

# Logs en temps réel
docker-compose logs -f

# Redémarrer
docker-compose restart

# Arrêter
docker-compose down

# Reconstruire (après changement Dockerfile)
docker-compose build --no-cache
docker-compose up -d
```

### Mode PROD

```powershell
cd environments\prod

# Logs en temps réel
docker-compose logs -f

# Logs de l'app uniquement
docker-compose logs -f app

# Logs de PostgreSQL
docker-compose logs -f postgres

# Redémarrer
docker-compose restart

# Arrêter (garde les données)
docker-compose down

# Reconstruire
docker-compose build --no-cache
docker-compose up -d

# Démarrer PgAdmin
docker-compose --profile admin up -d

# Arrêter PgAdmin
docker-compose --profile admin down
```

---

## 🐛 DÉPANNAGE

### L'application ne démarre pas

```powershell
# Voir les logs détaillés
cd environments\dev  # ou prod
docker-compose logs -f

# Reconstruire complètement
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### Port 8080 déjà utilisé

```powershell
# Trouver le processus
netstat -ano | findstr :8080

# Tuer le processus
taskkill /PID <PID> /F

# Ou changer le port dans docker-compose.yml
```

### Données perdues en PROD

```powershell
# Vérifier les volumes
docker volume ls

# Vérifier le dossier data
dir environments\prod\data\postgres

# Restaurer depuis backup
.\restore-db.ps1
```

---

## 📚 DOCUMENTATION DÉTAILLÉE

- **Structure environnements** : `environments/README.md`
- **Déploiement PROD** : `DEPLOIEMENT_WINDOWS.md`
- **Démarrage rapide** : `QUICK_START_WINDOWS.md`
- **Code source** : `src/`
- **Documentation technique** : `docs/`

---

## 🎯 RÉCAPITULATIF

| Action | Commande |
|--------|----------|
| **Démarrer DEV** | `.\start-dev.ps1` |
| **Démarrer PROD** | `.\start-prod.ps1` |
| **Backup** | `.\backup-db.ps1` |
| **Restore** | `.\restore-db.ps1` |
| **Logs DEV** | `cd environments\dev ; docker-compose logs -f` |
| **Logs PROD** | `cd environments\prod ; docker-compose logs -f` |
| **Arrêter** | `cd environments\[dev|prod] ; docker-compose down` |

---

**Date :** 28 Décembre 2025  
**Application :** 🛒 ShopTracker  
**Version :** 1.0.0-SNAPSHOT

