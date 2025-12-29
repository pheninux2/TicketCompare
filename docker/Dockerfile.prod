# ===================================
# Multi-stage build pour ShopTracker PRODUCTION
# ===================================

# Stage 1: Build de l'application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copier les fichiers Maven pour le cache des dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Builder l'application en mode production
RUN mvn clean package -DskipTests -Pprod -B

# Stage 2: Runtime optimisé pour production
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Mohamed Adil HADDAD <adil.haddad.xdev@gmail.com>"
LABEL description="ShopTracker Production - Windows Deployment"
LABEL version="1.0.0"

WORKDIR /app

# Installer Tesseract OCR et utilitaires système
RUN apk add --no-cache \
    tesseract-ocr \
    tesseract-ocr-data-fra \
    tesseract-ocr-data-eng \
    ttf-dejavu \
    wget \
    curl \
    bash && \
    # Vérifier installation Tesseract
    tesseract --version && \
    # Créer liens symboliques
    mkdir -p /app/tessdata && \
    ln -sf /usr/share/tessdata/* /app/tessdata/ 2>/dev/null || true

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Copier le JAR depuis l'étape de build
COPY --from=builder /app/target/*.jar app.jar

# Créer les dossiers nécessaires et ajuster les permissions
RUN mkdir -p /app/uploads /app/logs /app/data && \
    chown -R appuser:appuser /app && \
    chmod -R 755 /app && \
    # Donner accès en lecture aux données Tesseract
    chmod -R 755 /usr/share/tessdata 2>/dev/null || true

# Passer à l'utilisateur non-root
USER appuser

# Exposer le port de l'application
EXPOSE 8080

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod \
    TESSDATA_PREFIX=/usr/share/tessdata \
    JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Lancer l'application avec optimisations JVM
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

