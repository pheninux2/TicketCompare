#!/bin/bash
# ==========================================
# Script de Déploiement Simplifié - ShopTracker
# Utilise des images Docker pré-construites
# depuis GitHub Container Registry (GHCR)
# ==========================================

set -e

echo ""
echo "========================================="
echo "   ShopTracker - Déploiement Image     "
echo "========================================="
echo ""

# Variables
APP_DIR="/opt/shoptracker/app1"
PROD_DIR="$APP_DIR/environments/prod"
ENV_FILE="$PROD_DIR/.env"
COMPOSE_FILE="$PROD_DIR/docker-compose-pull.yml"

# Configuration GitHub Container Registry
GITHUB_USER="${GITHUB_USER:-pheninux2}"
GITHUB_REPO="${GITHUB_REPO:-ticketcompare}"
IMAGE_NAME="ghcr.io/${GITHUB_USER}/${GITHUB_REPO}:latest"

# Vérifier que le script est exécuté par root
if [ "$USER" != "root" ]; then
    echo "[ERREUR] Ce script doit être exécuté par root"
    echo "Exécutez: sudo su - puis relancez ce script"
    exit 1
fi

echo "[*] Vérification des prérequis..."

# Vérifier Docker
if ! command -v docker &> /dev/null; then
    echo "[ERREUR] Docker n'est pas installé"
    exit 1
fi

# Vérifier Docker Compose
if ! docker compose version &> /dev/null; then
    echo "[ERREUR] Docker Compose n'est pas installé"
    exit 1
fi

echo "[OK] Prérequis vérifiés"
echo ""

# ==========================================
# Authentification GitHub Container Registry
# ==========================================

echo "========================================="
echo "   Authentification GitHub              "
echo "========================================="
echo ""

if [ -z "$GITHUB_TOKEN" ]; then
    echo "[INFO] Variable GITHUB_TOKEN non définie"
    echo "[INFO] Pour les images publiques, pas d'authentification nécessaire"
    echo "[INFO] Pour les images privées, définissez:"
    echo "        export GITHUB_TOKEN=ghp_votre_token"
    echo ""
else
    echo "[*] Authentification à GitHub Container Registry..."
    echo "$GITHUB_TOKEN" | docker login ghcr.io -u "$GITHUB_USER" --password-stdin
    echo "[OK] Authentification réussie"
    echo ""
fi

# ==========================================
# Pull de l'image Docker
# ==========================================

echo "========================================="
echo "   Pull de l'Image Docker               "
echo "========================================="
echo ""

echo "[*] Image à télécharger: $IMAGE_NAME"
echo "[*] Pull de l'image depuis GitHub..."

if docker pull "$IMAGE_NAME"; then
    echo "[OK] Image téléchargée avec succès"
else
    echo "[ERREUR] Impossible de télécharger l'image"
    echo "[INFO] Vérifiez:"
    echo "  1. Que l'image existe: https://github.com/${GITHUB_USER}/${GITHUB_REPO}/pkgs/container/${GITHUB_REPO}"
    echo "  2. Que vous avez les droits d'accès (pour images privées)"
    echo "  3. Que GITHUB_TOKEN est correctement défini"
    exit 1
fi

echo ""

# ==========================================
# Configuration environnement
# ==========================================

echo "========================================="
echo "   Configuration Environnement          "
echo "========================================="
echo ""

# Créer la structure de dossiers
mkdir -p "$APP_DIR/environments/prod/data/postgres"
mkdir -p "$APP_DIR/environments/prod/data/uploads"
mkdir -p "$APP_DIR/environments/prod/data/logs"
mkdir -p "$APP_DIR/environments/prod/backups"

# Créer le fichier .env si n'existe pas
if [ ! -f "$ENV_FILE" ]; then
    echo "[*] Création du fichier .env..."
    cat > "$ENV_FILE" << 'EOF'
# ===================================
# Configuration Production - ShopTracker
# ===================================

# Database
DB_PASSWORD=ShopTracker2025!Secure

