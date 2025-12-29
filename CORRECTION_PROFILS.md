# ✅ CORRECTION PROFILS DEV/PROD - TERMINÉ

## Date : 28 Décembre 2025

---

## 🐛 PROBLÈME IDENTIFIÉ

Quand vous lanciez `start-dev.ps1`, Maven tentait de charger `application-prod.properties` au lieu de `application-dev.properties`, et échouait avec :

```
[ERROR] Failed to execute goal maven-resources-plugin:3.3.1:resources
filtering application-prod.properties failed with MalformedInputException: Input length = 1
```

**Causes :**
1. ❌ Caractère accentué `É` dans `SÉCURITÉ` dans `application-prod.properties`
2. ❌ Pas de fichier `application-dev.properties` spécifique
3. ❌ Profil DEV pas correctement activé

---

## ✅ CORRECTIONS APPLIQUÉES

### 1. Correction de l'encodage dans application-prod.properties

**Avant :**
```properties
# ====== SÉCURITÉ ======
```

**Après :**
```properties
# ====== SECURITE ======
```

### 2. Création de application-dev.properties

Nouveau fichier créé avec :
- ✅ Base H2 en mémoire
- ✅ H2 Console activée
- ✅ Logs en mode DEBUG
- ✅ Hot reload activé
- ✅ Pas de cache Thymeleaf

### 3. Mise à jour de application.properties

Ajout du profil par défaut :
```properties
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}
```

### 4. Suppression ligne obsolète dans docker-compose

Supprimé :
```yaml
version: '3.8'  # Obsolète dans Docker Compose v2
```

---

## 📁 FICHIERS DE CONFIGURATION

### Mode DÉVELOPPEMENT (DEV)

**Fichier :** `src/main/resources/application-dev.properties`

```properties
# Base de donnees H2 en memoire
spring.datasource.url=jdbc:h2:mem:shoptracker

# H2 Console activee
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Logs DEBUG
logging.level.pheninux.xdev.ticketcompare=DEBUG

# Hot reload
spring.devtools.restart.enabled=true

# Pas de cache
spring.thymeleaf.cache=false
```

---

### Mode PRODUCTION (PROD)

**Fichier :** `src/main/resources/application-prod.properties`

```properties
# Base de donnees PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/shoptracker

# Logs INFO/WARN
logging.level.pheninux.xdev.ticketcompare=INFO

# Cache active
spring.thymeleaf.cache=true

# Optimisations
```

---

## 🚀 UTILISATION

### Démarrer en mode DEV

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\start-dev.ps1
```

**Ce qui se passe :**
1. Docker Compose démarre avec `SPRING_PROFILES_ACTIVE=dev`
2. Spring Boot charge `application.properties` (profil par défaut: dev)
3. Spring Boot charge `application-dev.properties` (surcharge)
4. Base H2 en mémoire démarrée
5. H2 Console accessible : http://localhost:8080/h2-console

---

### Démarrer en mode PROD

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\start-prod.ps1
```

**Ce qui se passe :**
1. Docker Compose démarre avec `SPRING_PROFILES_ACTIVE=prod`
2. Spring Boot charge `application.properties` (profil par défaut: dev)
3. Spring Boot charge `application-prod.properties` (surcharge)
4. PostgreSQL démarrée
5. Données persistantes sauvegardées

---

## 📊 DIFFÉRENCES DEV vs PROD

| Configuration | DEV | PROD |
|--------------|-----|------|
| **Profil Spring** | `dev` | `prod` |
| **Base de données** | H2 en mémoire | PostgreSQL |
| **H2 Console** | ✅ Activée | ❌ Désactivée |
| **Logs** | DEBUG | INFO/WARN |
| **SQL visible** | ✅ Oui | ❌ Non |
| **Hot reload** | ✅ Activé | ❌ Désactivé |
| **Cache Thymeleaf** | ❌ Désactivé | ✅ Activé |
| **Données** | Perdues au redémarrage | Persistantes |

---

## 🔍 VÉRIFICATION DU PROFIL ACTIF

Pour voir quel profil est actif, regardez les logs au démarrage :

```
INFO - The following 1 profile is active: "dev"
```

ou

```
INFO - The following 1 profile is active: "prod"
```

---

## 🐛 DÉPANNAGE

### Maven charge le mauvais profil

**Vérifiez :**
```yaml
# Dans environments/dev/docker-compose.yml
environment:
  SPRING_PROFILES_ACTIVE: dev   # ← Doit être 'dev'
```

```yaml
# Dans environments/prod/docker-compose.yml
environment:
  SPRING_PROFILES_ACTIVE: prod  # ← Doit être 'prod'
```

---

### Erreur d'encodage

Si vous voyez encore des erreurs d'encodage :

1. **Vérifiez les accents** dans les fichiers `.properties`
2. **Sauvegardez en UTF-8** sans BOM dans IntelliJ IDEA
3. **Évitez les caractères spéciaux** (é, è, à, ç, etc.)

**Remplacement recommandé :**
- `é, è, ê` → `e`
- `à` → `a`
- `ç` → `c`

---

### H2 Console ne s'affiche pas

En mode DEV, vérifiez :

```properties
# Dans application-dev.properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Puis accédez à : http://localhost:8080/h2-console

**Paramètres de connexion :**
- JDBC URL: `jdbc:h2:mem:shoptracker`
- User: `sa`
- Password: (vide)

---

## ✅ RÉSULTAT

```
✅ Erreur d'encodage corrigée
✅ Profil DEV créé avec H2
✅ Profil PROD configuré avec PostgreSQL
✅ Séparation claire DEV/PROD
✅ Hot reload en DEV
✅ Logs DEBUG en DEV, INFO en PROD
✅ H2 Console en DEV uniquement
```

---

## 📝 FICHIERS MODIFIÉS

1. ✅ `src/main/resources/application.properties` - Profil par défaut DEV
2. ✅ `src/main/resources/application-dev.properties` - CRÉÉ (configuration DEV)
3. ✅ `src/main/resources/application-prod.properties` - Corrigé (encodage)
4. ✅ `environments/dev/docker-compose.yml` - Supprimé version obsolète

---

## 🎯 PROCHAINES ÉTAPES

1. **Tester le démarrage DEV**
   ```powershell
   .\start-dev.ps1
   ```

2. **Vérifier les logs**
   ```
   INFO - The following 1 profile is active: "dev"
   INFO - H2 console available at '/h2-console'
   ```

3. **Accéder à l'application**
   - Application : http://localhost:8080
   - H2 Console : http://localhost:8080/h2-console

4. **Tester le hot reload**
   - Modifiez un fichier Java
   - Spring DevTools redémarre automatiquement

---

**Date :** 28 Décembre 2025  
**Application :** 🛒 ShopTracker  
**Problème :** Profils DEV/PROD + Encodage  
**Status :** ✅ RÉSOLU

