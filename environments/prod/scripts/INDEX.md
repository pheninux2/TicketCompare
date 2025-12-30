# 📜 Index des Scripts d'Administration

## 📁 Structure du Dossier

```
environments/prod/scripts/
├── README.md                      # Documentation complète
├── GUIDE_ADMIN_DATABASE.md        # Guide détaillé d'administration
├── INDEX.md                       # Ce fichier
│
├── 🪟 Scripts Windows PowerShell
│   ├── connect-db.ps1             # Connexion PostgreSQL
│   ├── query-db.ps1               # Requêtes rapides
│   ├── backup-db.ps1              # Backup de la BDD
│   ├── restore-db.ps1             # Restauration
│   ├── start-pgadmin.ps1          # Démarrer PgAdmin
│   ├── stop-pgadmin.ps1           # Arrêter PgAdmin
│   └── test-scripts.ps1           # Test des scripts
│
└── 🐧 Scripts Linux/WSL
    ├── connect-db.sh              # Connexion PostgreSQL
    └── backup-db.sh               # Backup de la BDD
```

---

## 🚀 Utilisation Rapide

### Windows PowerShell

```powershell
# Naviguer vers le dossier
cd environments\prod\scripts

# Tester l'installation
.\test-scripts.ps1

# Connexion PostgreSQL
.\connect-db.ps1

# Requêtes rapides
.\query-db.ps1

# Backup
.\backup-db.ps1

# Interface graphique
.\start-pgadmin.ps1
```

### Linux/WSL

```bash
# Naviguer vers le dossier
cd environments/prod/scripts

# Rendre les scripts exécutables (première fois)
chmod +x *.sh

# Connexion PostgreSQL
./connect-db.sh

# Backup
./backup-db.sh
```

---

## 📊 Scripts par Catégorie

### 🔐 Accès à la Base de Données

| Script | Plateforme | Description |
|--------|------------|-------------|
| `connect-db.ps1` | Windows | Connexion interactive psql |
| `connect-db.sh` | Linux/WSL | Connexion interactive psql |
| `query-db.ps1` | Windows | Menu de requêtes prédéfinies |

### 💾 Backup & Restauration

| Script | Plateforme | Description |
|--------|------------|-------------|
| `backup-db.ps1` | Windows | Backup avec options avancées |
| `backup-db.sh` | Linux/WSL | Backup simple |
| `restore-db.ps1` | Windows | Restauration interactive |

### 🖥️ Interface Graphique

| Script | Plateforme | Description |
|--------|------------|-------------|
| `start-pgadmin.ps1` | Windows | Démarrer PgAdmin |
| `stop-pgadmin.ps1` | Windows | Arrêter PgAdmin |

### 🧪 Tests & Maintenance

| Script | Plateforme | Description |
|--------|------------|-------------|
| `test-scripts.ps1` | Windows | Vérification de l'installation |

---

## 📚 Documentation

### Guides Disponibles

1. **README.md**
   - Vue d'ensemble complète
   - Cas d'usage fréquents
   - Exemples détaillés
   - Dépannage

2. **GUIDE_ADMIN_DATABASE.md**
   - Documentation PostgreSQL
   - Commandes SQL avancées
   - Sécurité et bonnes pratiques
   - Maintenance et optimisation

3. **INDEX.md** (ce fichier)
   - Vue d'ensemble rapide
   - Navigation dans les scripts

---

## 🔑 Informations Importantes

### Identifiants PostgreSQL
```
Database : shoptracker
User     : shoptracker_admin
Password : ShopTracker2025!Secure
Port     : 5432 (localhost)
```

### Identifiants PgAdmin
```
URL      : http://localhost:5050
Email    : admin@shoptracker.local
Password : AdminSecure2025!
```

### Dossiers Importants
```
../backups/     # Backups de la base de données
../data/        # Données persistantes PostgreSQL
../logs/        # Logs de l'application
```

---

## ⚡ Quick Start

### Première Utilisation

**Windows :**
```powershell
# 1. Tester l'installation
.\test-scripts.ps1

# 2. Se connecter pour explorer
.\connect-db.ps1

# 3. Faire un premier backup
.\backup-db.ps1
```

