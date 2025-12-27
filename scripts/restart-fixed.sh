#!/bin/bash
# ===================================
# Script de redémarrage après correction
# TicketCompare - Fix SQL Error
# ===================================

echo ""
echo "========================================"
echo "   Redémarrage avec Corrections"
echo "========================================"
echo ""

# Naviguer vers le dossier docker
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../docker"

echo "[INFO] Arrêt du conteneur existant..."
docker compose -f docker-compose-h2.yml down

echo ""
echo "[INFO] Reconstruction de l'image (avec corrections SQL)..."
docker compose -f docker-compose-h2.yml build --no-cache

echo ""
echo "[INFO] Démarrage de l'application..."
docker compose -f docker-compose-h2.yml up -d

echo ""
echo "[INFO] Attente du démarrage..."
sleep 10

echo ""
echo "========================================"
echo "   Vérification des logs"
echo "========================================"
docker compose -f docker-compose-h2.yml logs --tail=50

echo ""
echo "========================================"
echo "   Statut"
echo "========================================"
docker compose -f docker-compose-h2.yml ps

echo ""
echo "Si aucune erreur SQL n'apparait, l'application est prête!"
echo ""
echo "Accès :"
echo "  - Application : http://localhost:8080"
echo "  - Console H2 : http://localhost:8080/h2-console"
echo ""
echo "Pour voir les logs en continu :"
echo "  docker compose -f docker/docker-compose-h2.yml logs -f"
echo ""

