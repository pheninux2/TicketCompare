# ===================================
# Multi-stage build pour TicketCompare
# ===================================

# Stage 1: Build de l'application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copier les fichiers Maven pour le cache des dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Builder l'application (skip tests pour accélérer)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime avec image légère
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Installer Tesseract OCR et les langues nécessaires
RUN apk add --no-cache \
    tesseract-ocr \
    tesseract-ocr-data-fra \
    tesseract-ocr-data-eng \
    ttf-dejavu && \
    # Vérifier où sont les données Tesseract
    mkdir -p /app/tessdata && \
    # Créer des liens symboliques vers les données de langue
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

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=default \
    JAVA_OPTS="-Xmx512m -Xms256m" \
    TESSDATA_PREFIX=/usr/share/tessdata \
    LC_ALL=C.UTF-8 \
    LANG=C.UTF-8

# Exposer le port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Lancer l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

