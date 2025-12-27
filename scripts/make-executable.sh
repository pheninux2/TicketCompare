#!/bin/bash
# ===================================
# Script pour rendre tous les scripts exécutables
# TicketCompare
# ===================================

echo ""
echo "========================================"
echo "   Rendre les scripts exécutables"
echo "========================================"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "[INFO] Ajout des permissions d'exécution..."

chmod +x "$SCRIPT_DIR/start-docker.sh"
chmod +x "$SCRIPT_DIR/start-docker-h2.sh"
chmod +x "$SCRIPT_DIR/stop-docker.sh"
chmod +x "$SCRIPT_DIR/rebuild-docker.sh"
chmod +x "$SCRIPT_DIR/start-dev.sh"
chmod +x "$SCRIPT_DIR/logs.sh"
chmod +x "$SCRIPT_DIR/verify.sh"
chmod +x "$SCRIPT_DIR/test-tesseract.sh"
chmod +x "$SCRIPT_DIR/restart-fixed.sh"
chmod +x "$SCRIPT_DIR/open-h2-console.sh"
chmod +x "$SCRIPT_DIR/test-h2-access.sh"
chmod +x "$SCRIPT_DIR/start-and-test.sh"

echo "[SUCCESS] Tous les scripts sont maintenant exécutables!"
echo ""
echo "Vous pouvez maintenant exécuter:"
echo "  ./scripts/start-docker.sh          # Démarrer avec PostgreSQL"
echo "  ./scripts/start-docker-h2.sh       # Démarrer avec H2"
echo "  ./scripts/start-dev.sh             # Mode développement"
echo "  ./scripts/stop-docker.sh           # Arrêter Docker"
echo "  ./scripts/rebuild-docker.sh        # Reconstruire"
echo "  ./scripts/logs.sh                  # Voir les logs"
echo "  ./scripts/verify.sh                # Vérifier l'installation"
echo "  ./scripts/test-tesseract.sh        # Tester Tesseract OCR"
echo "  ./scripts/open-h2-console.sh       # Ouvrir console H2"
echo "  ./scripts/test-h2-access.sh        # Tester accès H2"
echo "  ./scripts/start-and-test.sh        # Démarrer + tester"
echo ""

