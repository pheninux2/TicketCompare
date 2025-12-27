#!/bin/bash
# ===================================
# Script d'accès rapide à la Console H2
# TicketCompare
# ===================================

echo ""
echo "========================================"
echo "   Console H2 - TicketCompare"
echo "========================================"
echo ""

# Vérifier si le conteneur est en cours d'exécution
if ! docker ps | grep -q "ticketcompare-app-h2"; then
    echo "[ERROR] Le conteneur ticketcompare-app-h2 n'est pas en cours d'exécution"
    echo ""
    echo "Démarrez-le d'abord avec :"
    echo "  cd docker && docker compose -f docker-compose-h2.yml up -d"
    echo ""
    exit 1
fi

echo "[OK] Application en cours d'exécution"
echo ""

# Attendre que l'application soit prête
echo "[INFO] Vérification que l'application est prête..."
sleep 5

# Tester si l'application répond
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "[OK] Application prête!"
else
    echo "[WARNING] Application pas encore prête, patientez quelques secondes..."
fi

echo ""
echo "========================================"
echo "   Informations de Connexion H2"
echo "========================================"
echo ""
echo "URL Console : http://localhost:8080/h2-console"
echo ""
echo "Paramètres de connexion :"
echo "  JDBC URL  : jdbc:h2:mem:ticketcomparedb"
echo "  User Name : sa"
echo "  Password  : (vide)"
echo ""

# Essayer d'ouvrir le navigateur automatiquement
H2_URL="http://localhost:8080/h2-console"

echo "[INFO] Tentative d'ouverture automatique du navigateur..."
echo ""

if command -v xdg-open > /dev/null 2>&1; then
    xdg-open "$H2_URL" 2>/dev/null &
    echo "[OK] Navigateur ouvert (xdg-open)"
elif command -v open > /dev/null 2>&1; then
    open "$H2_URL" 2>/dev/null &
    echo "[OK] Navigateur ouvert (macOS)"
else
    echo "[INFO] Ouverture automatique non disponible"
    echo ""
    echo "Ouvrez manuellement cette URL dans votre navigateur :"
    echo "  $H2_URL"
fi

echo ""
echo "========================================"
echo "   Commandes SQL Utiles"
echo "========================================"
echo ""
echo "-- Voir toutes les tables"
echo "SHOW TABLES;"
echo ""
echo "-- Voir tous les tickets"
echo "SELECT * FROM TICKETS;"
echo ""
echo "-- Voir tous les produits"
echo "SELECT * FROM PRODUCTS;"
echo ""
echo "-- Statistiques"
echo "SELECT COUNT(*) FROM TICKETS;"
echo ""

echo "Appuyez sur CTRL+C pour quitter..."
echo ""

# Garder le script ouvert pour afficher les informations
read -r

