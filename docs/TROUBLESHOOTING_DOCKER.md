# 🐳 Résolution des Problèmes Docker - TicketCompare

Guide de dépannage pour les problèmes liés à Docker.

## 🔴 Erreur : Docker Desktop n'est pas en cours d'exécution

### Symptômes
```
open //./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified
```

### Solutions

#### Solution 1 : Démarrer Docker Desktop Automatiquement
```powershell
.\scripts\start-docker-desktop.bat
```

#### Solution 2 : Démarrer Docker Desktop Manuellement
1. Chercher "Docker Desktop" dans le menu Démarrer
2. Lancer Docker Desktop
3. Attendre que l'icône Docker dans la barre des tâches soit verte
4. Relancer votre script

#### Solution 3 : Vérifier l'état de Docker
```powershell
# Vérifier si Docker fonctionne
docker info

# Si erreur, redémarrer Docker Desktop
```

#### Solution 4 : Réinstaller Docker Desktop
Si Docker ne démarre pas du tout :
1. Désinstaller Docker Desktop
2. Redémarrer Windows
3. Télécharger depuis : https://www.docker.com/products/docker-desktop
4. Installer en tant qu'administrateur
5. Redémarrer Windows

---

## 🔴 Erreur : version attribute is obsolete

### Symptômes
```
the attribute `version` is obsolete, it will be ignored
```

### Solution
✅ **Déjà corrigé !** Les fichiers docker-compose.yml ont été mis à jour.

Si vous voyez encore cette erreur, vérifiez que vous utilisez les derniers fichiers.

---

## 🔴 Erreur : Port 8080 déjà utilisé

### Symptômes
```
Bind for 0.0.0.0:8080 failed: port is already allocated
```

### Solutions

#### Solution 1 : Trouver et arrêter le processus
```powershell
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

#### Solution 2 : Changer le port de l'application
Éditez `docker/.env` :
```env
APP_PORT=8081
```

Puis relancez :
```powershell
.\scripts\rebuild-docker.bat
```

---

## 🔴 Erreur : Cannot connect to PostgreSQL

### Symptômes
```
Connection to localhost:5432 refused
```

### Solutions

#### Solution 1 : Vérifier que PostgreSQL est démarré
```powershell
docker ps
```

Vous devriez voir `ticketcompare-db` en cours d'exécution.

#### Solution 2 : Redémarrer PostgreSQL
```powershell
cd docker
docker-compose restart postgres
```

#### Solution 3 : Voir les logs PostgreSQL
```powershell
docker-compose logs postgres
```

#### Solution 4 : Réinitialiser complètement
```powershell
cd docker
docker-compose down -v
docker-compose up -d
```

---

## 🔴 Erreur : Unable to get image

### Symptômes
```
unable to get image 'docker-app': error during connect
```

### Cause
Docker Desktop n'est pas complètement démarré.

### Solution
1. Attendre 30-60 secondes après le démarrage de Docker Desktop
2. Vérifier avec : `docker info`
3. Relancer le script

---

## 🔴 Erreur : Build failed

### Symptômes
```
ERROR: failed to solve: executor failed running
```

### Solutions

#### Solution 1 : Nettoyer le cache Docker
```powershell
docker system prune -a
```

#### Solution 2 : Reconstruire sans cache
```powershell
.\scripts\rebuild-docker.bat
```

#### Solution 3 : Vérifier l'espace disque
```powershell
docker system df
```

Si l'espace est insuffisant :
```powershell
docker system prune -a --volumes
```

---

## 🔴 Erreur : Container keeps restarting

### Symptômes
```
docker ps
# Container status: Restarting
```

### Solutions

#### Solution 1 : Voir les logs
```powershell
.\scripts\logs.bat
```

#### Solution 2 : Vérifier la configuration
```powershell
cd docker
docker-compose config
```

#### Solution 3 : Redémarrer en mode interactif
```powershell
cd docker
docker-compose down
docker-compose up
# Observer les logs en direct
```

---

## 🔴 Erreur : Database connection failed

### Symptômes
Application démarre mais ne peut pas se connecter à la base de données.

### Solutions

#### Solution 1 : Vérifier les variables d'environnement
Éditez `docker/.env` et vérifiez :
```env
POSTGRES_DB=ticketcompare
POSTGRES_USER=ticketuser
POSTGRES_PASSWORD=TicketPass2024!
```

#### Solution 2 : Attendre le démarrage de PostgreSQL
L'application démarre parfois avant PostgreSQL. Attendez 30 secondes et vérifiez :
```powershell
docker-compose logs app
```

#### Solution 3 : Utiliser H2 à la place
Si problème persistant avec PostgreSQL :
```powershell
.\scripts\start-docker-h2.bat
```

---

## 🔴 Erreur : Permission denied

### Symptômes
```
Permission denied while trying to connect to the Docker daemon
```

### Solutions

#### Solution 1 : Exécuter PowerShell en administrateur
1. Clic droit sur PowerShell
2. "Exécuter en tant qu'administrateur"
3. Relancer le script

#### Solution 2 : Ajouter l'utilisateur au groupe Docker
```powershell
# Ouvrir PowerShell en admin
net localgroup docker-users "VOTRE_USERNAME" /add
```
Puis redémarrer Windows.

---

## 🔴 Erreur : Network error

### Symptômes
```
failed to create network
network with name ticketcompare_network already exists
```

### Solution
```powershell
docker network rm ticketcompare_network
docker-compose up -d
```

---

## 🔴 Erreur : Volume error

### Symptômes
```
Error response from daemon: create volume
```

### Solution
```powershell
# Supprimer les volumes
cd docker
docker-compose down -v

