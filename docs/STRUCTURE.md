# 📁 Structure du Projet TicketCompare

```
TicketCompare/
│
├── 📄 pom.xml                          # Configuration Maven (dépendances, plugins)
├── 📄 README.md                        # Documentation complète
├── 📄 QUICKSTART.md                    # Guide de démarrage rapide
├── 📄 Dockerfile                       # Image Docker
├── 📄 docker-compose.yml               # Orchestration Docker
├── 📄 .gitignore                       # Fichiers à ignorer Git
│
├── 🔧 run-dev.bat                      # Script démarrage développement (Windows)
├── 🔧 run-prod.bat                     # Script démarrage production (Windows)
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/pheninux/xdev/ticketcompare/
│   │   │   ├── 📄 TicketCompareApplication.java      # Point d'entrée Spring Boot
│   │   │   │
│   │   │   ├── 📁 controller/                        # Contrôleurs MVC
│   │   │   │   ├── HomeController.java               # Page d'accueil
│   │   │   │   ├── TicketController.java             # CRUD tickets
│   │   │   │   ├── StatisticController.java          # Statistiques
│   │   │   │   ├── ConsumptionController.java        # Consommation
│   │   │   │   └── AnalysisController.java           # Prédictions
│   │   │   │
│   │   │   ├── 📁 entity/                            # Entités JPA
│   │   │   │   ├── Ticket.java                       # Ticket de caisse
│   │   │   │   ├── Product.java                      # Produit
│   │   │   │   ├── PriceHistory.java                 # Historique des prix
│   │   │   │   ├── ConsumptionStatistic.java         # Stats consommation
│   │   │   │   └── StatisticSnapshot.java            # Cache statistiques
│   │   │   │
│   │   │   ├── 📁 repository/                        # Repositories JPA
│   │   │   │   ├── TicketRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── PriceHistoryRepository.java
│   │   │   │   ├── ConsumptionStatisticRepository.java
│   │   │   │   └── StatisticSnapshotRepository.java
│   │   │   │
│   │   │   ├── 📁 service/                           # Logique métier
│   │   │   │   ├── TicketService.java                # Gestion tickets
│   │   │   │   ├── StatisticService.java             # Calculs statistiques
│   │   │   │   ├── ConsumptionService.java           # Analyse consommation
│   │   │   │   └── PriceAnalysisService.java         # Prédictions (régression)
│   │   │   │
│   │   │   └── 📁 dto/                               # Data Transfer Objects
│   │   │       ├── TicketDTO.java
│   │   │       ├── ProductDTO.java
│   │   │       ├── PriceStatisticDTO.java
│   │   │       ├── PriceForecastDTO.java
│   │   │       └── ConsumptionDTO.java
│   │   │
│   │   └── 📁 resources/
│   │       ├── 📄 application.properties             # Config par défaut
│   │       ├── 📄 application-dev.properties         # Config développement (H2)
│   │       ├── 📄 application-prod.properties        # Config production (MySQL)
│   │       ├── 📄 data.sql                           # Données de test
│   │       ├── 📄 schema-mysql.sql                   # Schéma MySQL
│   │       │
│   │       └── 📁 templates/
│   │           ├── 📄 index.html                     # Accueil
│   │           │
│   │           ├── 📁 tickets/
│   │           │   ├── list.html                     # Liste des tickets
│   │           │   ├── create.html                   # Créer un ticket
│   │           │   ├── detail.html                   # Détail d'un ticket
│   │           │   └── edit.html                     # Éditer un ticket
│   │           │
│   │           ├── 📁 statistics/
│   │           │   ├── dashboard.html                # Tableau de bord stats
│   │           │   ├── category.html                 # Stats par catégorie
│   │           │   ├── expensive.html                # Produits chers
│   │           │   ├── cheap.html                    # Produits pas chers
│   │           │   └── price-comparison.html         # Comparaison de prix
│   │           │
│   │           ├── 📁 consumption/
│   │           │   ├── weekly.html                   # Consommation hebdo
│   │           │   ├── top-products.html             # Top produits
│   │           │   └── trend.html                    # Tendance consommation
│   │           │
│   │           ├── 📁 analysis/
│   │           │   ├── forecast.html                 # Prédictions de prix
│   │           │   └── trend.html                    # Tendance prix
│   │           │
│   │           └── 📁 fragments/
│   │               ├── navbar.html                   # Barre de navigation
│   │               ├── footer.html                   # Pied de page
│   │               └── modal.html                    # Modales
│   │
│   └── 📁 test/
│       └── 📁 java/pheninux/xdev/ticketcompare/
│           └── 📄 TicketCompareApplicationTests.java # Tests Spring
│
├── 📁 build/                           # Dossier de compilation (généré)
│   ├── 📁 classes/
│   ├── 📁 reports/
│   └── ...
│
└── 📁 target/                          # Dossier Maven de build (généré)
    ├── 📄 ticketcompare-0.0.1-SNAPSHOT.jar
    ├── 📁 classes/
    ├── 📁 maven-archiver/
    └── ...
```

