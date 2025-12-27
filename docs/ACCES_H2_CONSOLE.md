# 🗄️ Accès à la Console H2

## 🚀 Démarrage

```bash
# Dans WSL
cd /mnt/c/Users/pheni/IdeaProjects/TicketCompare/docker
docker compose -f docker-compose-h2.yml up
```

## 🌐 Accès à la Console H2

### URL
```
http://localhost:8080/h2-console
```

### Paramètres de Connexion

![H2 Console Login](https://docs.spring.io/spring-boot/images/h2-console.png)

| Champ | Valeur | Notes |
|-------|--------|-------|
| **Saved Settings** | Generic H2 (Embedded) | |
| **Setting Name** | Generic H2 (Embedded) | |
| **Driver Class** | `org.h2.Driver` | ✅ Automatique |
| **JDBC URL** | `jdbc:h2:mem:ticketcomparedb` | ⚠️ Important : en mémoire |
| **User Name** | `sa` | Par défaut |
| **Password** | *(vide)* | Pas de mot de passe |

### 📸 Capture d'écran de la Configuration

```
┌─────────────────────────────────────────┐
│  H2 Console                             │
├─────────────────────────────────────────┤
│                                         │
│  Saved Settings: Generic H2 (Embedded)  │
│                                         │
│  Driver Class: org.h2.Driver            │
│  JDBC URL: jdbc:h2:mem:ticketcomparedb  │
│  User Name: sa                          │
│  Password: [                          ] │
│                                         │
│           [ Connect ]                   │
└─────────────────────────────────────────┘
```

## 🔍 Vérification

### 1. Vérifier que l'application est démarrée

```bash
docker compose -f docker-compose-h2.yml ps
```

Vous devriez voir :
```
NAME                    STATUS
ticketcompare-app-h2    Up (healthy)
```

### 2. Tester l'accès web

```bash
# Vérifier que l'application répond
curl http://localhost:8080/actuator/health
```

### 3. Accéder à la console

Ouvrez votre navigateur : **http://localhost:8080/h2-console**

## 📊 Utilisation de la Console H2

### Voir les Tables

```sql
-- Lister toutes les tables
SHOW TABLES;

-- Voir la structure d'une table
SHOW COLUMNS FROM TICKETS;
```

### Requêtes Utiles

```sql
-- Voir tous les tickets
SELECT * FROM TICKETS;

-- Voir tous les produits
SELECT * FROM PRODUCTS;

-- Voir les produits d'un ticket
SELECT * FROM PRODUCTS WHERE TICKET_ID = 1;

-- Statistiques
SELECT COUNT(*) FROM TICKETS;
SELECT COUNT(*) FROM PRODUCTS;
```

### Insérer des Données de Test

Si vous voulez des données de démonstration, vous pouvez copier/coller le contenu du fichier `src/main/resources/test-data.sql` directement dans la console H2.

## ⚠️ Important

### Base de Données en Mémoire

La base H2 est **en mémoire** (`jdbc:h2:mem:ticketcomparedb`), ce qui signifie :

- ✅ **Avantage** : Très rapide
- ⚠️ **Inconvénient** : Les données sont **perdues au redémarrage**
- 🔄 **Mode** : `create-drop` - La base est recréée à chaque démarrage

### Pour Garder les Données

Si vous voulez que les données persistent entre les redémarrages, vous devez :

1. **Option 1** : Utiliser PostgreSQL
   ```bash
   docker compose -f docker-compose.yml up
   ```

2. **Option 2** : Modifier H2 en mode fichier (non recommandé pour Docker)

## 🐛 Troubleshooting

### Erreur : "404 Not Found" sur /h2-console

**Cause** : L'application n'est pas démarrée ou la console est désactivée

**Solution** :
```bash
# Vérifier les logs
docker compose -f docker-compose-h2.yml logs app | grep "h2"

# Vous devriez voir :
# H2 console available at '/h2-console'
```

### Erreur : "Cannot connect to jdbc:h2:mem:ticketcomparedb"

**Causes possibles** :
1. Mauvais JDBC URL
2. Application pas complètement démarrée

**Solutions** :
```bash
# Attendre 30 secondes après le démarrage
sleep 30

# Vérifier que l'app est "Up (healthy)"
docker compose ps

# JDBC URL correct :
jdbc:h2:mem:ticketcomparedb
# PAS : jdbc:h2:mem:testdb
```

### La Console H2 est Vide (Pas de Tables)

**Cause** : Normal si c'est le premier démarrage et qu'aucun ticket n'a été créé

**Solution** : Créez un ticket via l'interface web d'abord :
1. Allez sur http://localhost:8080
2. Menu "Nouveau Ticket"
3. Créez un ticket
4. Retournez sur /h2-console
5. Les tables seront maintenant visibles

## 📝 Commandes Rapides

### Démarrer et Accéder

```bash
# 1. Démarrer
cd /mnt/c/Users/pheni/IdeaProjects/TicketCompare/docker
docker compose -f docker-compose-h2.yml up -d

# 2. Attendre le démarrage
sleep 30

# 3. Ouvrir dans le navigateur
# Linux/WSL avec xdg-open :
xdg-open http://localhost:8080/h2-console

# Ou ouvrir manuellement :
# http://localhost:8080/h2-console
```

### Connexion Automatique

Vous pouvez aussi accéder directement avec l'URL complète :

```
http://localhost:8080/h2-console?url=jdbc:h2:mem:ticketcomparedb&user=sa&password=
```

Cela pré-remplira les champs de connexion.

## 🎯 Résumé Rapide

| Étape | Action |
|-------|--------|
| 1️⃣ | Démarrer : `docker compose -f docker-compose-h2.yml up` |
| 2️⃣ | Ouvrir : http://localhost:8080/h2-console |
| 3️⃣ | JDBC URL : `jdbc:h2:mem:ticketcomparedb` |
| 4️⃣ | User : `sa` / Password : *(vide)* |
| 5️⃣ | Cliquer : **Connect** |

## 📚 Liens Utiles

- **Application Web** : http://localhost:8080
- **Console H2** : http://localhost:8080/h2-console
- **Health Check** : http://localhost:8080/actuator/health
- **Metrics** : http://localhost:8080/actuator/metrics

---

**Bon développement ! 🚀**

