#!/bin/bash
# ==========================================
# Script de backup PostgreSQL - Linux/WSL
# ShopTracker - environments/prod
# ==========================================

FORMAT="${1:-dump}"  # dump ou sql

echo ""
echo "====================================="
echo "   Backup Base de Données           "
echo "   Environnement: PRODUCTION        "
echo "====================================="
echo ""

# Vérification que le conteneur est actif
echo "[*] Vérification du conteneur PostgreSQL..."
CONTAINER=$(docker ps --filter "name=shoptracker-db" --format "{{.Names}}")

if [ -z "$CONTAINER" ]; then
    echo "[ERREUR] Le conteneur shoptracker-db n'est pas démarré!"
    echo ""
    echo "Démarrez d'abord l'application"
    echo ""
    exit 1
fi

echo "[OK] Conteneur actif: $CONTAINER"
echo ""

# Créer le dossier backups s'il n'existe pas
BACKUP_DIR="../backups"
mkdir -p "$BACKUP_DIR"

# Générer le nom du fichier avec horodatage
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
HOSTNAME=$(hostname)

if [ "$FORMAT" = "sql" ]; then
    FILENAME="backup_${HOSTNAME}_${TIMESTAMP}.sql"
else
    FILENAME="backup_${HOSTNAME}_${TIMESTAMP}.dump"
fi

BACKUP_PATH="$BACKUP_DIR/$FILENAME"

echo "====================================="
echo "   Configuration du Backup           "
echo "====================================="
echo ""
echo "Format        : $FORMAT"
echo "Fichier       : $FILENAME"
echo "Destination   : $BACKUP_PATH"
echo ""

# Confirmation
read -p "Continuer avec le backup? (O/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Nn]$ ]]; then
    echo ""
    echo "[INFO] Backup annulé par l'utilisateur."
    echo ""
    exit 0
fi

echo ""
echo "====================================="
echo "   Backup en Cours...                "
echo "====================================="
echo ""

# Exécution du backup
START_TIME=$(date +%s)

if [ "$FORMAT" = "sql" ]; then
    # Backup au format SQL
    echo "[*] Création du backup SQL..."
    docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker > "$BACKUP_PATH"
else
    # Backup au format custom
    echo "[*] Création du backup au format custom..."
    docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c -b -v -f "/backups/$FILENAME"
fi

if [ $? -eq 0 ]; then
    END_TIME=$(date +%s)
    DURATION=$((END_TIME - START_TIME))

    # Vérifier que le fichier existe
    if [ -f "$BACKUP_PATH" ]; then
        FILE_SIZE=$(stat -f%z "$BACKUP_PATH" 2>/dev/null || stat -c%s "$BACKUP_PATH" 2>/dev/null)
        FILE_SIZE_MB=$(echo "scale=2; $FILE_SIZE / 1024 / 1024" | bc)

        echo ""
        echo "====================================="
        echo "   Backup Réussi!                    "
        echo "====================================="
        echo ""
        echo "Fichier       : $FILENAME"
        echo "Taille        : $FILE_SIZE_MB MB"
        echo "Durée         : $DURATION secondes"
        echo "Emplacement   : $BACKUP_PATH"
        echo ""

        # Lister les backups existants
        echo "====================================="
        echo "   Backups Existants                 "
        echo "====================================="
        echo ""
        ls -lh "$BACKUP_DIR"/backup_* 2>/dev/null | tail -5
        echo ""

        echo "[OK] Backup terminé avec succès!"
    else
        echo "[ERREUR] Le fichier de backup n'a pas été créé"
        exit 1
    fi
else
    echo "[ERREUR] Échec du backup!"
    exit 1
fi

echo ""

