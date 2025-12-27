# TicketCompare - Application de Gestion de Tickets de Caisse

Application complète pour gérer vos tickets de caisse, analyser vos dépenses et prédire l'évolution des prix.

## ✨ Fonctionnalités

### 📋 Gestion des Tickets
- Créer, lire, modifier et supprimer des tickets
- Ajouter plusieurs produits par ticket
- Filtrer par date et magasin
- Historique complet des achats

### 📊 Statistiques
- Analyse des prix par catégorie (min, max, moyenne)
- Top 10 des produits les plus chers
- Top 10 des produits les moins chers
- Comparaison de prix pour un même produit
- Historique des prix avec tendances

### 📈 Consommation
- Suivi de la consommation hebdomadaire
- Top 10 des produits les plus consommés par semaine
- Coût moyen par unité
- Tendances de consommation sur plusieurs semaines

### 🔮 Prédictions
- Prédiction d'évolution des prix (régression linéaire)
- Analyse de tendance (hausse/baisse)
- Niveau de confiance de la prédiction
- Analyse sur 6 mois avec historique complet

## 🛠️ Stack Technique

- **Backend**: Spring Boot 4.0 + Java 21
- **Frontend**: HTML5, Bootstrap 5, JavaScript, HTMX
- **Base de données**: H2 (développement) ou MySQL (production)
- **ORM**: JPA/Hibernate
- **Build**: Gradle

## 📦 Dépendances Principales

```groovy
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-thymeleaf
- io.github.wimdeblauwe:htmx-spring-boot-thymeleaf:5.0.0
- org.projectlombok:lombok
- com.h2database:h2
- mysql:mysql-connector-java:8.0.33
```

## 🚀 Démarrage

### Prérequis
- Java 21+
- Gradle 8.0+

### Mode Développement (H2)

```bash
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\gradlew bootRun
```

L'application sera disponible à: **http://localhost:8080**

Console H2: **http://localhost:8080/h2-console**
- URL: `jdbc:h2:mem:ticketcomparedb`
- Utilisateur: `sa`
- Mot de passe: (vide)

### Mode Production (MySQL)

1. Créer une base de données MySQL:
```sql
CREATE DATABASE ticket_compare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Modifier `application.properties`:
```properties
spring.profiles.active=prod
spring.datasource.url=jdbc:mysql://localhost:3306/ticket_compare
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

3. Démarrer:
```bash
.\gradlew bootRun --args='--spring.profiles.active=prod'
```

## 📁 Structure du Projet

```
src/
├── main/
│   ├── java/pheninux/xdev/ticketcompare/
│   │   ├── controller/          # Contrôleurs MVC
│   │   ├── entity/              # Entités JPA
│   │   ├── repository/          # Repositories JPA
│   │   ├── service/             # Logique métier
│   │   ├── dto/                 # Data Transfer Objects
│   │   └── TicketCompareApplication.java
│   └── resources/
│       ├── templates/           # Pages Thymeleaf/HTML
│       │   ├── tickets/
│       │   ├── statistics/
│       │   ├── consumption/
│       │   ├── analysis/
│       │   └── index.html
│       ├── application.properties
│       └── data.sql
└── test/
```

## 🔄 Entités et Relations

### Ticket
- Date, magasin, montant total
- Relation 1:N avec Product

### Product
- Nom, catégorie, prix, quantité, unité
- Lié à un Ticket

### PriceHistory
- Historique des prix d'un produit
- Index sur (product_name, price_date) pour les requêtes rapides

### ConsumptionStatistic
- Consommation agrégée par semaine et produit
- Index sur (product_name, week_start)

### StatisticSnapshot
- Cache des statistiques par catégorie

## 🎯 Pages Principales

### Navigation
- **Home** `/` - Accueil avec présentation
- **Tickets** `/tickets` - Gestion des tickets
- **Statistiques** `/statistics/dashboard` - Tableau de bord
- **Consommation** `/consumption/weekly` - Suivi hebdomadaire
- **Prédictions** `/analysis/forecast` - Prédiction des prix

### Pages de Détail
- `/tickets/{id}` - Détails d'un ticket
- `/tickets/create` - Créer un ticket
- `/statistics/expensive` - Produits chers
- `/statistics/cheap` - Produits pas chers
- `/consumption/top-products` - Top produits consommés
- `/analysis/trend` - Tendance des prix

## 📊 Algorithmes

### Prédiction de Prix
**Méthode**: Régression linéaire simple
- Collecte l'historique des prix d'un produit
- Calcule la pente et l'intercept
- Prédit la valeur future
- Calcul du R² pour le niveau de confiance

```java
Confiance = HIGH (R² > 0.8), MEDIUM (0.5-0.8), LOW (< 0.5)
```

### Consommation
- Groupe les produits par semaine (ISO week)
- Agrège quantité, coût, nombre d'achats
- Calcule le coût moyen par unité

## 🔐 Sécurité

- Validation des entrées avec Jakarta Validation
- Transactions gérées par Spring
- Delete en cascade pour les produits avec ticket
- Pas de données sensibles stockées

## 📝 Exemples d'Utilisation

### Créer un ticket
1. Aller sur `/tickets`
2. Cliquer "Nouveau Ticket"
3. Entrer date, magasin, montant
4. Ajouter produits avec prix et quantité
5. Soumettre

### Analyser les prix
1. Aller sur `/statistics/dashboard`
2. Voir les catégories avec min/max/moyenne
3. Cliquer sur une catégorie pour détails
4. Aller sur `/statistics/expensive` ou `/cheap`

### Prédire les prix
1. Aller sur `/analysis/forecast`
2. Entrer un nom de produit
3. Spécifier le nombre de jours (défaut: 30)
4. Voir prédiction et tendance
5. Confiance basée sur historique

### Consommation
1. Aller sur `/consumption/weekly`
2. Voir la consommation de la semaine actuelle
3. Cliquer sur "Top Produits" pour les plus achetés
4. Voir tendances de consommation

## 🐛 Dépannage

### Base de données vide au démarrage
- Les données de test sont chargées depuis `data.sql`
- Vérifier que `spring.jpa.hibernate.ddl-auto=create-drop`

### Erreur lors de la création d'un ticket
- Vérifier les formats de date (YYYY-MM-DD)
- Vérifier que montant total est un nombre valide

### Prédictions non disponibles
- Au moins 2 observations historiques nécessaires
- Peut falloir attendre plusieurs tickets du même produit

## 📚 API REST

Les endpoints API renvoient du JSON et sont utilisés par HTMX:

- `GET /statistics/api/price-comparison?product={name}`
- `GET /analysis/api/forecast?product={name}&days={days}`
- `GET /analysis/api/trend?product={name}&months={months}`

## 🎨 Styling

- Bootstrap 5.3 pour les composants
- Couleurs personnalisées:
  - Primary: #2563eb (Bleu)
  - Secondary: #10b981 (Vert)
  - Danger: #ef4444 (Rouge)

## 📦 Build et Déploiement

### Package JAR
```bash
.\gradlew build
java -jar build/libs/ticketcompare-0.0.1-SNAPSHOT.jar
```

### Docker (optionnel)
```dockerfile
FROM eclipse-temurin:21-jre
COPY build/libs/ticketcompare-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📝 Licence

Tous droits réservés © 2024

## 👨‍💻 Support

Pour plus d'informations, consultez les fichiers sources ou le HELP.md

