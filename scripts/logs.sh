#!/bin/bash
# ===================================
# Script de visualisation des logs
# TicketCompare
# ===================================

echo ""
echo "========================================"
echo "   TicketCompare - Logs Docker"
echo "========================================"
echo ""

# Naviguer vers le dossier docker
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../docker"

echo "[INFO] Affichage des logs (CTRL+C pour quitter)..."
echo ""

docker-compose logs -f --tail=100

