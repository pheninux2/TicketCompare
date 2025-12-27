.#!/bin/bash
# ===================================
# Script de démarrage Docker Compose
# TicketCompare - Mode Production
# ===================================

echo ""
echo "========================================"
echo "   TicketCompare - Démarrage Docker"
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

# Vérifier si .env existe, sinon copier depuis .env.example
if [ ! -f .env ]; then
    echo "[INFO] Fichier .env non trouvé. Création depuis .env.example..."
    cp .env.example .env
    echo "[ATTENTION] Veuillez modifier le fichier docker/.env avec vos paramètres!"
    echo ""
    read -p "Appuyez sur Entrée pour continuer..."
fi

echo "[INFO] Démarrage des conteneurs Docker..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Échec du démarrage de l'application!"
    echo ""
    echo "Consultez les logs avec: docker-compose logs"
    echo ""
    exit 1
fi

echo ""
echo "[SUCCESS] Application démarrée avec succès!"
echo ""
echo "Accès à l'application:"
echo "  - Application Web: http://localhost:8080"
echo "  - Base de données: localhost:5432"
echo ""
echo "Pour voir les logs en temps réel:"
echo "  docker-compose -f docker/docker-compose.yml logs -f"
echo ""
echo "Pour arrêter l'application:"
echo "  docker-compose -f docker/docker-compose.yml down"
echo ""
echo "Pour activer pgAdmin (interface de gestion):"
echo "  docker-compose --profile admin up -d"
echo "  puis accédez à http://localhost:5050"
echo ""

