# 🆕 Changements : docker-compose-h2.yml

## ✅ Persistance H2 Activée par Défaut

---

## 📋 Modifications Appliquées

### Avant (Mode Mémoire - Données Perdues)

```yaml
services:
  app:
    environment:
      # H2 Console Configuration
      SPRING_H2_CONSOLE_ENABLED: "true"
      # ... pas de config H2_DB_URL
    volumes:
      - app_uploads:/app/uploads
      - app_logs:/app/logs
      # ❌ Pas de volume pour les données

volumes:
  app_uploads: ...
  app_logs: ...
  # ❌ Pas de volume h2_data
```

**Résultat :** Les données étaient perdues à chaque redémarrage ❌

---

### Après (Mode Fichier - Données Persistées)

```yaml
services:
  app:
    environment:
      # H2 Database Configuration - NOUVEAU !
      H2_DB_URL: ${H2_DB_URL:-jdbc:h2:file:/app/data/ticketcomparedb;...}
      H2_DB_USERNAME: ${H2_DB_USERNAME:-sa}
      H2_DB_PASSWORD: ${H2_DB_PASSWORD:-}
      JPA_DDL_AUTO: ${JPA_DDL_AUTO:-update}  # NOUVEAU !
      
      # H2 Console Configuration
      SPRING_H2_CONSOLE_ENABLED: "true"
    volumes:
      - h2_data:/app/data          # ✅ NOUVEAU - Persistance !
      - app_uploads:/app/uploads
      - app_logs:/app/logs

volumes:
  h2_data:                         # ✅ NOUVEAU !
    driver: local
    name: ticketcompare_h2_data
  app_uploads: ...
  app_logs: ...
```

**Résultat :** Les données sont sauvegardées et conservées ✅

---

## 🎯 Nouveaux Paramètres

| Variable | Valeur par Défaut | Description |
|----------|-------------------|-------------|
| `H2_DB_URL` | `jdbc:h2:file:/app/data/ticketcomparedb` | URL de la base H2 (file = persistant) |
| `H2_DB_USERNAME` | `sa` | Nom d'utilisateur H2 |
| `H2_DB_PASSWORD` | (vide) | Mot de passe H2 |
| `JPA_DDL_AUTO` | `update` | Stratégie Hibernate (conserve les données) |

---

## 🔄 Nouveau Volume

```yaml
h2_data:
  driver: local
  name: ticketcompare_h2_data
```

**Contenu :**
- `ticketcomparedb.mv.db` - Fichier de base de données H2
- `ticketcomparedb.trace.db` - Fichier de logs H2

**Emplacement :**
- Géré par Docker
- Voir avec : `docker volume inspect ticketcompare_h2_data`

---

## ✨ Avantages

### ✅ Persistance Automatique
Les données sont maintenant **sauvegardées par défaut** sans configuration supplémentaire.

### ✅ Configurable
Vous pouvez toujours passer en mode mémoire si nécessaire :
```bash
H2_DB_URL="jdbc:h2:mem:ticketcomparedb" docker-compose -f docker/docker-compose-h2.yml up -d
```

### ✅ Backward Compatible
Le fichier fonctionne exactement comme avant, mais avec persistance en plus.

### ✅ Production-Ready (pour H2)
- Volume nommé
- Variables d'environnement
- Configuration externalisée

---

## 🚀 Utilisation

### Mode Persistant (Défaut)

```bash
# Démarrage normal - données sauvegardées ✅
docker-compose -f docker/docker-compose-h2.yml up -d
```

### Mode Mémoire (Sur demande)

```bash
# Mode mémoire - données perdues au redémarrage
H2_DB_URL="jdbc:h2:mem:ticketcomparedb" docker-compose -f docker/docker-compose-h2.yml up -d
```

### Avec Fichier .env

```bash
# 1. Copier le template
cd docker
copy .env.h2 .env

# 2. Éditer .env si nécessaire

# 3. Démarrer
docker-compose -f docker/docker-compose-h2.yml --env-file docker/.env up -d
```

---

## 📊 Comparaison

| Aspect | Avant | Après |
|--------|-------|-------|
| **Persistance** | ❌ Non (mémoire) | ✅ Oui (fichier) |
| **Redémarrage** | 🔄 Données perdues | ✅ Données conservées |
| **Volume** | ❌ Pas de volume données | ✅ Volume h2_data |
| **Configuration** | ⚙️ Fixe | ✅ Variables d'environnement |
| **Backup** | ❌ Impossible | ✅ Possible |
| **Flexibilité** | ❌ Limité | ✅ 2 modes (file/mem) |

---

## 🔄 Migration

### Si vous utilisez déjà docker-compose-h2.yml

**Pas de migration nécessaire !**

1. **Arrêter** :
   ```bash
   docker-compose -f docker/docker-compose-h2.yml down
   ```

2. **Rebuild** :
   ```bash
   docker-compose -f docker/docker-compose-h2.yml up -d --build
   ```

3. **Vérifier** :
   ```bash
   docker volume ls | findstr ticketcompare
   # Vous devriez voir: ticketcompare_h2_data
   ```

### Conserver l'Ancien Comportement (Mémoire)

Si vous voulez garder le mode mémoire :
```bash
H2_DB_URL="jdbc:h2:mem:ticketcomparedb" JPA_DDL_AUTO="create" docker-compose -f docker/docker-compose-h2.yml up -d
```

---

## 📁 Fichiers Associés

- `docker/.env.h2` - Template de configuration
- `GUIDE_RAPIDE_H2_PERSISTANCE.md` - Guide d'utilisation
- `docs/H2_PERSISTANCE_DOCKER.md` - Documentation complète
- `scripts/manage-h2.ps1` - Script de gestion

---

## ✅ Résumé

### Ce qui change :
- ✅ Persistance activée par défaut
- ✅ Volume h2_data ajouté
- ✅ Variables d'environnement configurables
- ✅ Support de .env

### Ce qui ne change pas :
- ✅ Même commande de démarrage
- ✅ Même port (8080)
- ✅ Même H2 Console
- ✅ Compatible avec l'existant

### Bénéfices :
- 💾 Données sauvegardées automatiquement
- 🔄 Plus de perte de données au redémarrage
- 💾 Backup/restore possible
- ⚙️ Configuration flexible

---

**Date : 27 Décembre 2024**  
**Fichier : docker-compose-h2.yml**  
**Changement : Persistance H2 activée**  
**Impact : ✅ Positif - Amélioration sans breaking change**

