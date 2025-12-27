#!/bin/bash
# ===================================
# Script d'arrêt Docker Compose
# TicketCompare
# ===================================

echo ""
echo "========================================"
echo "   TicketCompare - Arrêt Docker"
echo "========================================"
echo ""

# Naviguer vers le dossier docker
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../docker"

echo "[INFO] Arrêt des conteneurs Docker..."
docker-compose down

echo ""
echo "[SUCCESS] Application arrêtée avec succès!"
echo ""

