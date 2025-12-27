# 🐧 Guide Linux/macOS - TicketCompare

Guide d'utilisation pour Linux et macOS avec les scripts shell.

## 📋 Table des Matières

- [Prérequis](#prérequis)
- [Installation](#installation)
- [Scripts Disponibles](#scripts-disponibles)
- [Démarrage Rapide](#démarrage-rapide)
- [Commandes Courantes](#commandes-courantes)
- [Troubleshooting](#troubleshooting)

## 🔧 Prérequis

### Linux (Ubuntu/Debian)

```bash
# Java 21
sudo apt update
sudo apt install openjdk-21-jdk

# Docker & Docker Compose
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker

# Ajouter l'utilisateur au groupe docker
sudo usermod -aG docker $USER
newgrp docker

# Tesseract OCR (optionnel)
sudo apt install tesseract-ocr tesseract-ocr-fra
```

### macOS

```bash
# Homebrew (si pas installé)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Java 21
brew install openjdk@21

# Docker Desktop
brew install --cask docker

# Tesseract OCR (optionnel)
brew install tesseract tesseract-lang
```

## 📦 Installation

### 1. Cloner le Repository

```bash
git clone <votre-repository-url>
cd TicketCompare
```

### 2. Rendre les Scripts Exécutables

```bash
chmod +x scripts/*.sh
```

Ou utilisez le script automatique :

```bash
bash scripts/make-executable.sh
```

### 3. Vérifier l'Installation

```bash
./scripts/verify.sh
```

## 🚀 Scripts Disponibles

| Script | Description | Usage |
|--------|-------------|-------|
| **start-docker.sh** | Démarrer avec PostgreSQL | `./scripts/start-docker.sh` |
| **start-docker-h2.sh** | Démarrer avec H2 | `./scripts/start-docker-h2.sh` |
| **stop-docker.sh** | Arrêter Docker | `./scripts/stop-docker.sh` |
| **rebuild-docker.sh** | Reconstruire l'image | `./scripts/rebuild-docker.sh` |
| **start-dev.sh** | Mode développement local | `./scripts/start-dev.sh` |
| **logs.sh** | Voir les logs | `./scripts/logs.sh` |
| **verify.sh** | Vérifier l'installation | `./scripts/verify.sh` |
| **make-executable.sh** | Rendre scripts exécutables | `bash scripts/make-executable.sh` |

## ⚡ Démarrage Rapide

### Option 1 : Docker + PostgreSQL (Production)

```bash
# 1. Configurer (première fois uniquement)
cd docker
cp .env.example .env
nano .env  # ou vim, ou votre éditeur préféré

# 2. Lancer
cd ..
./scripts/start-docker.sh

# 3. Accéder
# http://localhost:8080
```

### Option 2 : Docker + H2 (Test)

```bash
./scripts/start-docker-h2.sh
```

### Option 3 : Développement Local (Sans Docker)

```bash
./scripts/start-dev.sh
```

## 🛠️ Commandes Courantes

### Gestion de l'Application

```bash
# Démarrer
./scripts/start-docker.sh

# Arrêter
./scripts/stop-docker.sh

# Redémarrer
./scripts/stop-docker.sh && ./scripts/start-docker.sh

# Reconstruire
./scripts/rebuild-docker.sh

# Voir les logs
./scripts/logs.sh
```

### Docker Compose Direct

```bash
# Dans le dossier docker/
cd docker

# Démarrer
docker-compose up -d

# Démarrer avec pgAdmin
docker-compose --profile admin up -d

# Arrêter
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v

# Voir les logs
docker-compose logs -f

# Voir les conteneurs
docker-compose ps

# Redémarrer un service
docker-compose restart app
```

### Maven

```bash
# Build
./mvnw clean package

# Tests
./mvnw test

# Lancer en dev
./mvnw spring-boot:run

# Nettoyer
./mvnw clean
```

### Docker

```bash
# Lister les conteneurs
docker ps -a

# Voir les logs d'un conteneur
docker logs ticketcompare-app -f

# Se connecter à un conteneur
docker exec -it ticketcompare-app sh

# Voir l'utilisation des ressources
docker stats

# Nettoyer Docker
docker system prune -a
```

## 🔐 Permissions

### Erreur de Permission Docker

Si vous obtenez des erreurs de permission avec Docker :

```bash
# Ajouter votre utilisateur au groupe docker
sudo usermod -aG docker $USER

# Se reconnecter au groupe
newgrp docker

# Ou redémarrer votre session
```

### Scripts Non Exécutables

Si vous obtenez "Permission denied" :

```bash
# Rendre un script exécutable
chmod +x scripts/nom-du-script.sh

# Ou tous les scripts
chmod +x scripts/*.sh
```

## 🐛 Troubleshooting

### Docker n'est pas démarré

**Linux :**
```bash
sudo systemctl start docker
sudo systemctl enable docker
```

**macOS :**
Lancez Docker Desktop depuis Applications.

### Port 8080 occupé

```bash
# Trouver le processus
lsof -i :8080

# Tuer le processus
kill -9 <PID>

# Ou changer le port dans docker/.env
echo "APP_PORT=8081" >> docker/.env
```

### Tesseract non trouvé

**Ubuntu/Debian :**
```bash
sudo apt install tesseract-ocr tesseract-ocr-fra
```

**macOS :**
```bash
brew install tesseract tesseract-lang
```

### Problèmes de Build Maven

```bash
# Nettoyer et rebuilder
./mvnw clean install -DskipTests

# Vérifier Java
java -version  # Doit être 21+
```

### Erreur "mvnw: command not found"

```bash
# Rendre Maven Wrapper exécutable
chmod +x mvnw

# Ou utiliser Maven global
mvn spring-boot:run
```

## 📊 Monitoring

### Logs en Temps Réel

```bash
# Tous les services
./scripts/logs.sh

# Service spécifique
docker-compose -f docker/docker-compose.yml logs -f app

# PostgreSQL
docker-compose -f docker/docker-compose.yml logs -f postgres
```

### Health Checks

```bash
# Health de l'application
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Info
curl http://localhost:8080/actuator/info
```

### Base de Données

**PostgreSQL :**
```bash
# Se connecter avec psql
docker exec -it ticketcompare-db psql -U ticketuser -d ticketcompare

# Backup
docker exec ticketcompare-db pg_dump -U ticketuser ticketcompare > backup.sql

# Restore
docker exec -i ticketcompare-db psql -U ticketuser ticketcompare < backup.sql
```

**Console H2 (mode dev) :**
- URL : http://localhost:8080/h2-console
- JDBC URL : `jdbc:h2:mem:ticketcomparedb`
- Username : `sa`
- Password : (vide)

## 🔄 Mise à Jour

```bash
# Récupérer les dernières modifications
git pull

# Reconstruire l'application
./scripts/rebuild-docker.sh

# Ou en mode dev
./mvnw clean install
./scripts/start-dev.sh
```

## 🗑️ Désinstallation

```bash
# Arrêter et supprimer les conteneurs
cd docker
docker-compose down -v

# Supprimer les images
docker rmi ticketcompare-app postgres:16-alpine

# Nettoyer complètement Docker
docker system prune -a --volumes

# Supprimer le projet
cd ../..
rm -rf TicketCompare
```

## 📁 Fichiers de Configuration

### .env (docker/.env)

```bash
# Éditer avec nano
nano docker/.env

# Éditer avec vim
vim docker/.env

# Éditer avec votre éditeur préféré
code docker/.env  # VS Code
gedit docker/.env  # Gedit
```

### application.yml

```bash
# Fichier de configuration principal
nano src/main/resources/application.yml

# Profil production
nano src/main/resources/application-prod.yml
```

## 🎯 Raccourcis Utiles

### Alias Bash

Ajoutez dans `~/.bashrc` ou `~/.zshrc` :

```bash
# Alias TicketCompare
alias tc-start='cd ~/TicketCompare && ./scripts/start-docker.sh'
alias tc-stop='cd ~/TicketCompare && ./scripts/stop-docker.sh'
alias tc-logs='cd ~/TicketCompare && ./scripts/logs.sh'
alias tc-dev='cd ~/TicketCompare && ./scripts/start-dev.sh'
alias tc-rebuild='cd ~/TicketCompare && ./scripts/rebuild-docker.sh'
```

Puis rechargez :
```bash
source ~/.bashrc  # ou source ~/.zshrc
```

Utilisez ensuite :
```bash
tc-start  # Démarrer l'application
tc-logs   # Voir les logs
tc-stop   # Arrêter
```

## 📚 Documentation Supplémentaire

- **[Installation Complète](INSTALLATION.md)**
- **[Architecture](ARCHITECTURE.md)**
- **[Troubleshooting Docker](TROUBLESHOOTING_DOCKER.md)**
- **[Guide Complet](GUIDE_COMPLET.md)**

## 🆘 Support

Pour obtenir de l'aide :

1. Vérifier l'installation : `./scripts/verify.sh`
2. Consulter les logs : `./scripts/logs.sh`
3. Lire la documentation dans `docs/`
4. Ouvrir une issue sur GitHub

---

**Guide maintenu pour : Linux (Ubuntu/Debian/CentOS) et macOS**  
**Dernière mise à jour : Décembre 2024**

