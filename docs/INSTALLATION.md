# 📦 Guide d'Installation - TicketCompare

Ce guide vous accompagne dans l'installation et la configuration de TicketCompare.

## 📋 Table des Matières

- [Prérequis](#prérequis)
- [Installation avec Docker](#installation-avec-docker)
- [Installation en Développement Local](#installation-en-développement-local)
- [Configuration](#configuration)
- [Vérification](#vérification)
- [Problèmes Courants](#problèmes-courants)

## 🔧 Prérequis

### Pour Docker (Recommandé)

- **Docker Desktop** : Version 20.10+
- **Docker Compose** : Version 2.0+
- **Espace disque** : 2 GB minimum

### Pour le Développement Local

- **Java JDK** : Version 21
- **Maven** : Version 3.9+
- **Tesseract OCR** : Version 5.0+ (optionnel pour l'OCR)
- **Git** : Pour cloner le repository

### Vérifier les versions

```bash
# Java
java -version

# Maven
mvn -version

# Docker
docker --version
docker-compose --version

# Tesseract (optionnel)
tesseract --version
```

## 🐳 Installation avec Docker

### Étape 1 : Cloner le Repository

```bash
git clone <votre-repository-url>
cd TicketCompare
```

### Étape 2 : Configurer les Variables d'Environnement

```bash
cd docker
copy .env.example .env
```

Éditez le fichier `.env` avec vos paramètres :

```env
# Base de données
POSTGRES_DB=ticketcompare
POSTGRES_USER=ticketuser
POSTGRES_PASSWORD=VotreMotDePasseSecurise2024!
POSTGRES_PORT=5432

# Application
APP_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# JVM
JAVA_OPTS=-Xmx512m -Xms256m
```

### Étape 3 : Lancer l'Application

**Option A : Utiliser le script Windows**
```bash
cd ..
.\scripts\start-docker.bat
```

**Option B : Commande Docker Compose**
```bash
cd docker
docker-compose up -d
```

### Étape 4 : Vérifier le Démarrage

```bash
# Voir les logs
docker-compose logs -f app

# Vérifier les conteneurs
docker-compose ps
```

Les services devraient être :
- ✅ `ticketcompare-db` (PostgreSQL) - Port 5432
- ✅ `ticketcompare-app` (Application) - Port 8080

### Étape 5 : Accéder à l'Application

Ouvrez votre navigateur :
- **Application** : http://localhost:8080
- **Health Check** : http://localhost:8080/actuator/health

## 💻 Installation en Développement Local

### Étape 1 : Cloner et Préparer

```bash
git clone <votre-repository-url>
cd TicketCompare
```

### Étape 2 : Installer Tesseract OCR (Optionnel)

**Windows :**
1. Télécharger depuis : https://github.com/UB-Mannheim/tesseract/wiki
2. Installer dans `C:\Program Files\Tesseract-OCR`
3. Ajouter au PATH système
4. Télécharger les données françaises : `fra.traineddata`

**Linux (Ubuntu/Debian) :**
```bash
sudo apt update
sudo apt install tesseract-ocr tesseract-ocr-fra
```

**macOS :**
```bash
brew install tesseract tesseract-lang
```

### Étape 3 : Lancer en Mode Développement

**Option A : Utiliser le script**
```bash
.\scripts\start-dev.bat
```

**Option B : Maven directement**
```bash
mvnw spring-boot:run
```

**Option C : Depuis l'IDE**
- Ouvrir le projet dans IntelliJ IDEA / Eclipse
- Exécuter `TicketCompareApplication.java`

### Étape 4 : Accéder à l'Application

- **Application** : http://localhost:8080
- **Console H2** : http://localhost:8080/h2-console
  - JDBC URL : `jdbc:h2:mem:ticketcomparedb`
  - Username : `sa`
  - Password : (vide)

## ⚙️ Configuration

### Configuration de la Base de Données

#### PostgreSQL (Production)

Éditez `src/main/resources/application-prod.yml` :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticketcompare
    username: ticketuser
    password: votre_mot_de_passe
```

#### H2 (Développement)

Configuration par défaut dans `application.yml`. Aucune modification nécessaire.

### Configuration Tesseract OCR

Éditez `application-prod.yml` :

```yaml
tesseract:
  ocr:
    language: fra+eng
    datapath: /usr/share/tessdata
```

**Windows** : Modifier pour pointer vers votre installation
```yaml
tesseract:
  ocr:
    datapath: C:/Program Files/Tesseract-OCR/tessdata
```

### Configuration des Uploads

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

## ✅ Vérification

### Tests de Santé

```bash
# Health check
curl http://localhost:8080/actuator/health

# Réponse attendue
{"status":"UP"}
```

### Tests Fonctionnels

1. Accéder à http://localhost:8080
2. Créer un ticket manuellement
3. Tester l'upload d'image (si OCR configuré)
4. Vérifier les statistiques

### Tests Unitaires

```bash
mvnw test
```

## 🐛 Problèmes Courants

### Erreur : Port 8080 déjà utilisé

**Solution :**
```bash
# Modifier le port dans .env
APP_PORT=8081
```

Ou arrêter l'application qui utilise le port :
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -i :8080
kill -9 <PID>
```

### Erreur : PostgreSQL ne démarre pas

**Vérifier les logs :**
```bash
docker-compose logs postgres
```

**Solutions :**
- Vérifier que le port 5432 n'est pas utilisé
- Vérifier les permissions du volume Docker
- Supprimer les volumes : `docker-compose down -v`

### Erreur : Tesseract non trouvé

**Windows :**
1. Vérifier l'installation : `tesseract --version`
2. Ajouter au PATH système
3. Redémarrer l'IDE/terminal

**Linux :**
```bash
sudo apt install tesseract-ocr tesseract-ocr-fra
```

### Erreur de Build Maven

```bash
# Nettoyer et rebuilder
mvnw clean install -DskipTests

# Vérifier la version Java
java -version  # Doit être 21+
```

### Erreur : Out of Memory

**Augmenter la mémoire JVM dans .env :**
```env
JAVA_OPTS=-Xmx1024m -Xms512m
```

## 🔄 Mise à Jour

### Mise à jour avec Docker

```bash
cd docker
docker-compose down
docker-compose pull
docker-compose up -d --build
```

### Mise à jour en Local

```bash
git pull
mvnw clean install
mvnw spring-boot:run
```

## 🗑️ Désinstallation

### Supprimer les conteneurs Docker

```bash
cd docker
docker-compose down -v
docker rmi ticketcompare-app
```

### Supprimer le projet

```bash
cd ..
rm -rf TicketCompare
```

## 📞 Support

Si vous rencontrez des problèmes :

1. Consultez la [documentation complète](GUIDE_COMPLET.md)
2. Vérifiez le [guide de résolution de problèmes](GUIDE_RESOLUTION_PROBLEMES.md)
3. Ouvrez une issue sur GitHub

## 📚 Prochaines Étapes

- [Guide Utilisateur](GUIDE_COMPLET.md)
- [Documentation API](API_OCR_DOCUMENTATION.md)
- [Configuration OCR](OCR_SETUP_GUIDE.md)

---

**Félicitations ! Votre application TicketCompare est maintenant installée et opérationnelle ! 🎉**

