#!/bin/bash
# Test d'accès à la Console H2

echo "========================================"
echo "  Test d'accès Console H2"
echo "========================================"
echo ""

# Test 1: Application répond
echo "[1/3] Test Health Check..."
if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
    echo "      ✓ Application UP"
else
    echo "      ✗ Application DOWN"
    echo ""
    echo "L'application n'est pas démarrée. Lancez :"
    echo "  docker compose -f docker-compose-h2.yml up -d"
    exit 1
fi

# Test 2: Console H2 accessible
echo "[2/3] Test Console H2..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/h2-console)
if [ "$HTTP_CODE" = "200" ]; then
    echo "      ✓ Console H2 accessible (HTTP $HTTP_CODE)"
elif [ "$HTTP_CODE" = "404" ]; then
    echo "      ✗ Console H2 non trouvée (HTTP 404)"
    echo ""
    echo "Solutions :"
    echo "  1. Rebuild : docker compose -f docker-compose-h2.yml build --no-cache"
    echo "  2. Redémarrer : docker compose -f docker-compose-h2.yml restart"
    echo "  3. Voir logs : docker compose -f docker-compose-h2.yml logs app | grep h2"
    exit 1
else
    echo "      ? Code HTTP: $HTTP_CODE"
fi

# Test 3: Vérifier les logs
echo "[3/3] Vérification logs H2..."
if docker compose -f docker-compose-h2.yml logs app 2>&1 | grep -qi "h2 console"; then
    echo "      ✓ H2 console mentionnée dans les logs"
else
    echo "      ⚠ H2 console non trouvée dans les logs"
fi

echo ""
echo "========================================"
echo "  ✅ RÉSULTAT"
echo "========================================"
echo ""
echo "Console H2 accessible à :"
echo "  http://localhost:8080/h2-console"
echo ""
echo "Paramètres de connexion :"
echo "  JDBC URL : jdbc:h2:mem:ticketcomparedb"
echo "  Username : sa"
echo "  Password : (vide)"
echo ""

