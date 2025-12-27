#!/bin/bash
# ===================================
# Script de démarrage - Mode Développement
# TicketCompare (Sans Docker - H2 Database)
# ===================================

echo ""
echo "========================================"
echo "   TicketCompare - Mode Développement"
echo "========================================"
echo ""

echo "[INFO] Démarrage de l'application en mode développement..."
echo "[INFO] Base de données: H2 (mémoire)"
echo "[INFO] Console H2 disponible sur: http://localhost:8080/h2-console"
echo ""

# Naviguer vers le dossier racine
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

# Vérifier si Maven Wrapper existe
if [ -f ./mvnw ]; then
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=default
else
    # Utiliser Maven global
    mvn spring-boot:run -Dspring-boot.run.profiles=default
fi