## 📊 Dépendances Principales (Maven)

```xml
Spring Boot 4.0
├── spring-boot-starter-web
├── spring-boot-starter-data-jpa
├── spring-boot-starter-thymeleaf
├── spring-boot-starter-validation
├── spring-boot-starter-h2 (dev)
└── mysql-connector-j (prod)

Tools
├── htmx-spring-boot-thymeleaf 5.0.0
├── lombok
├── commons-csv
└── spring-boot-devtools
```

## 🔄 Architecture en Couches

```
┌─────────────────────────────────────┐
│     HTML + Bootstrap + HTMX         │ ← Templates Thymeleaf
├─────────────────────────────────────┤
│         Controllers (MVC)           │ ← Routes web + rendu pages
├─────────────────────────────────────┤
│       Services (Logique métier)     │ ← Calculs, statistiques, prédictions
├─────────────────────────────────────┤
│    Repositories (Accès données)     │ ← JPA, requêtes SQL
├─────────────────────────────────────┤
│        Entities (Modèle ORM)        │ ← Tables mappées
├─────────────────────────────────────┤
│      Database (H2 ou MySQL)         │ ← Stockage persistant
└─────────────────────────────────────┘
```

## 📋 Relations entre Entités

```
Ticket (1) ────→ (N) Product
   │                  ├─ name
   ├─ date           ├─ category
   ├─ store          ├─ price
   ├─ totalAmount    ├─ quantity
   └─ products[]     └─ unit

PriceHistory (historique des prix)
   ├─ productName
   ├─ price
   ├─ priceDate
   └─ store

ConsumptionStatistic (aggregation hebdomadaire)
   ├─ productName
   ├─ weekStart
   ├─ totalQuantity
   ├─ totalCost
   └─ purchaseCount

StatisticSnapshot (cache statistiques)
   ├─ category
   ├─ minPrice, maxPrice, averagePrice
   └─ snapshotDate
```

## 🔌 Endpoints Principaux

### Frontend (Pages HTML)
```
GET  /                           → Accueil
GET  /tickets                    → Liste tickets
GET  /tickets/create             → Créer ticket
POST /tickets                    → Enregistrer ticket
GET  /tickets/{id}               → Détail ticket
GET  /statistics/dashboard       → Stats
GET  /consumption/weekly         → Consommation
GET  /analysis/forecast          → Prédictions
```

### API REST (JSON via HTMX)
```
GET  /statistics/api/price-comparison
GET  /analysis/api/forecast
GET  /analysis/api/trend
```

## 🛠️ Profils Spring

### Développement (dev)
- Base H2 en mémoire
- Auto-création des tables (create-drop)
- Thymeleaf cache désactivé
- Logs DEBUG activés
- Console H2 disponible

### Production (prod)
- Base MySQL (externe)
- Auto-création en update seulement
- Thymeleaf cache activé
- Logs WARN
- Pas de console H2

## 📦 Fichiers de Configuration Clés

- `pom.xml` - Dépendances et plugins Maven
- `application.properties` - Config par défaut
- `application-dev.properties` - Config dev
- `application-prod.properties` - Config prod
- `data.sql` - Données de test (dev)
- `schema-mysql.sql` - Schéma production

## 🚀 Commandes Maven Utiles

```bash
# Compiler sans tests
mvn clean compile

# Build complète
mvn clean install -DskipTests

# Démarrer en développement
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Démarrer en production
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Créer un JAR exécutable
mvn clean package -DskipTests

# Exécuter les tests
mvn test

# Voir les dépendances
mvn dependency:tree
```

## 💾 Structure de la Base de Données

```
tickets
├── id (PRIMARY KEY)
├── date
├── store
├── total_amount
├── notes
└── created_at

products
├── id (PRIMARY KEY)
├── ticket_id (FOREIGN KEY)
├── name
├── category
├── price
├── quantity
├── unit
└── total_price

price_history
├── id (PRIMARY KEY)
├── product_name
├── price
├── price_date
├── store
└── unit

consumption_statistics
├── id (PRIMARY KEY)
├── product_name
├── week_start
├── total_quantity
├── total_cost
├── purchase_count
├── category
└── unit

statistic_snapshots
├── id (PRIMARY KEY)
├── category
├── min_price
├── max_price
├── average_price
├── product_count
└── snapshot_date
```

## 🎨 Front-end Technologies

- **HTML5** - Markup
- **Bootstrap 5.3** - Framework CSS
- **HTMX** - Requêtes AJAX sans JavaScript
- **JavaScript vanilla** - Interactions
- **Chart.js** - Graphiques (optionnel)
- **Thymeleaf** - Template engine

## 📈 Améliorations Futures

- [ ] GraphQL API
- [ ] WebSocket pour mises à jour temps réel
- [ ] ML pour prédictions plus précises
- [ ] Export CSV/PDF
- [ ] Authentification multi-utilisateurs
- [ ] Mobile app
- [ ] Notifications push
- [ ] Graphiques interactifs

