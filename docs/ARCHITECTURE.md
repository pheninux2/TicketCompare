# 🏗️ Architecture - TicketCompare

Documentation de l'architecture de l'application TicketCompare.

## 📋 Table des Matières

- [Vue d'Ensemble](#vue-densemble)
- [Architecture Technique](#architecture-technique)
- [Structure du Code](#structure-du-code)
- [Base de Données](#base-de-données)
- [Flux de Données](#flux-de-données)
- [Sécurité](#sécurité)

## 🎯 Vue d'Ensemble

TicketCompare est une application web monolithique construite avec Spring Boot suivant une architecture MVC (Model-View-Controller) en 3 couches.

```
┌─────────────────────────────────────────┐
│         Présentation (Web)              │
│    Thymeleaf + Bootstrap + HTMX        │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          Couche Contrôleur              │
│    @Controller + @RestController       │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          Couche Service                 │
│    Logique métier + OCR                │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│        Couche Repository                │
│    Spring Data JPA + Hibernate         │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          Base de Données                │
│    PostgreSQL / H2                     │
└─────────────────────────────────────────┘
```

## 🔧 Architecture Technique

### Stack Technique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| Langage | Java | 21 |
| Framework | Spring Boot | 4.0 |
| ORM | Hibernate / JPA | 6.x |
| Template Engine | Thymeleaf | 3.1 |
| DB Production | PostgreSQL | 16 |
| DB Développement | H2 | 2.x |
| Build Tool | Maven | 3.9 |
| OCR | Tesseract | 5.x |
| Conteneurisation | Docker | 20+ |

### Dépendances Principales

```xml
<!-- Web -->
spring-boot-starter-web

<!-- Data -->
spring-boot-starter-data-jpa
postgresql / h2

<!-- View -->
spring-boot-starter-thymeleaf
htmx-spring-boot-thymeleaf

<!-- OCR -->
tess4j (Tesseract Java wrapper)

<!-- Utilities -->
lombok
commons-csv
commons-io
```

## 📁 Structure du Code

### Package Principal : `pheninux.xdev.ticketcompare`

```
pheninux.xdev.ticketcompare/
│
├── config/                     # Configuration Spring
│   ├── WebConfig.java
│   ├── TesseractConfig.java
│   └── SecurityConfig.java
│
├── controller/                 # Contrôleurs MVC
│   ├── TicketController.java      # CRUD tickets
│   ├── ProductController.java     # Gestion produits
│   ├── CategoryController.java    # Gestion catégories
│   └── ScannerController.java     # Upload & OCR
│
├── model/                      # Entités JPA
│   ├── Ticket.java                # Entité Ticket
│   ├── Product.java               # Entité Produit
│   └── Category.java              # Entité Catégorie
│
├── repository/                 # Repositories Spring Data
│   ├── TicketRepository.java
│   ├── ProductRepository.java
│   └── CategoryRepository.java
│
├── service/                    # Logique métier
│   ├── TicketService.java         # Service CRUD
│   ├── TicketOCRService.java      # Service OCR
│   ├── ProductService.java
│   └── ExportService.java         # Export CSV
│
├── dto/                        # Data Transfer Objects
│   ├── TicketDTO.java
│   └── ProductDTO.java
│
├── exception/                  # Gestion des erreurs
│   ├── TicketNotFoundException.java
│   └── OCRException.java
│
└── TicketCompareApplication.java  # Point d'entrée
```

## 🗄️ Base de Données

### Modèle de Données

```sql
┌─────────────────────┐
│      TICKET         │
├─────────────────────┤
│ id (PK)            │
│ date               │
│ store              │
│ total_amount       │
│ notes              │
│ created_at         │
│ updated_at         │
└─────────────────────┘
          │
          │ 1:N
          ↓
┌─────────────────────┐
│      PRODUCT        │
├─────────────────────┤
│ id (PK)            │
│ ticket_id (FK)     │
│ name               │
│ price              │
│ quantity           │
│ unit               │
│ total_price        │
│ category           │
└─────────────────────┘
```

### Entité Ticket

```java
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate date;
    private String store;
    private BigDecimal totalAmount;
    private String notes;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Product> products;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### Entité Product

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;
    
    private String name;
    private BigDecimal price;
    private Double quantity;
    private String unit;
    private BigDecimal totalPrice;
    private String category;
}
```

### Stratégies de Migration

**Développement (H2) :**
```yaml
spring.jpa.hibernate.ddl-auto: create-drop
```

**Production (PostgreSQL) :**
```yaml
spring.jpa.hibernate.ddl-auto: update
```

Pour les migrations complexes, utiliser **Flyway** ou **Liquibase** (à implémenter).

## 🔄 Flux de Données

### 1. Création Manuelle d'un Ticket

```
User
  ↓ (Formulaire)
TicketController.create()
  ↓
TicketService.createTicket()
  ↓
TicketRepository.save()
  ↓
Database
  ↓ (Redirection)
View: ticket/detail
```

### 2. Scan OCR d'un Ticket

```
User
  ↓ (Upload Image)
ScannerController.scanTicket()
  ↓
TicketOCRService.processImage()
  ↓ (Tesseract)
Extraction texte
  ↓ (Parsing)
TicketDTO + ProductDTO[]
  ↓
TicketService.createFromOCR()
  ↓
Database
  ↓
View: ticket/detail
```

### 3. Export CSV

```
User
  ↓ (Demande export)
ExportController.exportCSV()
  ↓
TicketService.findAll()
  ↓
ExportService.generateCSV()
  ↓ (Apache Commons CSV)
CSV File
  ↓ (Download)
User
```

## 🔐 Sécurité

### Mesures de Sécurité Actuelles

| Aspect | Implémentation |
|--------|----------------|
| SQL Injection | JPA/Hibernate PreparedStatements |
| XSS | Thymeleaf auto-escaping |
| CSRF | Spring Security (à activer) |
| File Upload | Validation taille et type |
| Input Validation | Bean Validation (@Valid) |

### Sécurité à Améliorer

- [ ] Authentification utilisateur (Spring Security)
- [ ] Autorisation basée sur les rôles
- [ ] HTTPS obligatoire en production
- [ ] Rate limiting sur les endpoints
- [ ] Audit logging
- [ ] Chiffrement des données sensibles

## 🐳 Architecture Docker

```
┌─────────────────────────────────────┐
│      Docker Network                 │
│  ticketcompare_network              │
│                                     │
│  ┌─────────────────────────────┐  │
│  │  Container: postgres        │  │
│  │  Image: postgres:16-alpine  │  │
│  │  Port: 5432                 │  │
│  │  Volume: postgres_data      │  │
│  └─────────────────────────────┘  │
│               ↑                     │
│               │                     │
│  ┌─────────────────────────────┐  │
│  │  Container: app             │  │
│  │  Image: ticketcompare       │  │
│  │  Port: 8080                 │  │
│  │  Volumes:                   │  │
│  │   - app_uploads             │  │
│  │   - app_logs                │  │
│  └─────────────────────────────┘  │
│                                     │
│  ┌─────────────────────────────┐  │
│  │  Container: pgadmin         │  │
│  │  (Optionnel)                │  │
│  │  Port: 5050                 │  │
│  └─────────────────────────────┘  │
└─────────────────────────────────────┘
```

### Build Multi-stage

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add tesseract-ocr
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 🚀 Performance

### Optimisations Implémentées

- **Connection Pooling** : HikariCP (par défaut)
- **JPA Batch Inserts** : Hibernate batch size = 20
- **Lazy Loading** : Relations @OneToMany
- **Caching** : Thymeleaf template cache (prod)

### Métriques avec Actuator

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

Endpoints disponibles :
- `/actuator/health` - Santé de l'application
- `/actuator/metrics` - Métriques JVM et application
- `/actuator/info` - Informations sur l'application

## 📊 Monitoring

### Logs

**Niveaux de log :**
- **Développement** : DEBUG
- **Production** : INFO / WARN

**Rotation des logs :**
```yaml
logging:
  file:
    max-size: 10MB
    max-history: 30
```

### Health Checks

**Docker Compose :**
```yaml
healthcheck:
  test: ["CMD", "wget", "--spider", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
```

## 🔮 Évolutions Futures

### Phase 2 : API REST

- Création d'une API REST complète
- Documentation OpenAPI/Swagger
- Versionning de l'API

### Phase 3 : Microservices

- Séparation OCR en microservice
- Message Queue (RabbitMQ/Kafka)
- Service de notifications

### Phase 4 : Scalabilité

- Load Balancer
- Cache distribué (Redis)
- Réplication base de données

---

**Documentation maintenue par : Pheninux XDev**  
**Dernière mise à jour : Décembre 2024**

