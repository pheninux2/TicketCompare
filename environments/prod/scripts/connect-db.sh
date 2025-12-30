#!/bin/bash
# ==========================================
# Script de connexion PostgreSQL - Linux/WSL
# ShopTracker - environments/prod
# ==========================================

echo ""
echo "====================================="
echo "   Accès PostgreSQL - PRODUCTION    "
echo "====================================="
echo ""

# Vérification que le conteneur est actif
echo "[*] Vérification du conteneur PostgreSQL..."
CONTAINER=$(docker ps --filter "name=shoptracker-db" --format "{{.Names}}")

if [ -z "$CONTAINER" ]; then
    echo "[ERREUR] Le conteneur shoptracker-db n'est pas démarré!"
    echo ""
    echo "Démarrez d'abord l'application:"
    echo "  cd ../.."
    echo "  ./start-prod.ps1"
    echo ""
    exit 1
fi

echo "[OK] Conteneur actif: $CONTAINER"
echo ""

# Informations de connexion
echo "====================================="
echo "   Informations de Connexion        "
echo "====================================="
echo ""
echo "  Base de données : shoptracker"
echo "  Utilisateur     : shoptracker_admin"
echo "  Mot de passe    : ShopTracker2025!Secure"
echo "  Environnement   : PRODUCTION"
echo ""

echo "====================================="
echo "   Commandes psql Utiles            "
echo "====================================="
echo ""
echo "  \\dt              - Lister les tables"
echo "  \\d nom_table     - Décrire une table"
echo "  \\l               - Lister les bases de données"
echo "  \\du              - Lister les utilisateurs"
echo "  \\q               - Quitter"
echo ""
echo "  SELECT * FROM tickets LIMIT 10;    - Voir les tickets"
echo "  SELECT * FROM stores;              - Voir les magasins"
echo "  SELECT * FROM users;               - Voir les utilisateurs"
echo ""

echo "====================================="
echo "   Connexion en cours...             "
echo "====================================="
echo ""

# Connexion à PostgreSQL
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker

echo ""
echo "[OK] Déconnexion réussie!"
echo ""

