# 🧠 ReceiptIQ

**Smart Receipt Intelligence** - Application web intelligente de gestion et comparaison de tickets de caisse avec OCR (Reconnaissance Optique de Caractères).

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)

## 🎯 Fonctionnalités

- ✅ **Gestion de tickets** : Créer, éditer, visualiser et supprimer des tickets de caisse
- 🔍 **OCR Intelligent** : Scanner des tickets de caisse avec Tesseract OCR
- 📊 **Statistiques** : Visualiser vos dépenses par magasin et catégorie
- 🧠 **Intelligence Artificielle** : Analyse intelligente de vos habitudes d'achat
- 🏷️ **Catégorisation** : Organiser vos produits par catégories
- 💾 **Export CSV** : Exporter vos données pour analyse
- 🐳 **Docker Ready** : Déploiement simplifié avec Docker Compose

## 🚀 Démarrage Rapide

### Prérequis

- Java 21+
- Maven 3.9+
- Docker & Docker Compose (pour le déploiement)

### Option 1 : Avec Docker (Recommandé)

**Windows :**
```powershell
# 1. Cloner le repository
git clone <votre-repo>
cd TicketCompare

# 2. Copier et configurer les variables d'environnement
cd docker
copy .env.example .env
# Modifier le fichier .env selon vos besoins

# 3. Lancer l'application avec le script
cd ..
.\scripts\start-docker.bat

# 4. Accéder à l'application
# Application : http://localhost:8080
```

**Linux/macOS :**
```bash
# 1. Cloner le repository
git clone <votre-repo>
cd TicketCompare

# 2. Rendre les scripts exécutables
chmod +x scripts/*.sh

# 3. Copier et configurer les variables d'environnement
cd docker
cp .env.example .env
nano .env  # Modifier selon vos besoins

# 4. Lancer l'application
cd ..
./scripts/start-docker.sh

# 5. Accéder à l'application
# Application : http://localhost:8080
```

### Option 2 : En Développement Local (H2)

**Windows :**
```powershell
# Utiliser le script de démarrage
.\scripts\start-dev.bat

# Ou directement avec Maven
mvnw spring-boot:run
```

**Linux/macOS :**
```bash
# Utiliser le script de démarrage
./scripts/start-dev.sh

# Ou directement avec Maven
./mvnw spring-boot:run
```

**Accéder à l'application :**
- Application : http://localhost:8080
- Console H2 : http://localhost:8080/h2-console

## 📁 Structure du Projet

```
TicketCompare/
├── docker/                     # Configuration Docker
│   ├── docker-compose.yml     # Orchestration des services
│   ├── Dockerfile             # Image de l'application
│   ├── .env.example           # Variables d'environnement
│   └── init-db/               # Scripts d'initialisation DB
│
├── docs/                       # Documentation complète
│   ├── README.md              # Documentation principale
│   ├── GUIDE_COMPLET.md       # Guide utilisateur
│   ├── API_OCR_DOCUMENTATION.md
│   └── ...                    # Autres guides
│
├── scripts/                    # Scripts utilitaires
│   ├── start-docker.bat       # Démarrer avec Docker
│   ├── stop-docker.bat        # Arrêter Docker
│   ├── rebuild-docker.bat     # Reconstruire l'image
│   ├── start-dev.bat          # Mode développement
│   └── logs.bat               # Voir les logs
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── pheninux/xdev/ticketcompare/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── TicketCompareApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       ├── application.yml
│   │       └── application-prod.yml
│   └── test/
│
├── pom.xml                     # Configuration Maven
└── README.md                   # Ce fichier
```

## 🛠️ Technologies Utilisées

### Backend
- **Java 21** - Langage de programmation
- **Spring Boot 4.0** - Framework web
- **Spring Data JPA** - ORM et gestion des données
- **Hibernate** - Implémentation JPA
- **Maven** - Gestion des dépendances

### Frontend
- **Thymeleaf** - Moteur de templates
- **Bootstrap 5** - Framework CSS
- **HTMX** - Interactivité moderne
- **Font Awesome** - Icônes

### Base de Données
- **PostgreSQL 16** - Base de données production
- **H2** - Base de données développement

### OCR & Traitement
- **Tesseract OCR** - Reconnaissance de caractères
- **Tess4J** - Binding Java pour Tesseract

### Infrastructure
- **Docker** - Conteneurisation
- **Docker Compose** - Orchestration

