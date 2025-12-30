#!/bin/bash
# ==========================================
# Script de Backup - ShopTracker
# VPS DigitalOcean
# ==========================================

set -e

BACKUP_DIR="/opt/shoptracker/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/backup_$DATE.dump"

echo ""
echo "========================================="
echo "   ShopTracker - Backup                 "
echo "========================================="
echo ""

# Vérifier que PostgreSQL est actif
if ! docker exec shoptracker-db pg_isready -U shoptracker_admin > /dev/null 2>&1; then
    echo "[ERREUR] PostgreSQL n'est pas accessible"
    exit 1
fi

echo "[*] Création du backup..."
echo "[INFO] Fichier: $BACKUP_FILE"

# Créer le backup
docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c -b -v > "$BACKUP_FILE" 2>/dev/null

if [ $? -eq 0 ]; then
    # Taille du fichier
    SIZE=$(du -h "$BACKUP_FILE" | cut -f1)

    echo ""
    echo "[OK] Backup créé avec succès !"
    echo "     Fichier: $BACKUP_FILE"
    echo "     Taille: $SIZE"
    echo ""

    # Lister les 5 derniers backups
    echo "Derniers backups:"
    ls -lth "$BACKUP_DIR"/backup_*.dump | head -5
    echo ""

    # Proposer de supprimer les anciens backups
    OLD_BACKUPS=$(find "$BACKUP_DIR" -name "backup_*.dump" -mtime +30 | wc -l)
    if [ "$OLD_BACKUPS" -gt 0 ]; then
        echo "[INFO] $OLD_BACKUPS backup(s) de plus de 30 jours trouvé(s)"
        echo "Voulez-vous les supprimer? (o/N)"
        read -r RESPONSE

        if [ "$RESPONSE" = "o" ] || [ "$RESPONSE" = "O" ]; then
            find "$BACKUP_DIR" -name "backup_*.dump" -mtime +30 -delete
            echo "[OK] Anciens backups supprimés"
        fi
    fi
else
    echo "[ERREUR] Échec de la création du backup"
    exit 1
fi

echo ""
echo "Pour restaurer ce backup:"
echo "  ./restore-backup.sh $BACKUP_FILE"
echo ""

