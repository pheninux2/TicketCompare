# ✅ SOLUTION - "open Dockerfile.prod: no such file or directory"

## 🐛 Problème

```
failed to solve: failed to read dockerfile: open Dockerfile.prod: no such file or directory
```

**Cause :** Le `docker-compose.prod.yml` cherche `Dockerfile.prod` dans le mauvais répertoire.

Le fichier était configuré comme :
```yaml
build:
  context: .           # Dossier courant (deploy/)
  dockerfile: Dockerfile.prod   # Cherche dans deploy/Dockerfile.prod
```

Mais `Dockerfile.prod` est à la **racine du projet**, pas dans `deploy/`.

---

## ✅ CORRECTION APPLIQUÉE

J'ai modifié `docker-compose.prod.yml` :

```yaml
build:
  context: ..          # Dossier parent (racine du projet)
  dockerfile: Dockerfile.prod   # Cherche dans /opt/shoptracker/app/Dockerfile.prod
```

Et supprimé la ligne obsolète :
```yaml
version: '3.8'  # Obsolète dans Docker Compose v2
```

---

## 🚀 SOLUTION RAPIDE

### Option A : Retransférer le fichier corrigé (RECOMMANDÉ)

**Sur votre PC Windows :**

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\retransfer-compose.ps1
```

Puis **sur le VPS :**

```bash
cd /opt/shoptracker/scripts && ./deploy-app.sh
```

---

### Option B : Corriger directement sur le VPS

**Sur le VPS :**

```bash
# Éditer le fichier
nano /opt/shoptracker/app/deploy/docker-compose.prod.yml
```

**Modifiez ces lignes :**

```yaml
# AVANT
  app:
    build:
      context: .
      dockerfile: Dockerfile.prod

# APRÈS
  app:
    build:
      context: ..
      dockerfile: Dockerfile.prod
```

**Supprimez aussi :**
```yaml
version: '3.8'  # Supprimez cette ligne
```

Sauvegardez (`Ctrl+X`, `Y`, `Enter`) puis relancez :

```bash
cd /opt/shoptracker/scripts && ./deploy-app.sh
```

---

### Option C : Vérifier que Dockerfile.prod existe

**Sur le VPS :**

```bash
# Vérifier l'existence du Dockerfile.prod
ls -la /opt/shoptracker/app/Dockerfile.prod

# Si manquant, le créer via Git pull
cd /opt/shoptracker/app
git pull origin master
```

---

## 📊 Structure attendue

```
/opt/shoptracker/app/
├── Dockerfile.prod                      ← Doit être ici
├── pom.xml
├── src/
└── deploy/
    ├── docker-compose.prod.yml          ← Référence ../Dockerfile.prod
    ├── .env.production
    └── nginx/
```

---

## 🔍 Après correction

Le build devrait afficher :

```
[*] Build de l'image Docker...
[INFO] Cette étape peut prendre 5-10 minutes...

[+] Building 245.3s (18/18) FINISHED
 => [internal] load build definition from Dockerfile.prod
 => => transferring dockerfile: 1.89kB
 => [internal] load .dockerignore
 => [build 1/5] FROM docker.io/library/maven:3.9-eclipse-temurin-21-alpine
 => CACHED [build 2/5] WORKDIR /app
 => [build 3/5] COPY pom.xml .
 => [build 4/5] COPY src ./src
 => [build 5/5] RUN mvn clean package -DskipTests
 => [runtime 1/6] FROM docker.io/library/eclipse-temurin:21-jre-alpine
 => [runtime 2/6] WORKDIR /app
 => [runtime 3/6] RUN apk add --no-cache curl bash tzdata
 => [runtime 4/6] COPY --from=build /app/target/*.jar app.jar
 => exporting to image
 => => exporting layers
 => => writing image sha256:abc123...

[OK] Image construite avec succès
```

---

## 🐛 Si le problème persiste

### Vérifier le contexte Git

```bash
cd /opt/shoptracker/app
git status
git pull origin master
ls -la Dockerfile.prod
```

### Vérifier les permissions

```bash
chown -R deployer:deployer /opt/shoptracker/app
chmod 644 /opt/shoptracker/app/Dockerfile.prod
```

### Build manuel pour tester

```bash
cd /opt/shoptracker/app
docker build -f Dockerfile.prod -t shoptracker-app:test .
```

---

## ✅ Résultat

```
✅ docker-compose.prod.yml corrigé
✅ Context pointé vers la racine du projet
✅ Ligne version obsolète supprimée
✅ Prêt à builder !
```

---

## 🎯 ACTION IMMÉDIATE

### Méthode 1 (Windows - PLUS SIMPLE) :
```powershell
.\retransfer-compose.ps1
```

### Méthode 2 (VPS) :
```bash
# Corriger le context
sed -i 's|context: \.|context: ..|' /opt/shoptracker/app/deploy/docker-compose.prod.yml

# Supprimer version
sed -i '/^version:/d' /opt/shoptracker/app/deploy/docker-compose.prod.yml
```

**Puis relancez :**
```bash
cd /opt/shoptracker/scripts && ./deploy-app.sh
```

---

**Date :** 30 Décembre 2025  
**Problème :** Dockerfile.prod not found  
**Solution :** Context corrigé dans docker-compose  
**Status :** ✅ CORRIGÉ