## 📖 Documentation

Consultez le dossier `docs/` pour la documentation complète :

- **[Guide de Démarrage](docs/GUIDE_DEMARRAGE_FINAL.md)** - Installation et configuration
- **[Guide Linux/macOS](docs/GUIDE_LINUX_MACOS.md)** - Guide spécifique Unix 🐧
- **[Configuration WSL2](docs/CONFIGURATION_WSL2_DOCKER.md)** - Docker avec WSL 2 🪟🐧
- **[Guide Complet](docs/GUIDE_COMPLET.md)** - Utilisation détaillée
- **[Documentation OCR](docs/API_OCR_DOCUMENTATION.md)** - Configuration Tesseract
- **[Résolution de Problèmes](docs/GUIDE_RESOLUTION_PROBLEMES.md)** - Troubleshooting
- **[Troubleshooting Docker](docs/TROUBLESHOOTING_DOCKER.md)** - Problèmes Docker 🐳

## 🐳 Commandes Docker Utiles

### Windows (PowerShell)

```powershell
# Démarrer l'application
.\scripts\start-docker.bat

# Démarrer avec H2
.\scripts\start-docker-h2.bat

# Voir les logs
.\scripts\logs.bat

# Arrêter l'application
.\scripts\stop-docker.bat

# Reconstruire l'image
.\scripts\rebuild-docker.bat
```

### Linux/macOS (Bash)

```bash
# Démarrer l'application
./scripts/start-docker.sh

# Démarrer avec H2
./scripts/start-docker-h2.sh

# Voir les logs
./scripts/logs.sh

# Arrêter l'application
./scripts/stop-docker.sh

# Reconstruire l'image
./scripts/rebuild-docker.sh
```

### Docker Compose Direct

```bash
# Démarrer l'application
cd docker && docker-compose up -d

# Démarrer avec pgAdmin
docker-compose --profile admin up -d

# Voir les logs
docker-compose logs -f app

# Arrêter l'application
docker-compose down

# Tout supprimer (y compris les données)
docker-compose down -v

# Reconstruire l'image
docker-compose build --no-cache app

# Redémarrer uniquement l'app
docker-compose restart app
```

## ⚙️ Configuration

### Variables d'Environnement

Copiez `docker/.env.example` vers `docker/.env` et modifiez :

```env
# Base de données
POSTGRES_DB=ticketcompare
POSTGRES_USER=ticketuser
POSTGRES_PASSWORD=VotreMotDePasse2024!

# Application
APP_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# JVM
JAVA_OPTS=-Xmx512m -Xms256m
```

### Profils Spring

- **default** : Développement avec H2
- **prod** : Production avec PostgreSQL

## 🔐 Sécurité

⚠️ **Important en production :**

1. Changez TOUS les mots de passe dans `.env`
2. Utilisez HTTPS en production
3. Configurez un firewall
4. Sauvegardez régulièrement votre base de données
5. Ne commitez JAMAIS le fichier `.env`

## 📊 Base de Données

### PostgreSQL (Production)

- **Host** : localhost (ou postgres dans Docker)
- **Port** : 5432
- **Database** : ticketcompare
- **User** : ticketuser

### H2 (Développement)

- **Console** : http://localhost:8080/h2-console
- **JDBC URL** : jdbc:h2:mem:ticketcomparedb
- **Username** : sa
- **Password** : (vide)

## 🧪 Tests

```bash
# Exécuter les tests
mvnw test

# Exécuter avec coverage
mvnw test jacoco:report
```

## 📦 Build

```bash
# Build Maven
mvnw clean package

# Build Docker
cd docker && docker-compose build
```

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📝 License

Ce projet est sous licence MIT - voir le fichier LICENSE pour plus de détails.

## 👨‍💻 Auteur

**Pheninux XDev**

## 🆘 Support

En cas de problème :

1. Consultez la [documentation](docs/)
2. Vérifiez les [problèmes connus](docs/GUIDE_RESOLUTION_PROBLEMES.md)
3. Ouvrez une issue sur GitHub

## 📈 Roadmap

- [ ] Authentification utilisateur
- [ ] API REST complète
- [ ] Application mobile
- [ ] Comparaison automatique de prix
- [ ] Intelligence artificielle pour la catégorisation
- [ ] Export PDF des rapports

---

⭐ **N'oubliez pas de star le projet si vous le trouvez utile !** ⭐

