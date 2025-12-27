# 🔧 Correction Erreur H2 - AUTO_SERVER

## ❌ Problème Identifié

**Erreur :**
```
Feature not supported: "AUTO_SERVER=TRUE && DB_CLOSE_ON_EXIT=FALSE" [50100-240]
```

**Cause :**
H2 ne supporte pas l'utilisation simultanée de :
- `AUTO_SERVER=TRUE` 
- `DB_CLOSE_ON_EXIT=FALSE`

Ces deux paramètres sont incompatibles dans H2 version 2.x.

---

## ✅ Solution Appliquée

### Fichiers Corrigés

#### 1. `application.properties`

**Avant :**
```properties
spring.datasource.url=${H2_DB_URL:jdbc:h2:file:./data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE}
```

**Après :**
```properties
spring.datasource.url=${H2_DB_URL:jdbc:h2:file:./data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE}
```

#### 2. `docker/.env.h2`

**Avant :**
```
H2_DB_URL=jdbc:h2:file:/app/data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
```

**Après :**
```
H2_DB_URL=jdbc:h2:file:/app/data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE
```

#### 3. `docker/docker-compose-h2.yml`

**Avant :**
```yaml
H2_DB_URL: ${H2_DB_URL:-jdbc:h2:file:/app/data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE}
```

**Après :**
```yaml
H2_DB_URL: ${H2_DB_URL:-jdbc:h2:file:/app/data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE}
```

---

## 📋 Impact de la Suppression de AUTO_SERVER

### Qu'est-ce que AUTO_SERVER ?

`AUTO_SERVER=TRUE` permettait à plusieurs processus/applications de se connecter à la même base H2 en mode fichier simultanément.

### Impact de sa Suppression

**Ce qui reste fonctionnel :**
- ✅ Persistance des données (mode fichier)
- ✅ Données sauvegardées entre redémarrages
- ✅ Une application peut accéder à la base

**Limitation :**
- ❌ Une seule connexion à la fois possible
- ❌ Si vous essayez d'ouvrir H2 Console pendant que l'app tourne, erreur possible

### Solution de Contournement

**Pour accéder à H2 Console pendant que l'app tourne :**

Option A : Utiliser H2 Console intégré
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/ticketcomparedb
```
✅ Fonctionne car même processus

Option B : Arrêter temporairement l'app
```bash
docker-compose -f docker/docker-compose-h2.yml stop
# Accéder à H2 Console externe
docker-compose -f docker/docker-compose-h2.yml start
```

---

## 🚀 Redémarrage

### Étapes

1. **Arrêter les conteneurs**
```bash
docker-compose -f docker/docker-compose-h2.yml down
```

2. **Rebuild (optionnel mais recommandé)**
```bash
docker-compose -f docker/docker-compose-h2.yml build --no-cache
```

3. **Redémarrer**
```bash
docker-compose -f docker/docker-compose-h2.yml up -d
```

4. **Vérifier les logs**
```bash
docker-compose -f docker/docker-compose-h2.yml logs -f
```

### Logs Attendus (Succès)

```
✅ Starting TicketCompareApplication
✅ Tomcat initialized with port 8080
✅ HikariPool-1 - Start completed
✅ Started TicketCompareApplication in X.XXX seconds
```

---

## 🔍 Vérification

### Test de Démarrage

```bash
# Vérifier que le conteneur tourne
docker ps | findstr ticketcompare

# Vérifier les logs (pas d'erreur)
docker-compose -f docker/docker-compose-h2.yml logs | findstr "ERROR"

# Accéder à l'application
curl http://localhost:8080
```

### Test de Persistance

1. **Créer des données**
   - Aller sur http://localhost:8080
   - Scanner un ticket ou ajouter des produits

2. **Redémarrer**
   ```bash
   docker-compose -f docker/docker-compose-h2.yml restart
   ```

3. **Vérifier**
   - Les données doivent toujours être là ✅

---

## 📚 Documentation Mise à Jour

Les fichiers suivants ont été corrigés :
- ✅ `application.properties`
- ✅ `docker/.env.h2`
- ✅ `docker/docker-compose-h2.yml`

La documentation dans `docs/H2_PERSISTANCE_DOCKER.md` reste valide.

---

## ⚠️ Notes Importantes

### H2 en Production

H2 reste adapté uniquement pour :
- ✅ Développement
- ✅ Tests
- ✅ Démonstrations

Pour la production, utilisez PostgreSQL :
```bash
docker-compose -f docker/docker-compose.yml up -d
```

### Accès Concurrent

Si vous avez besoin d'accès concurrent (plusieurs apps sur la même base H2), deux solutions :

**Solution 1 : Mode TCP/IP Server (plus complexe)**
```
jdbc:h2:tcp://localhost/~/data/ticketcomparedb
```

**Solution 2 : Utiliser PostgreSQL**
```yaml
# docker-compose.yml déjà configuré
docker-compose up -d
```

---

## ✅ Résolution

**Problème :** Incompatibilité AUTO_SERVER + DB_CLOSE_ON_EXIT  
**Solution :** Suppression de AUTO_SERVER  
**Impact :** Aucun pour usage normal (une app à la fois)  
**Persistance :** ✅ Toujours fonctionnelle  

**L'application peut maintenant démarrer correctement !** 🎉

---

**Date : 27 Décembre 2024**  
**Erreur : H2 AUTO_SERVER incompatible**  
**Statut : ✅ CORRIGÉ**