# Admin
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
ADMIN_EMAIL=admin@shoptracker.local

# JWT
JWT_SECRET=your-secret-key-change-in-production-$(openssl rand -base64 32)

# Email (optionnel)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Stripe (optionnel)
STRIPE_API_KEY=
STRIPE_WEBHOOK_SECRET=

# Application
APP_BASE_URL=http://178.128.162.253/app1
SERVER_SERVLET_CONTEXT_PATH=/

# Image Docker
DOCKER_IMAGE=ghcr.io/pheninux2/ticketcompare:latest
EOF
    echo "[OK] Fichier .env créé"
    echo "[ATTENTION] Modifiez les mots de passe dans $ENV_FILE"
else
    echo "[OK] Fichier .env existe déjà"
fi

# Créer le docker-compose-pull.yml
echo "[*] Création du fichier docker-compose-pull.yml..."
cat > "$COMPOSE_FILE" << 'EOF'
# ===================================
# ShopTracker - Production avec Image Pré-construite
# ===================================

services:
  # Base de données PostgreSQL
  postgres:
    image: postgres:15-alpine
    container_name: shoptracker-app1-db
    restart: unless-stopped
    environment:
      POSTGRES_DB: shoptracker
      POSTGRES_USER: shoptracker_admin
      POSTGRES_PASSWORD: ${DB_PASSWORD:-ShopTracker2025!Secure}
      PGDATA: /var/lib/postgresql/data/pgdata
    volumes:
      - ./data/postgres:/var/lib/postgresql/data
      - ./backups:/backups
    ports:
      - "127.0.0.1:5432:5432"
    networks:
      - shoptracker-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U shoptracker_admin -d shoptracker"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Application ShopTracker (depuis GHCR)
  app:
    image: ${DOCKER_IMAGE:-ghcr.io/pheninux2/ticketcompare:latest}
    container_name: shoptracker-app1
    restart: unless-stopped
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      # Configuration production
      SPRING_PROFILES_ACTIVE: prod

      # Base de données PostgreSQL
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/shoptracker
      SPRING_DATASOURCE_USERNAME: shoptracker_admin
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-ShopTracker2025!Secure}

      # Hibernate
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_JPA_SHOW_SQL: false

      # Tesseract OCR
      TESSDATA_PREFIX: /usr/share/tessdata

      # Sécurité
      SERVER_PORT: 8080
      SERVER_SERVLET_CONTEXT_PATH: ${SERVER_SERVLET_CONTEXT_PATH:-/}

      # Email (optionnel)
      MAIL_USERNAME: ${MAIL_USERNAME:-}
      MAIL_PASSWORD: ${MAIL_PASSWORD:-}

      # Stripe (optionnel)
      STRIPE_API_KEY: ${STRIPE_API_KEY:-}
      STRIPE_WEBHOOK_SECRET: ${STRIPE_WEBHOOK_SECRET:-}

      # Application
      APP_BASE_URL: ${APP_BASE_URL:-http://localhost:8080}

    volumes:
      - ./data/uploads:/app/uploads
      - ./data/logs:/app/logs
    ports:
      - "8080:8080"
    networks:
      - shoptracker-network
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

networks:
  shoptracker-network:
    driver: bridge

EOF

echo "[OK] Fichier docker-compose-pull.yml créé"
echo ""

# ==========================================
# Configuration Nginx Multi-Apps
# ==========================================

echo "========================================="
echo "   Configuration Nginx Multi-Apps       "
echo "========================================="
echo ""

NGINX_CONF="/etc/nginx/sites-available/multi-app"

if [ -f "/etc/nginx/nginx.conf" ]; then
    echo "[*] Installation de la configuration Nginx multi-applications..."

    cat > "$NGINX_CONF" << 'NGINX_EOF'
server {
    listen 80;
    server_name _;

    client_max_body_size 10M;

    # Logs
    access_log /var/log/nginx/multi_app_access.log;
    error_log /var/log/nginx/multi_app_error.log;

    # Application 1 - ShopTracker
    location /app1/ {
        rewrite ^/app1/(.*) /$1 break;
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Prefix /app1;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    location /app1/actuator/health {
        rewrite ^/app1/(.*) /$1 break;
        proxy_pass http://localhost:8080/actuator/health;
        access_log off;
    }

    # Application 2 (Exemple - port 8081)
    location /app2/ {
        rewrite ^/app2/(.*) /$1 break;
        proxy_pass http://localhost:8081/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Prefix /app2;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Page d'accueil
    location = / {
        return 200 '<!DOCTYPE html>
<html>
<head>
    <title>VPS Applications</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h1 { color: #333; }
        .app-list { list-style: none; padding: 0; }
        .app-list li { margin: 15px 0; }
        .app-list a { display: block; padding: 15px; background: #f0f0f0; text-decoration: none; color: #333; border-radius: 5px; }
        .app-list a:hover { background: #e0e0e0; }
    </style>
</head>
<body>
    <h1>Applications disponibles</h1>
    <ul class="app-list">
        <li><a href="/app1/">Application 1 - ShopTracker</a></li>
        <li><a href="/app2/">Application 2</a></li>
    </ul>
</body>
</html>';
        add_header Content-Type text/html;
    }
}
NGINX_EOF

    # Activer la configuration
    ln -sf "$NGINX_CONF" /etc/nginx/sites-enabled/multi-app

    # Supprimer l'ancienne configuration par défaut
    rm -f /etc/nginx/sites-enabled/default
    rm -f /etc/nginx/sites-enabled/shoptracker

    # Tester et recharger Nginx
    if nginx -t; then
        echo "[OK] Configuration Nginx valide"
        systemctl reload nginx
        echo "[OK] Nginx rechargé"
    else
        echo "[ERREUR] Configuration Nginx invalide"
        exit 1
    fi
else
    echo "[ATTENTION] Nginx n'est pas installé ou configuré"
fi

echo ""

# ==========================================
# Démarrage des services
# ==========================================

echo "========================================="
echo "   Démarrage des Services               "
echo "========================================="
echo ""

cd "$PROD_DIR"

echo "[*] Arrêt des conteneurs existants..."
docker compose -f docker-compose-pull.yml down 2>/dev/null || true

echo "[*] Démarrage des conteneurs avec l'image téléchargée..."
docker compose -f docker-compose-pull.yml up -d

echo ""
echo "[*] Attente du démarrage (30 secondes)..."
sleep 30

# Vérifier les services
echo ""
echo "[*] Vérification des services..."

if docker ps | grep -q shoptracker-app1-db; then
    echo "[OK] PostgreSQL est opérationnel"
else
    echo "[ERREUR] PostgreSQL n'a pas démarré"
fi

if docker ps | grep -q shoptracker-app1; then
    echo "[OK] Application est opérationnelle"
else
    echo "[ERREUR] Application n'a pas démarré"
fi

# Afficher les logs
echo ""
echo "[*] Derniers logs de l'application:"
docker compose -f docker-compose-pull.yml logs --tail=20 app

echo ""
echo "========================================="
echo "   Déploiement Terminé !                "
echo "========================================="
echo ""
echo "Application accessible sur:"
echo "  http://178.128.162.253/app1/"
echo ""
echo "Image Docker utilisée:"
echo "  $IMAGE_NAME"
echo ""
echo "Commandes utiles:"
echo "  cd $PROD_DIR"
echo "  docker compose -f docker-compose-pull.yml logs -f app    # Voir les logs"
echo "  docker compose -f docker-compose-pull.yml ps             # Statut"
echo "  docker compose -f docker-compose-pull.yml restart app    # Redémarrer"
echo "  docker compose -f docker-compose-pull.yml down           # Arrêter"
echo ""
echo "Pour mettre à jour avec une nouvelle image:"
echo "  docker pull $IMAGE_NAME"
echo "  docker compose -f docker-compose-pull.yml up -d"
echo ""
ker