# Recréer
docker-compose up -d
```

---

## 🟡 Alternative : Mode Développement Local (Sans Docker)

Si Docker pose trop de problèmes, utilisez le mode local :

```powershell
.\scripts\start-dev.bat
```

**Avantages :**
- ✅ Pas besoin de Docker
- ✅ Base de données H2 en mémoire
- ✅ Démarrage plus rapide
- ✅ Idéal pour le développement

**Inconvénients :**
- ❌ Pas de PostgreSQL
- ❌ Configuration différente de la production
- ❌ Données perdues au redémarrage

---

## 📋 Checklist de Diagnostic

Avant de demander de l'aide, vérifiez :

- [ ] Docker Desktop est installé
- [ ] Docker Desktop est démarré (icône verte)
- [ ] `docker info` fonctionne
- [ ] `docker ps` fonctionne
- [ ] Le port 8080 est libre
- [ ] Le fichier `docker/.env` existe
- [ ] Vous êtes dans le bon dossier
- [ ] Vous avez suffisamment d'espace disque

---

## 🛠️ Commandes de Diagnostic

```powershell
# Vérifier Docker
docker info
docker version

# Lister les conteneurs
docker ps -a

# Voir les logs
docker-compose -f docker/docker-compose.yml logs

# Voir l'utilisation des ressources
docker stats

# Voir l'espace disque
docker system df

# Nettoyer Docker
docker system prune -a

# Supprimer tout (ATTENTION: perte de données)
docker system prune -a --volumes
```

---

## 🚀 Scripts de Dépannage Disponibles

| Script | Usage |
|--------|-------|
| `verify.bat` | Vérifier l'installation complète |
| `start-docker-desktop.bat` | Démarrer Docker Desktop |
| `start-docker.bat` | Démarrer avec PostgreSQL |
| `start-docker-h2.bat` | Démarrer avec H2 |
| `stop-docker.bat` | Arrêter l'application |
| `rebuild-docker.bat` | Reconstruire complètement |
| `logs.bat` | Voir les logs |
| `start-dev.bat` | Mode développement sans Docker |

---

## 📞 Obtenir de l'Aide

Si aucune solution ne fonctionne :

1. **Vérifier l'installation :**
   ```powershell
   .\scripts\verify.bat
   ```

2. **Collecter les informations :**
   ```powershell
   docker version > diagnostic.txt
   docker-compose -f docker/docker-compose.yml config >> diagnostic.txt
   docker-compose -f docker/docker-compose.yml logs >> diagnostic.txt
   ```

3. **Consulter :**
   - [Guide d'installation](INSTALLATION.md)
   - [Documentation principale](../README.md)

---

**Dernière mise à jour : 25 Décembre 2024**

