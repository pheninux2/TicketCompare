#!/bin/bash
# ===================================
# Script de Mise à Jour Rapide
# Pull nouvelle image et redémarrage
# ===================================

set -e

APP_NAME="${1:-app1}"
APP_DIR="/opt/shoptracker/$APP_NAME"
PROD_DIR="$APP_DIR/environments/prod"

if [ ! -d "$PROD_DIR" ]; then
    echo "[ERREUR] Application '$APP_NAME' non trouvée dans $APP_DIR"
    echo "Usage: $0 [app1|app2|app3]"
    exit 1
fi

cd "$PROD_DIR"

echo ""
echo "========================================="
echo "   Mise à jour de $APP_NAME            "
echo "========================================="
echo ""

# Charger l'image depuis .env
if [ -f ".env" ]; then
    export $(grep -v '^#' .env | grep DOCKER_IMAGE | xargs)
fi

IMAGE="${DOCKER_IMAGE:-ghcr.io/pheninux2/ticketcompare:latest}"

echo "[*] Image: $IMAGE"
echo ""

# Pull de la nouvelle image
echo "[*] Téléchargement de la nouvelle image..."
docker pull "$IMAGE"

# Redémarrer l'application
echo "[*] Redémarrage de l'application..."
docker compose -f docker-compose-pull.yml up -d --force-recreate

echo ""
echo "[*] Attente du démarrage (15 secondes)..."
sleep 15

# Vérifier le statut
echo ""
echo "[*] Vérification..."
docker compose -f docker-compose-pull.yml ps

echo ""
echo "[*] Logs récents:"
docker compose -f docker-compose-pull.yml logs --tail=30 app

echo ""
echo "========================================="
echo "   Mise à jour terminée !               "
echo "========================================="
echo ""

