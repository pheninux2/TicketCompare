# 🔧 Administration Base de Données - Guide Rapide

## 📍 Emplacement des Scripts

Tous les scripts d'administration sont dans :
```
environments/prod/scripts/
```

## 🚀 Accès Rapide

### Windows PowerShell

```powershell
# Se déplacer vers les scripts
cd environments/prod/scripts

# Connexion PostgreSQL
.\connect-db.ps1

# Requêtes rapides
.\query-db.ps1

# Backup
.\backup-db.ps1

# Restauration
.\restore-db.ps1

# PgAdmin (interface graphique)
.\start-pgadmin.ps1
.\stop-pgadmin.ps1
```

### Linux/WSL

```bash
# Connexion PostgreSQL
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker

# Backup
docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c -b -f "/backups/backup_$(date +%Y%m%d_%H%M%S).dump"

# Requête simple
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT * FROM tickets LIMIT 10;"
```

---

## 📂 Scripts Disponibles

| Script | Description | Usage |
|--------|-------------|-------|
| **connect-db.ps1** | Connexion interactive à PostgreSQL | `.\connect-db.ps1` |
| **query-db.ps1** | Menu de requêtes rapides | `.\query-db.ps1` |
| **backup-db.ps1** | Backup de la base de données | `.\backup-db.ps1` |
| **restore-db.ps1** | Restauration de la base | `.\restore-db.ps1` |
| **start-pgadmin.ps1** | Démarrer PgAdmin (GUI) | `.\start-pgadmin.ps1` |
| **stop-pgadmin.ps1** | Arrêter PgAdmin | `.\stop-pgadmin.ps1` |

---

## 🔐 Identifiants

### PostgreSQL
```
Base de données : shoptracker
Utilisateur     : shoptracker_admin
Mot de passe    : ShopTracker2025!Secure
```

### PgAdmin
```
URL         : http://localhost:5050
Email       : admin@shoptracker.local
Mot de passe: AdminSecure2025!
```

---

## 📖 Documentation Complète

Pour la documentation détaillée, consultez :

```
environments/prod/scripts/GUIDE_ADMIN_DATABASE.md
environments/prod/scripts/README.md
```

---

## 🎯 Exemples d'Utilisation

### Voir les derniers tickets
```powershell
cd environments/prod/scripts
.\query-db.ps1
# Choisir option 2
```

### Faire un backup quotidien
```powershell
cd environments/prod/scripts
.\backup-db.ps1
```

### Accéder à l'interface graphique
```powershell
cd environments/prod/scripts
.\start-pgadmin.ps1
# Ouvre automatiquement http://localhost:5050
```

### Ajouter un utilisateur admin
```powershell
cd environments/prod/scripts
.\connect-db.ps1
# Puis dans psql :
UPDATE users SET role = 'ADMIN' WHERE username = 'votre_user';
\q
```

---

## 🚨 Dépannage Rapide

### Conteneur non démarré
```powershell
.\start-prod.ps1
```

### Erreur "role root does not exist"
❌ Mauvais : `docker exec -it shoptracker-db psql`  
✅ Correct : `docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker`

### Voir les logs
```powershell
docker logs shoptracker-db --tail 100 -f
```

---

**📅 Créé le : 2025-12-30**  
**📍 Environnement : Production**  
**✅ Tous les scripts sont prêts à l'emploi !**

