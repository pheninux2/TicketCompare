#!/bin/bash
# ===================================
# Script de démarrage Docker avec H2
# TicketCompare - Mode Test/Développement
# ===================================

echo ""
echo "========================================"
echo "   TicketCompare - Docker avec H2"
echo "========================================"
echo ""

# Vérifier si Docker est en cours d'exécution
echo "[CHECK] Vérification de Docker..."
if ! docker info > /dev/null 2>&1; then
    echo ""
    echo "[ERROR] Docker n'est pas en cours d'exécution!"
    echo ""
    echo "Solutions:"
    echo "  1. Démarrez Docker"
    echo "  2. Attendez que Docker soit complètement démarré"
    echo "  3. Relancez ce script"
    echo ""
    echo "Ou utilisez le mode développement local sans Docker:"
    echo "  ./scripts/start-dev.sh"
    echo ""
    exit 1
fi
echo "[OK] Docker est en cours d'exécution"
echo ""

# Naviguer vers le dossier docker
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../docker"

echo "[INFO] Démarrage avec base de données H2 (mémoire)..."
docker-compose -f docker-compose-h2.yml up -d

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Échec du démarrage de l'application!"
    echo ""
    echo "Consultez les logs avec: docker-compose -f docker-compose-h2.yml logs"
    echo ""
    exit 1
fi

echo ""
echo "[SUCCESS] Application démarrée avec succès!"
echo ""
echo "Accès à l'application:"
echo "  - Application Web: http://localhost:8080"
echo "  - Console H2: http://localhost:8080/h2-console"
echo ""
echo "Base de données H2 (mémoire):"
echo "  - JDBC URL: jdbc:h2:mem:ticketcomparedb"
echo "  - Username: sa"
echo "  - Password: (vide)"
echo ""
echo "Pour voir les logs en temps réel:"
echo "  docker-compose -f docker/docker-compose-h2.yml logs -f"
echo ""
echo "Pour arrêter:"
echo "  docker-compose -f docker/docker-compose-h2.yml down"
echo ""

