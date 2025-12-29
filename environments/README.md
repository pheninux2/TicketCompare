# 📁 STRUCTURE DES ENVIRONNEMENTS

## 🎯 Organisation

Ce dossier contient les configurations séparées pour chaque environnement.

```
environments/
├── dev/                          ← Développement
│   ├── docker-compose.yml        ← Docker dev (H2 en mémoire)
│   ├── Dockerfile.dev            ← Image dev (hot reload)
│   ├── .env                      ← Configuration dev
│   └── data/                     ← Données temporaires dev
│       ├── uploads/
│       └── logs/
│
└── prod/                         ← Production
    ├── docker-compose.yml        ← Docker prod (PostgreSQL)
    ├── Dockerfile                ← Image prod optimisée
    ├── .env.example              ← Template config prod
    ├── .env                      ← Configuration prod (à créer)
    ├── data/                     ← Données persistantes prod
    │   ├── postgres/             ← Base de données
    │   ├── uploads/              ← Images tickets
    │   ├── logs/                 ← Logs
    │   └── pgadmin/              ← Config PgAdmin
    └── backups/                  ← Backups BDD
```

---

## 🚀 UTILISATION

### Développement

```powershell
# Démarrer en mode développement
.\start-dev.ps1

# Ou manuellement
cd environments\dev
docker-compose up -d
```

**Accès :**
- 🌐 Application : http://localhost:8080
- 🗄️ H2 Console : http://localhost:8080/h2-console
- 🐛 Debug : localhost:5005

---

### Production

```powershell
# Démarrer en mode production
.\start-prod.ps1

# Ou manuellement
cd environments\prod
copy .env.example .env
notepad .env    # Modifier les mots de passe !
docker-compose up -d
```

**Accès :**
- 🌐 Application : http://localhost:8080
- 🔐 PgAdmin (admin) : http://localhost:5050

---

## 🔄 DIFFÉRENCES DEV vs PROD

| Aspect | DEV | PROD |
|--------|-----|------|
| **Base de données** | H2 en mémoire | PostgreSQL persistante |
| **Données** | Temporaires | Sauvegardées sur disque |
| **Hot Reload** | ✅ Activé | ❌ Désactivé |
| **Debug** | Port 5005 ouvert | ❌ Fermé |
| **Logs** | DEBUG | INFO/WARN |
| **Optimisations** | Minimales | JVM optimisé |
| **H2 Console** | ✅ Activée | ❌ Désactivée |
| **PgAdmin** | ❌ Non disponible | ✅ Disponible |
| **Backups** | Non nécessaire | Scripts fournis |

---

## 📝 FICHIERS À CONFIGURER

### Développement (environments/dev/.env)
```env
# Rien à configurer
# Tout est prêt par défaut
```

### Production (environments/prod/.env)
```env
# À MODIFIER OBLIGATOIREMENT :
DB_PASSWORD=VotreMotDePasseSecurise123!
ADMIN_PASSWORD=AdminPasswordSecure456!
ADMIN_EMAIL=votre.email@example.com

# Optionnel :
MAIL_USERNAME=
MAIL_PASSWORD=
STRIPE_API_KEY=
```

---

## 🔒 SÉCURITÉ

### Développement
- ⚠️ Utiliser UNIQUEMENT en local
- ⚠️ Ne JAMAIS exposer sur Internet
- ⚠️ H2 Console accessible sans mot de passe

### Production
- ✅ PostgreSQL accessible uniquement en localhost
- ✅ PgAdmin accessible uniquement en localhost
- ✅ Mots de passe configurables
- ✅ Données chiffrées (selon config PostgreSQL)

---

## 🔄 MIGRATION DEV → PROD

### Méthode 1 : Export/Import données
```powershell
# 1. En DEV : Exporter les données via H2 Console
# 2. En PROD : Importer dans PostgreSQL via PgAdmin
```

### Méthode 2 : Recréer en PROD
```powershell
# Recommandé : Recréer les données de test en PROD
# Les données de DEV sont souvent des données de test
```

---

## 📦 BACKUP (PROD uniquement)

```powershell
# Créer un backup
.\backup-db.ps1

# Restaurer un backup
.\restore-db.ps1
```

Les backups sont sauvegardés dans `environments/prod/backups/`

---

## 🧹 NETTOYAGE

### Développement
```powershell
cd environments\dev
docker-compose down -v  # Supprime aussi les volumes
```

### Production
```powershell
cd environments\prod
docker-compose down  # Garde les données
# OU
docker-compose down -v  # ⚠️ SUPPRIME TOUT (y compris données)
```

---

## 📚 DOCUMENTATION COMPLÈTE

- **Dev** : `environments/dev/` (prêt à l'emploi)
- **Prod** : `DEPLOIEMENT_WINDOWS.md` (guide complet)
- **Quick Start** : `QUICK_START_WINDOWS.md`

---

**Date :** 28 Décembre 2025  
**Application :** 🛒 ShopTracker  
**Version :** 1.0.0

