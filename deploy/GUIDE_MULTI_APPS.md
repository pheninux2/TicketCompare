# ===================================
# Guide : Déploiement Multi-Applications sur VPS
# ===================================

## 🎯 Objectif

Héberger plusieurs applications Spring Boot sur un même VPS avec des chemins d'accès différents :
- `http://178.128.162.253/app1/` → Application 1 (ShopTracker)
- `http://178.128.162.253/app2/` → Application 2
- `http://178.128.162.253/app3/` → Application 3

Chaque application utilise une **image Docker pré-construite** par GitHub Actions, sans besoin de cloner tout le code source.

---

## 📁 Structure des applications sur le VPS

```
/opt/
└── shoptracker/
    ├── app1/
    │   └── environments/prod/
    │       ├── .env
    │       ├── docker-compose-pull.yml
    │       └── data/
    ├── app2/
    │   └── environments/prod/
    │       ├── .env
    │       ├── docker-compose-pull.yml
    │       └── data/
    └── app3/
        └── environments/prod/
            ├── .env
            ├── docker-compose-pull.yml
            └── data/
```

---

## 🚀 Étape 1 : Configuration GitHub Actions

### 1.1 Activer GitHub Container Registry (GHCR)

Le fichier `.github/workflows/docker-build.yml` a été créé. Ce workflow va :
- Builder votre application Maven
- Créer une image Docker
- La publier sur `ghcr.io/pheninux2/ticketcompare:latest`

### 1.2 Configurer les permissions du package

1. Allez sur GitHub : `https://github.com/pheninux2/TicketCompare`
2. Cliquez sur **Settings** → **Actions** → **General**
3. Sous **Workflow permissions**, sélectionnez **Read and write permissions**
4. Sauvegardez

### 1.3 Rendre l'image publique (optionnel)

Pour éviter l'authentification sur le VPS :
1. Allez sur `https://github.com/pheninux2?tab=packages`
2. Trouvez le package `ticketcompare`
3. Cliquez dessus → **Package settings**
4. Descendez jusqu'à **Change visibility**
5. Changez en **Public**

---

## 🔧 Étape 2 : Configuration du VPS

### 2.1 Se connecter au VPS

```bash
ssh root@178.128.162.253
```

### 2.2 Installer les prérequis (si pas déjà fait)

```bash
# Mettre à jour le système
apt update && apt upgrade -y

# Installer Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Installer Nginx
apt install nginx -y

# Démarrer Nginx
systemctl start nginx
systemctl enable nginx
```

---

## 📦 Étape 3 : Déployer l'Application 1

### 3.1 Créer la structure

```bash
mkdir -p /opt/shoptracker/app1/environments/prod
cd /opt/shoptracker/app1
```

### 3.2 Télécharger le script de déploiement

```bash
# Option 1 : Depuis votre PC, copier le fichier
# Sur votre PC Windows (PowerShell) :
scp C:\Users\pheni\IdeaProjects\TicketCompare\deploy\deploy-image.sh root@178.128.162.253:/opt/shoptracker/app1/

# Option 2 : Créer le fichier directement sur le VPS
curl -o deploy-image.sh https://raw.githubusercontent.com/pheninux2/TicketCompare/main/deploy/deploy-image.sh
```

### 3.3 Rendre le script exécutable

```bash
chmod +x deploy-image.sh
```

### 3.4 Exécuter le déploiement

```bash
# Pour une image publique
./deploy-image.sh

# Pour une image privée
export GITHUB_TOKEN=ghp_votre_token_github
./deploy-image.sh
```

Le script va :
✅ Télécharger l'image Docker depuis GHCR
✅ Créer la configuration `.env`
✅ Créer le `docker-compose-pull.yml`
✅ Configurer Nginx pour le multi-apps
✅ Démarrer l'application sur le port 8080

---

## 🔄 Étape 4 : Déployer l'Application 2 (optionnel)

### 4.1 Créer la structure

```bash
mkdir -p /opt/shoptracker/app2/environments/prod
cd /opt/shoptracker/app2
```

### 4.2 Copier et adapter le script

```bash
cp /opt/shoptracker/app1/deploy-image.sh .
```

Modifier les variables dans le script :
```bash
nano deploy-image.sh
```

Changez :
```bash
APP_DIR="/opt/shoptracker/app2"         # Chemin app2
IMAGE_NAME="ghcr.io/user/app2:latest"   # Image app2
```

Modifiez aussi le port dans docker-compose-pull.yml :
```bash
nano environments/prod/docker-compose-pull.yml
```

Changez :
```yaml
ports:
  - "8081:8080"  # Port 8081 pour app2
```

### 4.3 Mettre à jour Nginx

Le fichier Nginx créé par le script supporte déjà `/app2/` (port 8081)

### 4.4 Démarrer l'app2

```bash
./deploy-image.sh
```

---

## 🌐 Étape 5 : Configuration Nginx Multi-Apps

La configuration Nginx est automatiquement créée par le script.

Pour vérifier ou modifier manuellement :

```bash
nano /etc/nginx/sites-available/multi-app
```

