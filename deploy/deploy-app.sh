#!/bin/bash
# ==========================================
# Script de Deploiement - ShopTracker
# Utilise environments/prod/
# ==========================================

set -e

echo ""
echo "========================================="
echo "   ShopTracker - Deploiement VPS       "
echo "========================================="
echo ""

# Variables
APP_DIR="/opt/shoptracker/app"
PROD_DIR="$APP_DIR/environments/prod"
ENV_FILE="$PROD_DIR/.env"
COMPOSE_FILE="$PROD_DIR/docker-compose.yml"

# Verifier que le script est execute par root
if [ "$USER" != "root" ]; then
    echo "[ERREUR] Ce script doit etre execute par root"
    echo "Executez: exit, puis relancez ce script"
    exit 1
fi

echo "[*] Verification des prerequis..."

# Verifier Docker
if ! command -v docker &> /dev/null; then
    echo "[ERREUR] Docker n'est pas installe"
    exit 1
fi

# Verifier Docker Compose
if ! command -v docker &> /dev/null || ! docker compose version &> /dev/null; then
    echo "[ERREUR] Docker Compose n'est pas installe"
    exit 1
fi

# Verifier Git
if ! command -v git &> /dev/null; then
    echo "[ERREUR] Git n'est pas installe"
    exit 1
fi

echo "[OK] Prerequis verifies"
echo ""

# ==========================================
# Clonage/Mise a jour du Repository
# ==========================================

echo "========================================="
echo "   Repository Git                       "
echo "========================================="
echo ""

if [ -d "$APP_DIR/.git" ]; then
    echo "[*] Repository Git existe, mise a jour..."

    # Ajouter le repertoire aux dossiers Git surs
    git config --global --add safe.directory "$APP_DIR" 2>/dev/null || true

    # Corriger les permissions
    chown -R root:root "$APP_DIR" 2>/dev/null || true

    cd "$APP_DIR"
    git pull origin main || git pull origin master
else
    if [ -d "$APP_DIR" ]; then
        echo "[ATTENTION] Le dossier existe mais n'est pas un repository Git"
        echo "[*] Suppression et clone du repository..."
        rm -rf "$APP_DIR"
    fi

    echo "[*] Clonage du repository..."
    echo "[INFO] URL du repository GitHub:"
    echo "https://github.com/pheninux2/TicketCompare.git"
    echo ""

    git clone https://github.com/pheninux2/TicketCompare.git "$APP_DIR"
    cd "$APP_DIR"
fi

echo "[OK] Code recupere"
echo ""

# ==========================================
# Configuration environnement
# ==========================================

echo "========================================="
echo "   Configuration Environnement          "
echo "========================================="
echo ""

# Creer le dossier prod/data et backups
mkdir -p "$PROD_DIR/data/postgres"
mkdir -p "$PROD_DIR/backups"
mkdir -p "$PROD_DIR/logs"

# Copier .env si n'existe pas
if [ ! -f "$ENV_FILE" ]; then
    if [ -f "$PROD_DIR/.env.example" ]; then
        echo "[*] Creation du fichier .env..."
        cp "$PROD_DIR/.env.example" "$ENV_FILE"
    else
        echo "[*] Creation du fichier .env..."
        cat > "$ENV_FILE" << 'EOF'
# Database
DB_PASSWORD=ShopTracker2025!Secure

# Admin
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123

# JWT
JWT_SECRET=your-secret-key-change-in-production

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Stripe (optionnel)
STRIPE_API_KEY=
STRIPE_WEBHOOK_SECRET=
EOF
    fi
    echo "[OK] Fichier .env cree"
    echo "[ATTENTION] Modifiez les mots de passe dans $ENV_FILE"
else
    echo "[OK] Fichier .env existe deja"
fi

echo ""

# ==========================================
# Configuration Nginx
# ==========================================

echo "========================================="
echo "   Configuration Nginx                  "
echo "========================================="
echo ""

NGINX_CONF_FILE="$PROD_DIR/nginx/shoptracker.conf"

if [ -f "$NGINX_CONF_FILE" ]; then
    echo "[*] Installation de la configuration Nginx..."

    # Copier la configuration
    cp "$NGINX_CONF_FILE" /etc/nginx/sites-available/shoptracker

    # Activer le site
    ln -sf /etc/nginx/sites-available/shoptracker /etc/nginx/sites-enabled/shoptracker

    # Supprimer le site par defaut
    rm -f /etc/nginx/sites-enabled/default

    # Tester la configuration
    echo "[*] Test de la configuration Nginx..."
    if nginx -t; then
        echo "[OK] Configuration Nginx valide"
        systemctl reload nginx
        echo "[OK] Nginx recharge"
    else
        echo "[ERREUR] Configuration Nginx invalide"
        exit 1
    fi
else
    echo "[ATTENTION] Configuration Nginx non trouvee dans $NGINX_CONF_FILE"
    echo "[INFO] Creez le fichier ou l'application sera accessible uniquement via IP"
fi

echo ""

# ==========================================
# Build de l'application
# ==========================================

echo "========================================="
echo "   Build de l'Application               "
echo "========================================="
echo ""

cd "$PROD_DIR"

echo "[*] Arret des conteneurs existants..."
docker compose down 2>/dev/null || true

echo "[*] Build de l'image Docker..."
echo "[INFO] Cette etape peut prendre 5-10 minutes..."
docker compose build --no-cache

echo "[OK] Image construite avec succes"
echo ""

# ==========================================
# Demarrage des services
# ==========================================

echo "========================================="
echo "   Demarrage des Services               "
echo "========================================="
echo ""

echo "[*] Demarrage des conteneurs..."
docker compose up -d

echo ""
echo "[*] Attente du demarrage (30 secondes)..."
sleep 30

# Verifier les services
echo ""
echo "[*] Verification des services..."

if docker ps | grep -q shoptracker-db; then
    echo "[OK] PostgreSQL est operationnel"
else
    echo "[ERREUR] PostgreSQL n'a pas demarre"
fi

if docker ps | grep -q shoptracker-app; then
    echo "[OK] Application est operationnelle"
else
    echo "[ERREUR] Application n'a pas demarre"
fi

# Afficher les logs
echo ""
echo "[*] Derniers logs de l'application:"
docker compose logs --tail=20 app

echo ""
echo "========================================="
echo "   Deploiement Termine !                "
echo "========================================="
echo ""
echo "Application accessible sur:"
echo "  http://$(curl -s ifconfig.me 2>/dev/null || echo 'VOTRE_IP')"
echo ""
echo "Commandes utiles:"
echo "  docker compose logs -f app           # Voir les logs"
echo "  docker compose ps                    # Statut des conteneurs"
echo "  docker compose restart app           # Redemarrer l'app"
echo "  docker compose down                  # Arreter tous les services"
echo ""
echo "Fichiers de configuration:"
echo "  $ENV_FILE"
echo "  $COMPOSE_FILE"
echo ""