**Linux/WSL :**
```bash
# 1. Rendre exécutables
chmod +x *.sh

# 2. Se connecter
./connect-db.sh

# 3. Backup
./backup-db.sh
```

---

## 🎯 Exemples par Besoin

### Je veux voir les données

```powershell
# Option 1 : Menu interactif
.\query-db.ps1

# Option 2 : Connexion directe
.\connect-db.ps1
# Puis : SELECT * FROM tickets LIMIT 10;
```

### Je veux faire un backup

```powershell
# Backup complet
.\backup-db.ps1

# Backup SQL (lisible)
.\backup-db.ps1 -Format sql

# Backup compressé
.\backup-db.ps1 -Format sql -Compress
```

### Je veux restaurer des données

```powershell
# Mode interactif (sélection du backup)
.\restore-db.ps1

# Restaurer un backup spécifique
.\restore-db.ps1 backup_20251230_140000.dump
```

### Je veux une interface graphique

```powershell
# Démarrer PgAdmin
.\start-pgadmin.ps1

# Ouvre automatiquement http://localhost:5050
```

### Je veux ajouter des données

```powershell
# Se connecter
.\connect-db.ps1

# Puis exécuter du SQL
INSERT INTO stores (name, address, created_at)
VALUES ('Nouveau Magasin', '123 Rue Test', NOW());
```

---

## 🔧 Maintenance Recommandée

### Quotidienne
- Vérifier les logs : `docker logs shoptracker-db --tail 50`

### Hebdomadaire
- Faire un backup : `.\backup-db.ps1`
- Vérifier la taille de la BDD : `.\query-db.ps1` (option 7)

### Mensuelle
- Nettoyer les anciens backups (>30 jours)
- Optimiser la BDD : `VACUUM ANALYZE;` dans psql
- Tester une restauration

---

## 🚨 Dépannage Express

| Problème | Solution |
|----------|----------|
| Conteneur non démarré | `cd ..\.. && .\start-prod.ps1` |
| "role root does not exist" | Toujours utiliser `-U shoptracker_admin` |
| Mot de passe incorrect | Vérifier `../.env` ou utiliser le défaut |
| Script non trouvé | Vérifier que vous êtes dans `environments/prod/scripts` |
| Permission denied (.sh) | `chmod +x *.sh` |

---

## 📞 Support

### Logs utiles
```powershell
# Logs PostgreSQL
docker logs shoptracker-db --tail 100 -f

# Logs PgAdmin
docker logs shoptracker-pgadmin --tail 50 -f

# Statut des conteneurs
docker ps
```

### Commandes de diagnostic
```powershell
# Vérifier PostgreSQL
docker exec shoptracker-db pg_isready -U shoptracker_admin

# Vérifier la connexion
docker exec shoptracker-db psql -U shoptracker_admin -d shoptracker -c "\dt"

# Espace disque
docker system df
```

---

## 🎓 Ressources d'Apprentissage

### Pour débuter
1. Commencez par `test-scripts.ps1` pour vérifier votre environnement
2. Utilisez `connect-db.ps1` pour explorer la base
3. Pratiquez avec `query-db.ps1` pour les requêtes courantes
4. Faites des backups réguliers avec `backup-db.ps1`

### Pour approfondir
- Consultez `GUIDE_ADMIN_DATABASE.md`
- Explorez PgAdmin avec `start-pgadmin.ps1`
- Lisez la documentation PostgreSQL
- Créez vos propres scripts personnalisés

---

## ✅ Checklist de Sécurité

- [ ] Changer les mots de passe par défaut
- [ ] Configurer des backups automatiques
- [ ] Tester les restaurations régulièrement
- [ ] Ne pas commiter les fichiers `.env`
- [ ] Limiter les accès réseau à PostgreSQL
- [ ] Surveiller la taille de la base
- [ ] Auditer les logs régulièrement

---

**📅 Dernière mise à jour : 2025-12-30**  
**📍 Version : 1.0**  
**🔒 Environnement : Production**

---

## 🌟 Contribution

Ces scripts sont conçus pour être extensibles. N'hésitez pas à :
- Créer vos propres scripts basés sur ces exemples
- Améliorer les scripts existants
- Partager vos cas d'usage

---

**✨ Tous les outils sont prêts à l'emploi !**

Pour démarrer immédiatement :
```powershell
.\test-scripts.ps1
```