Testez la configuration :
```bash
nginx -t
systemctl reload nginx
```

---

## 🔐 Étape 6 : Configuration Spring Boot (Important!)

Pour que Spring Boot fonctionne derrière un reverse proxy avec un chemin, ajoutez dans votre `application.yml` ou `application.properties` :

### application.yml
```yaml
server:
  port: 8080
  servlet:
    context-path: /
  forward-headers-strategy: framework
  
spring:
  mvc:
    servlet:
      path: /
```

### OU application.properties
```properties
server.port=8080
server.servlet.context-path=/
server.forward-headers-strategy=framework
spring.mvc.servlet.path=/
```

⚠️ **Important** : Le `context-path` doit être `/` car Nginx utilise `rewrite` pour retirer le préfixe `/app1/`

---

## 🧪 Étape 7 : Tester les applications

```bash
# Test de la page d'accueil
curl http://178.128.162.253/

# Test de l'app1
curl http://178.128.162.253/app1/actuator/health

# Test de l'app2
curl http://178.128.162.253/app2/actuator/health
```

Depuis votre navigateur :
- `http://178.128.162.253/` → Liste des applications
- `http://178.128.162.253/app1/` → Application 1
- `http://178.128.162.253/app2/` → Application 2

---

## 🔄 Mise à jour d'une application

Lorsque GitHub Actions construit une nouvelle image :

```bash
# Se connecter au VPS
ssh root@178.128.162.253

# Aller dans le dossier de l'app
cd /opt/shoptracker/app1/environments/prod

# Télécharger la nouvelle image
docker pull ghcr.io/pheninux2/ticketcompare:latest

# Redémarrer l'application
docker compose -f docker-compose-pull.yml up -d

# Voir les logs
docker compose -f docker-compose-pull.yml logs -f app
```

---

## 📊 Commandes utiles

### Gérer les conteneurs

```bash
# Voir tous les conteneurs
docker ps -a

# Logs d'une application
docker logs -f shoptracker-app1

# Redémarrer une app
cd /opt/shoptracker/app1/environments/prod
docker compose -f docker-compose-pull.yml restart app

# Arrêter une app
docker compose -f docker-compose-pull.yml down

# Voir l'utilisation des ressources
docker stats
```

### Gérer Nginx

```bash
# Tester la configuration
nginx -t

# Recharger Nginx
systemctl reload nginx

# Voir les logs Nginx
tail -f /var/log/nginx/multi_app_access.log
tail -f /var/log/nginx/multi_app_error.log
```

### Nettoyer Docker

```bash
# Supprimer les images inutilisées
docker image prune -a

# Supprimer les conteneurs arrêtés
docker container prune

# Nettoyer complètement
docker system prune -a --volumes
```

---

## 🔒 Sécurité & Production

### Ajouter HTTPS avec Let's Encrypt

```bash
# Installer Certbot
apt install certbot python3-certbot-nginx -y

# Obtenir un certificat SSL (nécessite un nom de domaine)
certbot --nginx -d votredomaine.com
```

### Firewall

```bash
# Autoriser uniquement les ports nécessaires
ufw allow 22/tcp    # SSH
ufw allow 80/tcp    # HTTP
ufw allow 443/tcp   # HTTPS
ufw enable
```

### Sauvegarde automatique

Créez un cron job pour sauvegarder les bases de données :

```bash
crontab -e
```

Ajoutez :
```cron
# Backup quotidien à 2h du matin
0 2 * * * docker exec shoptracker-app1-db pg_dump -U shoptracker_admin shoptracker > /opt/shoptracker/app1/environments/prod/backups/backup_$(date +\%Y\%m\%d).sql
```

---

## ❓ Dépannage

### L'application ne répond pas

```bash
# Vérifier que le conteneur tourne
docker ps | grep shoptracker-app1

# Voir les logs
docker logs shoptracker-app1

# Vérifier que le port est ouvert
netstat -tulpn | grep 8080
```

### Nginx retourne 502 Bad Gateway

```bash
# Vérifier que l'application répond localement
curl http://localhost:8080/actuator/health

# Vérifier les logs Nginx
tail -f /var/log/nginx/multi_app_error.log
```

### L'image ne se télécharge pas

```bash
# Vérifier que l'image existe
docker pull ghcr.io/pheninux2/ticketcompare:latest

# Si image privée, s'authentifier
echo $GITHUB_TOKEN | docker login ghcr.io -u pheninux2 --password-stdin
```

---

## 📝 Checklist de déploiement

- [ ] GitHub Actions configuré et image publiée
- [ ] VPS accessible en SSH
- [ ] Docker et Nginx installés
- [ ] Structure de dossiers créée
- [ ] Script `deploy-image.sh` copié
- [ ] Script exécuté avec succès
- [ ] Nginx configuré et rechargé
- [ ] Application accessible via `http://IP/app1/`
- [ ] Health check OK
- [ ] Logs sans erreurs

---

## 🎉 Félicitations !

Vous avez maintenant un VPS qui peut héberger plusieurs applications Spring Boot avec des chemins d'accès différents, le tout en utilisant des images Docker pré-construites !

