#!/bin/bash
# ===================================
# Script de reconstruction Docker
# TicketCompare
# ===================================

echo ""
echo "========================================"
echo "   TicketCompare - Rebuild Docker"
echo "========================================"
echo ""

# Naviguer vers le dossier docker
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../docker"

echo "[INFO] Arrêt et suppression des conteneurs..."
docker-compose down

echo ""
echo "[INFO] Reconstruction de l'image Docker (sans cache)..."
docker-compose build --no-cache

echo ""
echo "[INFO] Redémarrage des conteneurs..."
docker-compose up -d

echo ""
echo "[SUCCESS] Application reconstruite et démarrée avec succès!"
echo ""

