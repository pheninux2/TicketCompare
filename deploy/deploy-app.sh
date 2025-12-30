#!/bin/bash
# ==========================================
# Script de Déploiement - ShopTracker
# VPS DigitalOcean
# ==========================================

set -e

echo ""
echo "========================================="
echo "   ShopTracker - Déploiement           "
echo "========================================="
echo ""

# Vérifier que le script est exécuté par deployer ou root
if [ "$USER" != "deployer" ] && [ "$USER" != "root" ]; then
    echo "[ERREUR] Ce script doit être exécuté par l'utilisateur 'deployer' ou 'root'"
    exit 1
fi

APP_DIR="/opt/shoptracker/app"
SCRIPTS_DIR="/opt/shoptracker/scripts"
NGINX_CONF="/etc/nginx/sites-available/shoptracker"

echo "[*] Vérification des prérequis..."

# Vérifier Docker
if ! command -v docker &> /dev/null; then
    echo "[ERREUR] Docker n'est pas installé. Exécutez d'abord setup-vps.sh"
    exit 1
fi

# Vérifier Nginx
if ! command -v nginx &> /dev/null; then
    echo "[ERREUR] Nginx n'est pas installé. Exécutez d'abord setup-vps.sh"
    exit 1
fi

echo "[OK] Prérequis vérifiés"

echo ""
echo "========================================="
echo "   Clonage du Repository                "
echo "========================================="
echo ""

if [ -d "$APP_DIR" ]; then
    echo "[*] Le dossier existe déjà, mise à jour..."
    cd "$APP_DIR"
    git pull origin main
else
    echo "[*] Clonage du repository..."
    echo "[INFO] Entrez l'URL de votre repository GitHub:"
    read -r REPO_URL

    git clone "$REPO_URL" "$APP_DIR"
    cd "$APP_DIR"
fi

echo "[OK] Code récupéré"

echo ""
echo "========================================="
echo "   Configuration de l'environnement     "
echo "========================================="
echo ""

# Vérifier si .env existe déjà
if [ -f "$APP_DIR/deploy/.env.production" ]; then
    echo "[OK] Fichier .env.production existe déjà"
else
    echo "[*] Création du fichier .env.production..."

    # Copier le template
    cp "$APP_DIR/deploy/.env.production.template" "$APP_DIR/deploy/.env.production"

    # Récupérer les credentials générés
    if [ -f "/root/credentials/app_credentials.txt" ]; then
        DB_PASSWORD=$(grep "DB_PASSWORD=" /root/credentials/app_credentials.txt | cut -d'=' -f2)
        ADMIN_PASSWORD=$(grep "ADMIN_PASSWORD=" /root/credentials/app_credentials.txt | cut -d'=' -f2)
        JWT_SECRET=$(grep "JWT_SECRET=" /root/credentials/app_credentials.txt | cut -d'=' -f2)

        # Remplacer les placeholders
        sed -i "s|DB_PASSWORD=.*|DB_PASSWORD=$DB_PASSWORD|" "$APP_DIR/deploy/.env.production"
        sed -i "s|ADMIN_PASSWORD=.*|ADMIN_PASSWORD=$ADMIN_PASSWORD|" "$APP_DIR/deploy/.env.production"
        sed -i "s|JWT_SECRET=.*|JWT_SECRET=$JWT_SECRET|" "$APP_DIR/deploy/.env.production"

        echo "[OK] Variables d'environnement configurées"
    else
        echo "[AVERTISSEMENT] Credentials non trouvés. Éditez manuellement .env.production"
    fi
fi

# Copier le fichier .env pour Docker Compose
cp "$APP_DIR/deploy/.env.production" "$APP_DIR/deploy/.env"

echo ""
echo "========================================="
echo "   Configuration de Nginx               "
echo "========================================="
echo ""

echo "[*] Installation de la configuration Nginx..."

# Copier la configuration
sudo cp "$APP_DIR/deploy/nginx/shoptracker.conf" "$NGINX_CONF"

# Activer le site
sudo ln -sf "$NGINX_CONF" /etc/nginx/sites-enabled/shoptracker

# Supprimer le site par défaut
sudo rm -f /etc/nginx/sites-enabled/default

# Tester la configuration
echo "[*] Test de la configuration Nginx..."
sudo nginx -t

if [ $? -eq 0 ]; then
    echo "[OK] Configuration Nginx valide"
    sudo systemctl reload nginx
    echo "[OK] Nginx rechargé"
else
    echo "[ERREUR] Configuration Nginx invalide"
    exit 1
fi

echo ""
echo "========================================="
echo "   Build de l'Application               "
echo "========================================="
echo ""

cd "$APP_DIR"

echo "[*] Build de l'image Docker..."
echo "[INFO] Cette étape peut prendre 5-10 minutes..."

# Build avec Docker Compose
docker compose -f deploy/docker-compose.prod.yml build --no-cache

echo "[OK] Image construite avec succès"

echo ""
echo "========================================="
echo "   Démarrage des Services               "
echo "========================================="
echo ""

echo "[*] Arrêt des anciens conteneurs..."
docker compose -f deploy/docker-compose.prod.yml down 2>/dev/null || true

echo "[*] Démarrage des services..."
docker compose -f deploy/docker-compose.prod.yml up -d

echo "[*] Attente du démarrage (30 secondes)..."
sleep 30

echo ""
echo "========================================="
echo "   Vérification du Déploiement          "
echo "========================================="
echo ""

# Vérifier PostgreSQL
echo "[*] Vérification de PostgreSQL..."
if docker exec shoptracker-db pg_isready -U shoptracker_admin > /dev/null 2>&1; then
    echo "✅ PostgreSQL est opérationnel"
else
    echo "❌ PostgreSQL n'est pas accessible"
fi

# Vérifier l'application
echo "[*] Vérification de l'application..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Application Spring Boot est opérationnelle"
else
    echo "⚠️  Application Spring Boot démarre encore (peut prendre jusqu'à 2 minutes)"
fi

# Vérifier Nginx
echo "[*] Vérification de Nginx..."
if curl -s http://localhost > /dev/null 2>&1; then
    echo "✅ Nginx est opérationnel"
else
    echo "❌ Nginx n'est pas accessible"
fi

echo ""
echo "========================================="
echo "   Logs des Services                    "
echo "========================================="
echo ""

echo "Dernières lignes des logs de l'application:"
docker compose -f deploy/docker-compose.prod.yml logs --tail=20 app

echo ""
echo "========================================="
echo "   Déploiement Terminé ! ✅             "
echo "========================================="
echo ""

# Récupérer l'IP publique
PUBLIC_IP=$(curl -s ifconfig.me)

echo "🌐 Votre application est accessible à:"
echo "   http://$PUBLIC_IP"
echo ""
echo "🔐 Credentials Admin:"
echo "   Username: admin"
echo "   Password: Voir /root/credentials/app_credentials.txt"
echo ""
echo "📊 Commandes utiles:"
echo "   - Voir les logs:        docker compose -f deploy/docker-compose.prod.yml logs -f"
echo "   - Redémarrer l'app:     docker compose -f deploy/docker-compose.prod.yml restart"
echo "   - Arrêter les services: docker compose -f deploy/docker-compose.prod.yml down"
echo "   - Status des services:  docker compose -f deploy/docker-compose.prod.yml ps"
echo ""
echo "💾 Backup automatique configuré à 2h du matin"
echo ""
echo "📝 Pour activer HTTPS avec un domaine:"
echo "   1. Pointez votre domaine vers $PUBLIC_IP"
echo "   2. Exécutez: sudo certbot --nginx -d votre-domaine.com"
echo ""

