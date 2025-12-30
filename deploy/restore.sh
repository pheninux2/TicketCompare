#!/bin/bash
# ==========================================
# Script de Restauration - ShopTracker
# VPS DigitalOcean
# ==========================================

set -e

BACKUP_DIR="/opt/shoptracker/backups"

echo ""
echo "========================================="
echo "   ShopTracker - Restauration           "
echo "========================================="
echo ""

# Si un fichier est passé en paramètre
if [ -n "$1" ]; then
    BACKUP_FILE="$1"
else
    # Lister les backups disponibles
    echo "Backups disponibles:"
    echo ""

    BACKUPS=($(ls -t "$BACKUP_DIR"/backup_*.dump 2>/dev/null))

    if [ ${#BACKUPS[@]} -eq 0 ]; then
        echo "[ERREUR] Aucun backup trouvé dans $BACKUP_DIR"
        exit 1
    fi

    for i in "${!BACKUPS[@]}"; do
        SIZE=$(du -h "${BACKUPS[$i]}" | cut -f1)
        DATE=$(stat -c %y "${BACKUPS[$i]}" | cut -d' ' -f1,2 | cut -d'.' -f1)
        echo "  $((i+1)). $(basename "${BACKUPS[$i]}") - $SIZE - $DATE"
    done

    echo ""
    echo "Sélectionnez un backup (1-${#BACKUPS[@]}) ou 0 pour annuler:"
    read -r CHOICE

    if [ "$CHOICE" -eq 0 ] || [ -z "$CHOICE" ]; then
        echo "[INFO] Restauration annulée"
        exit 0
    fi

    INDEX=$((CHOICE - 1))
    if [ $INDEX -lt 0 ] || [ $INDEX -ge ${#BACKUPS[@]} ]; then
        echo "[ERREUR] Choix invalide"
        exit 1
    fi

    BACKUP_FILE="${BACKUPS[$INDEX]}"
fi

# Vérifier que le fichier existe
if [ ! -f "$BACKUP_FILE" ]; then
    echo "[ERREUR] Fichier non trouvé: $BACKUP_FILE"
    exit 1
fi

SIZE=$(du -h "$BACKUP_FILE" | cut -f1)

echo ""
echo "========================================="
echo "   Configuration de la Restauration     "
echo "========================================="
echo ""
echo "Fichier: $(basename "$BACKUP_FILE")"
echo "Taille: $SIZE"
echo ""

# AVERTISSEMENT
echo "========================================="
echo "   ⚠️  AVERTISSEMENT  ⚠️                "
echo "========================================="
echo ""
echo "Cette opération va:"
echo "  - SUPPRIMER toutes les données actuelles"
echo "  - Restaurer les données du backup"
echo "  - Cette action est IRREVERSIBLE"
echo ""
echo "Voulez-vous créer un backup de sécurité avant? (O/n)"
read -r SAFETY_BACKUP

if [ "$SAFETY_BACKUP" != "n" ] && [ "$SAFETY_BACKUP" != "N" ]; then
    echo ""
    echo "[*] Création d'un backup de sécurité..."
    SAFETY_FILE="$BACKUP_DIR/safety_backup_$(date +%Y%m%d_%H%M%S).dump"
    docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c -b > "$SAFETY_FILE" 2>/dev/null

    if [ $? -eq 0 ]; then
        echo "[OK] Backup de sécurité créé: $(basename "$SAFETY_FILE")"
    else
        echo "[AVERTISSEMENT] Impossible de créer le backup de sécurité"
    fi
fi

echo ""
echo "Tapez 'RESTAURER' pour confirmer:"
read -r CONFIRMATION

if [ "$CONFIRMATION" != "RESTAURER" ]; then
    echo "[INFO] Restauration annulée"
    exit 0
fi

echo ""
echo "========================================="
echo "   Restauration en Cours...             "
echo "========================================="
echo ""

# Arrêter l'application
echo "[*] Arrêt de l'application..."
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml stop app

# Copier le fichier dans le conteneur si nécessaire
BACKUP_FILENAME=$(basename "$BACKUP_FILE")
docker cp "$BACKUP_FILE" shoptracker-db:/tmp/"$BACKUP_FILENAME"

# Restaurer
echo "[*] Restauration de la base de données..."
docker exec shoptracker-db pg_restore -U shoptracker_admin -d shoptracker -v -c /tmp/"$BACKUP_FILENAME" 2>/dev/null

if [ $? -eq 0 ]; then
    echo ""
    echo "[OK] Restauration réussie !"

    # Redémarrer l'application
    echo "[*] Redémarrage de l'application..."
    docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml start app

    # Vérifier
    echo "[*] Vérification de la restauration..."
    sleep 5

    TICKET_COUNT=$(docker exec shoptracker-db psql -U shoptracker_admin -d shoptracker -t -c "SELECT COUNT(*) FROM tickets;" 2>/dev/null)
    STORE_COUNT=$(docker exec shoptracker-db psql -U shoptracker_admin -d shoptracker -t -c "SELECT COUNT(*) FROM stores;" 2>/dev/null)
    USER_COUNT=$(docker exec shoptracker-db psql -U shoptracker_admin -d shoptracker -t -c "SELECT COUNT(*) FROM users;" 2>/dev/null)

    echo ""
    echo "Données restaurées:"
    echo "  - Tickets: $TICKET_COUNT"
    echo "  - Magasins: $STORE_COUNT"
    echo "  - Utilisateurs: $USER_COUNT"
    echo ""

    echo "========================================="
    echo "   Restauration Terminée ! ✅           "
    echo "========================================="
    echo ""
else
    echo ""
    echo "[ERREUR] Échec de la restauration"

    if [ -n "$SAFETY_FILE" ]; then
        echo "[INFO] Un backup de sécurité a été créé: $(basename "$SAFETY_FILE")"
    fi

    # Redémarrer quand même
    docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml start app

    exit 1
fi

