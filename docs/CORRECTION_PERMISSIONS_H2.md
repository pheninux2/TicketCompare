# 🔧 Correction Problème de Permissions H2

## ❌ Problème Identifié

**Erreur :**
```
java.nio.file.AccessDeniedException: /app/data/ticketcomparedb.mv.db
java.nio.file.AccessDeniedException: /app/data/ticketcomparedb.trace.db
```

**Cause :**
Le conteneur Docker crée le volume `/app/data` avec les permissions **root**, mais l'application tourne avec l'utilisateur **appuser (UID 1001)** qui n'a pas les droits d'écriture.

---

## ✅ Solutions Appliquées

### 1. Dockerfile Corrigé

**Ajout du répertoire `/app/data` :**
```dockerfile
# Créer les dossiers nécessaires et ajuster les permissions
RUN mkdir -p /app/uploads /app/logs /app/data && \
    chown -R appuser:appuser /app && \
    chmod -R 755 /app
```

### 2. Docker-Compose Corrigé

**Ajout du paramètre `user` :**
```yaml
services:
  app:
    user: "1001:1001"  # ← NOUVEAU: Force l'exécution avec UID/GID de appuser
    ...
```

Cela force Docker à créer les volumes avec les bonnes permissions dès le départ.

---

## 🚀 Comment Corriger (3 Méthodes)

### Méthode 1 : Script Automatique (Recommandé)

**Sur Windows - Double-clic :**
```
fix-permissions.bat
```

**Sur Windows - PowerShell :**
```powershell
.\scripts\fix-permissions.ps1
```

**Sur Linux/Mac :**
```bash
bash scripts/fix-permissions.sh
```

### Méthode 2 : Commandes Manuelles

```bash
# 1. Arrêter
docker-compose -f docker/docker-compose-h2.yml down

# 2. Supprimer le volume avec mauvaises permissions
docker volume rm ticketcompare_h2_data

# 3. Rebuild (applique les corrections)
docker-compose -f docker/docker-compose-h2.yml build --no-cache

# 4. Redémarrer
docker-compose -f docker/docker-compose-h2.yml up -d

# 5. Vérifier les logs
docker-compose -f docker/docker-compose-h2.yml logs -f
```

### Méthode 3 : Reset Complet (Si problèmes persistent)

```bash
# Arrêter et supprimer tout
docker-compose -f docker/docker-compose-h2.yml down -v

# Supprimer l'image
docker rmi ticketcompare-app-h2 2>/dev/null || true

# Rebuild from scratch
docker-compose -f docker/docker-compose-h2.yml build --no-cache

# Redémarrer
docker-compose -f docker/docker-compose-h2.yml up -d
```

---

## 🔍 Vérification du Succès

### Logs Attendus (Succès) ✅

```
✅ Starting TicketCompareApplication
✅ Tomcat initialized with port 8080
✅ HikariPool-1 - Start completed
✅ Started TicketCompareApplication in X.XXX seconds
```

### Logs d'Erreur (Échec) ❌

```
❌ AccessDeniedException: /app/data/ticketcomparedb.mv.db
❌ Log file error
```

### Test de l'Application

```bash
# Test 1: Conteneur en cours d'exécution
docker ps | grep ticketcompare

# Test 2: Application répond
curl http://localhost:8080

# Test 3: Créer des données
# - Aller sur http://localhost:8080
# - Scanner un ticket ou ajouter des produits

# Test 4: Redémarrer et vérifier persistance
docker-compose -f docker/docker-compose-h2.yml restart
# Les données doivent toujours être là ✅
```

---

## 📊 Détails Techniques

### Pourquoi ce Problème ?

**Avant la correction :**
```
1. Docker crée le volume /app/data (propriétaire: root:root)
2. Application essaie d'écrire (utilisateur: appuser UID 1001)
3. Permission refusée ❌
```

**Après la correction :**
```
1. Dockerfile crée /app/data avec chown appuser:appuser
2. docker-compose force user: "1001:1001"
3. Docker crée le volume avec UID/GID 1001
4. Application peut écrire ✅
```

### Permissions des Fichiers

**Dans le conteneur :**
```bash
docker exec ticketcompare-app-h2 ls -la /app/data

# Devrait afficher:
drwxr-xr-x appuser appuser /app/data
-rw-r--r-- appuser appuser ticketcomparedb.mv.db
```

**Sur l'hôte (volume Docker) :**
```bash
# Linux/Mac
docker volume inspect ticketcompare_h2_data

# Windows
docker volume inspect ticketcompare_h2_data
```

---

## 🛠️ Troubleshooting

### Problème : Le volume existe déjà

**Symptôme :**
```
Error response from daemon: remove ticketcompare_h2_data: volume is in use
```

**Solution :**
```bash
# Arrêter tous les conteneurs
docker-compose -f docker/docker-compose-h2.yml down

# Réessayer
docker volume rm ticketcompare_h2_data
```

### Problème : Permissions toujours incorrectes

**Symptôme :**
Même après correction, erreurs de permissions

**Solution :**
```bash
# Reset complet
docker-compose -f docker/docker-compose-h2.yml down -v
docker system prune -af --volumes
docker-compose -f docker/docker-compose-h2.yml build --no-cache
docker-compose -f docker/docker-compose-h2.yml up -d
```

### Problème : Build échoue

**Symptôme :**
```
ERROR: failed to solve: process "/bin/sh -c ..." did not complete successfully
```

**Solution :**
```bash
# Vérifier l'espace disque
docker system df

# Nettoyer si nécessaire
docker system prune -a

# Rebuild
docker-compose -f docker/docker-compose-h2.yml build --no-cache
```

---

## 📝 Fichiers Modifiés

### 1. `docker/Dockerfile`

**Ajouté :**
- Création du répertoire `/app/data`
- `chown` pour donner les droits à appuser
- `chmod 755` pour les permissions correctes

### 2. `docker/docker-compose-h2.yml`

**Ajouté :**
- `user: "1001:1001"` pour forcer l'UID/GID

### 3. Scripts Créés

- ✅ `fix-permissions.bat` - Script Windows (batch)
- ✅ `scripts/fix-permissions.ps1` - Script PowerShell
- ✅ `scripts/fix-permissions.sh` - Script Linux/Mac

---

## 🎯 Résumé

### Cause du Problème
- Volume Docker créé avec permissions root
- Application tourne avec utilisateur non-root (UID 1001)
- Pas de droits d'écriture → AccessDeniedException

### Solution Appliquée
1. ✅ Dockerfile : Création de `/app/data` avec bonnes permissions
2. ✅ Docker-Compose : Ajout `user: "1001:1001"`
3. ✅ Scripts : Automatisation de la correction

### Résultat
- ✅ Application peut écrire dans `/app/data`
- ✅ Base H2 peut être créée et modifiée
- ✅ Persistance des données fonctionnelle
- ✅ Sécurité maintenue (utilisateur non-root)

---

## 🚀 Action Requise

**Pour corriger immédiatement :**

```bash
# Windows (plus simple)
fix-permissions.bat

# OU PowerShell
.\scripts\fix-permissions.ps1

# OU Commandes manuelles
docker-compose -f docker/docker-compose-h2.yml down
docker volume rm ticketcompare_h2_data
docker-compose -f docker/docker-compose-h2.yml build --no-cache
docker-compose -f docker/docker-compose-h2.yml up -d
```

**Temps estimé :** 3-5 minutes (rebuild complet)

---

**Date : 27 Décembre 2024**  
**Problème : Permissions H2 AccessDeniedException**  
**Statut : ✅ CORRIGÉ - Scripts prêts à exécuter**

