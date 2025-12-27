#!/bin/bash
# ===================================
# Démarrage complet avec vérifications
# TicketCompare - Tesseract fixé
# ===================================

echo ""
echo "========================================"
echo "   TicketCompare - Démarrage Complet"
echo "========================================"
echo ""

cd "$(dirname "$0")/../docker"

echo "[INFO] Vérification de Docker..."
if ! docker info > /dev/null 2>&1; then
    echo "[ERROR] Docker n'est pas en cours d'exécution!"
    exit 1
fi
echo "[OK] Docker est actif"
echo ""

echo "[INFO] Arrêt des conteneurs existants..."
docker compose -f docker-compose-h2.yml down
echo ""

echo "[INFO] Vérification de l'image..."
if ! docker images | grep -q "docker-app"; then
    echo "[INFO] Image non trouvée, build en cours..."
    docker compose -f docker-compose-h2.yml build --no-cache
fi
echo ""

echo "[INFO] Démarrage de l'application..."
docker compose -f docker-compose-h2.yml up -d
echo ""

echo "[INFO] Attente du démarrage (30 secondes)..."
sleep 30
echo ""

echo "========================================"
echo "   Vérification Tesseract"
echo "========================================"
echo ""

CONTAINER_NAME="ticketcompare-app-h2"

if docker ps | grep -q "$CONTAINER_NAME"; then
    echo "[TEST] Version Tesseract:"
    docker exec $CONTAINER_NAME tesseract --version 2>&1 | head -n 1

    echo ""
    echo "[TEST] Langues disponibles:"
    docker exec $CONTAINER_NAME tesseract --list-langs 2>&1 | grep -E "(fra|eng)"

    echo ""
    echo "[TEST] TESSDATA_PREFIX:"
    docker exec $CONTAINER_NAME sh -c 'echo $TESSDATA_PREFIX'

    echo ""
    echo "[TEST] Fichiers de langue:"
    docker exec $CONTAINER_NAME test -f /usr/share/tessdata/fra.traineddata && echo "  ✓ fra.traineddata" || echo "  ✗ fra.traineddata manquant!"
    docker exec $CONTAINER_NAME test -f /usr/share/tessdata/eng.traineddata && echo "  ✓ eng.traineddata" || echo "  ✗ eng.traineddata manquant!"

    echo ""
    echo "========================================"
    echo "   Logs de Démarrage (Tesseract)"
    echo "========================================"
    docker logs $CONTAINER_NAME 2>&1 | grep -i "tesseract" | tail -10

    echo ""
    echo "========================================"
    echo "   Statut Final"
    echo "========================================"
    docker compose -f docker-compose-h2.yml ps

    echo ""
    echo "✅ Application démarrée!"
    echo ""
    echo "📊 Accès:"
    echo "   - Application: http://localhost:8080"
    echo "   - Console H2: http://localhost:8080/h2-console"
    echo ""
    echo "🧪 Pour tester l'OCR:"
    echo "   1. Allez sur http://localhost:8080"
    echo "   2. Menu 'Scanner un ticket'"
    echo "   3. Uploadez une image"
    echo ""
    echo "📋 Logs en direct:"
    echo "   docker compose -f docker/docker-compose-h2.yml logs -f"
    echo ""
else
    echo "[ERROR] Le conteneur n'a pas démarré correctement!"
    echo ""
    echo "Voir les logs:"
    docker compose -f docker-compose-h2.yml logs
fi

