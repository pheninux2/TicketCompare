# 🚀 Guide Rapide - Persistance H2

## ✅ C'EST DÉJÀ CONFIGURÉ !

Par défaut, l'application H2 sauvegarde maintenant **automatiquement** les données sur le disque.

---

## 🎯 Utilisation Simple

### 1. Démarrer avec Persistance (Mode par défaut)

```bash
cd C:\Users\pheni\IdeaProjects\TicketCompare
docker-compose -f docker/docker-compose-h2.yml up -d
```

**✅ C'est tout ! Les données seront sauvegardées automatiquement.**

### 2. Vérifier que ça fonctionne

1. Scanner un ticket
2. Arrêter : `docker-compose -f docker/docker-compose-h2.yml down`
3. Redémarrer : `docker-compose -f docker/docker-compose-h2.yml up -d`
4. **Les données sont toujours là !** ✅

---

## 🛠️ Utilisation Avancée

### Script PowerShell (Recommandé)

```powershell
# Démarrer (mode persistant)
.\scripts\manage-h2.ps1 start

# Démarrer (mode mémoire)
.\scripts\manage-h2.ps1 start -Mode mem

# Arrêter
.\scripts\manage-h2.ps1 stop

# Redémarrer
.\scripts\manage-h2.ps1 restart

# Voir le statut
.\scripts\manage-h2.ps1 status

# Backup
.\scripts\manage-h2.ps1 backup

# Restore
.\scripts\manage-h2.ps1 restore -BackupFile "h2-backup-20241227.tar.gz"

# Reset (supprimer toutes les données)
.\scripts\manage-h2.ps1 reset

# Voir les logs
.\scripts\manage-h2.ps1 logs

# Ouvrir H2 Console
.\scripts\manage-h2.ps1 console
```

---

## 🔧 Configuration Manuelle

### Mode Persistant (par défaut)

```bash
# Données sauvegardées ✅
docker-compose -f docker/docker-compose-h2.yml up -d
```

### Mode Mémoire (données perdues)

```bash
# Données perdues au redémarrage ❌
H2_DB_URL="jdbc:h2:mem:ticketcomparedb" docker-compose -f docker/docker-compose-h2.yml up -d
```

---

## 📊 Accès H2 Console

**URL :** http://localhost:8080/h2-console

**Paramètres :**
```
JDBC URL: jdbc:h2:file:/app/data/ticketcomparedb
Username: sa
Password: (vide)
```

---

## 💾 Backup et Restore

### Backup Rapide

```powershell
.\scripts\manage-h2.ps1 backup
# Crée: h2-backup-YYYYMMDD-HHMMSS.tar.gz
```

### Restore

```powershell
.\scripts\manage-h2.ps1 restore -BackupFile "h2-backup-20241227-143052.tar.gz"
```

### Backup Manuel

```bash
docker run --rm ^
  -v ticketcompare_h2_data:/data ^
  -v %CD%:/backup ^
  ubuntu tar czf /backup/h2-backup.tar.gz /data
```

---

## 🗑️ Reset Total

```powershell
# Via script (recommandé)
.\scripts\manage-h2.ps1 reset

# Ou manuellement
docker-compose -f docker/docker-compose-h2.yml down
docker volume rm ticketcompare_h2_data
docker-compose -f docker/docker-compose-h2.yml up -d
```

---

## 🔄 Modes Disponibles

| Mode | Commande | Persistance | Usage |
|------|----------|-------------|-------|
| **Fichier** | `start` | ✅ Oui | Développement, Tests |
| **Mémoire** | `start -Mode mem` | ❌ Non | Tests rapides |

---

## ⚙️ Configuration via .env

**1. Créer `.env` :**
```bash
cd docker
copy .env.h2 .env
```

**2. Éditer `.env` :**
```bash
# Mode persistant
H2_DB_URL=jdbc:h2:file:/app/data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
JPA_DDL_AUTO=update

# Mode mémoire
# H2_DB_URL=jdbc:h2:mem:ticketcomparedb
# JPA_DDL_AUTO=create
```

**3. Démarrer :**
```bash
docker-compose -f docker/docker-compose-h2.yml --env-file docker/.env up -d
```

---

## 📁 Où sont les Données ?

**Volume Docker :**
```
ticketcompare_h2_data
```

**Voir les détails :**
```bash
docker volume inspect ticketcompare_h2_data
```

---

## ❓ FAQ

### Les données sont-elles sauvegardées par défaut ?
✅ **OUI** ! Depuis la mise à jour, le mode persistant est activé par défaut.

### Comment désactiver la persistance ?
```bash
H2_DB_URL="jdbc:h2:mem:ticketcomparedb" docker-compose -f docker/docker-compose-h2.yml up -d
```

### Puis-je changer de mode sans perdre les données ?
Non. Pour basculer, faites un backup/restore.

### H2 est-il pour la production ?
❌ **NON**. H2 est pour développement/tests uniquement.  
Pour production, utilisez PostgreSQL : `docker-compose.yml`

### Comment migrer vers PostgreSQL ?
1. Exporter les données H2 (SCRIPT TO 'export.sql')
2. Démarrer PostgreSQL : `docker-compose -f docker/docker-compose.yml up -d`
3. Importer les données

---

## 🆘 Problèmes Courants

### Les données disparaissent au redémarrage
**Cause :** Mode mémoire activé  
**Solution :**
```bash
# Vérifier le mode
docker exec ticketcompare-app-h2 env | grep H2_DB_URL

# Devrait afficher:
# H2_DB_URL=jdbc:h2:file:/app/data/ticketcomparedb
```

### Volume non créé
```bash
# Créer manuellement
docker volume create ticketcompare_h2_data

# Redémarrer
docker-compose -f docker/docker-compose-h2.yml up -d
```

### Erreur de connexion H2 Console
**Solution :**
- Vérifier JDBC URL : `jdbc:h2:file:/app/data/ticketcomparedb`
- Username : `sa`
- Password : (vide)

---

## 📚 Documentation Complète

- `docs/H2_PERSISTANCE_DOCKER.md` - Documentation détaillée
- `docker/.env.h2` - Template de configuration
- `scripts/manage-h2.ps1` - Script de gestion

---

**Date : 27 Décembre 2024**  
**Fonctionnalité : Persistance H2 activée par défaut**  
**Statut : ✅ Prêt à l'emploi**

