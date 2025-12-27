#!/bin/bash
# ===================================
# Script de test Tesseract dans Docker
# TicketCompare
# ===================================

echo ""
echo "========================================"
echo "   Test Tesseract OCR dans Docker"
echo "========================================"
echo ""

CONTAINER_NAME="ticketcompare-app-h2"

# Vérifier si le conteneur existe et est en cours d'exécution
if ! docker ps | grep -q "$CONTAINER_NAME"; then
    echo "[ERROR] Le conteneur $CONTAINER_NAME n'est pas en cours d'exécution"
    echo ""
    echo "Démarrez-le d'abord avec:"
    echo "  docker compose -f docker/docker-compose-h2.yml up -d"
    echo ""
    exit 1
fi

echo "[INFO] Connexion au conteneur $CONTAINER_NAME..."
echo ""

echo "========================================"
echo "   1. Version de Tesseract"
echo "========================================"
docker exec $CONTAINER_NAME tesseract --version
echo ""

echo "========================================"
echo "   2. Langues disponibles"
echo "========================================"
docker exec $CONTAINER_NAME tesseract --list-langs
echo ""

echo "========================================"
echo "   3. Variable TESSDATA_PREFIX"
echo "========================================"
docker exec $CONTAINER_NAME sh -c 'echo $TESSDATA_PREFIX'
echo ""

echo "========================================"
echo "   4. Fichiers de données Tesseract"
echo "========================================"
docker exec $CONTAINER_NAME ls -lh /usr/share/tessdata/
echo ""

echo "========================================"
echo "   5. Vérification des fichiers de langue"
echo "========================================"
echo -n "fra.traineddata: "
docker exec $CONTAINER_NAME test -f /usr/share/tessdata/fra.traineddata && echo "✓ Présent" || echo "✗ Absent"

echo -n "eng.traineddata: "
docker exec $CONTAINER_NAME test -f /usr/share/tessdata/eng.traineddata && echo "✓ Présent" || echo "✗ Absent"
echo ""

echo "========================================"
echo "   6. Logs Java (Tesseract config)"
echo "========================================"
docker logs $CONTAINER_NAME 2>&1 | grep -i "tesseract" | tail -20
echo ""

echo "========================================"
echo "   Résumé"
echo "========================================"
echo ""
echo "Si tous les tests passent, Tesseract devrait fonctionner!"
echo ""
echo "Pour tester l'OCR:"
echo "  1. Accédez à http://localhost:8080"
echo "  2. Allez dans 'Scanner un ticket'"
echo "  3. Uploadez une image de ticket"
echo ""

