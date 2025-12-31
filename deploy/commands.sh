#!/bin/bash
# ===================================
# Commandes Utiles - Multi-App VPS
# Aide-mémoire pour la gestion
# ===================================

show_help() {
    echo ""
    echo "========================================="
    echo "   Commandes Utiles - ShopTracker       "
    echo "========================================="
    echo ""
    echo "Usage: ./commands.sh [commande] [app]"
    echo ""
    echo "Commandes disponibles:"
    echo ""
    echo "  status [app]         - Voir le statut des conteneurs"
    echo "  logs [app]           - Voir les logs en temps réel"
    echo "  restart [app]        - Redémarrer une application"
    echo "  stop [app]           - Arrêter une application"
    echo "  start [app]          - Démarrer une application"
    echo "  update [app]         - Mettre à jour une application"
    echo "  backup [app]         - Sauvegarder la base de données"
    echo "  shell [app]          - Accéder au shell du conteneur"
    echo "  db [app]             - Accéder à PostgreSQL"
    echo "  clean                - Nettoyer Docker"
    echo "  nginx                - Gérer Nginx"
    echo "  monitor              - Monitorer les ressources"
    echo ""
    echo "Exemples:"
    echo "  ./commands.sh status app1"
    echo "  ./commands.sh logs app2"
    echo "  ./commands.sh update app1"
    echo ""
}

# Variables
APP=${2:-app1}
APP_DIR="/opt/shoptracker/$APP/environments/prod"

case "$1" in
    status)
        echo "[*] Statut de $APP..."
        cd "$APP_DIR"
        docker compose -f docker-compose-pull.yml ps
        ;;

    logs)
        echo "[*] Logs de $APP (Ctrl+C pour quitter)..."
        cd "$APP_DIR"
        docker compose -f docker-compose-pull.yml logs -f app
        ;;

    restart)
        echo "[*] Redémarrage de $APP..."
        cd "$APP_DIR"
        docker compose -f docker-compose-pull.yml restart app
        echo "[OK] Application redémarrée"
        ;;

    stop)
        echo "[*] Arrêt de $APP..."
        cd "$APP_DIR"
        docker compose -f docker-compose-pull.yml stop
        echo "[OK] Application arrêtée"
        ;;

    start)
        echo "[*] Démarrage de $APP..."
        cd "$APP_DIR"
        docker compose -f docker-compose-pull.yml up -d
        echo "[OK] Application démarrée"
        ;;

    update)
        echo "[*] Mise à jour de $APP..."
        if [ -f "$APP_DIR/../../update.sh" ]; then
            cd "/opt/shoptracker/$APP"
            ./update.sh "$APP"
        else
            echo "[*] Téléchargement du script de mise à jour..."
            curl -o /tmp/update.sh https://raw.githubusercontent.com/pheninux2/TicketCompare/main/deploy/update-image.sh
            chmod +x /tmp/update.sh
            /tmp/update.sh "$APP"
        fi
        ;;

    backup)
        echo "[*] Sauvegarde de la base de données de $APP..."
        cd "$APP_DIR"
        BACKUP_FILE="backups/backup_$(date +%Y%m%d_%H%M%S).sql"
        docker exec shoptracker-${APP}-db pg_dump -U shoptracker_admin shoptracker > "$BACKUP_FILE"
        echo "[OK] Sauvegarde créée: $BACKUP_FILE"
        ;;

    shell)
        echo "[*] Accès au shell du conteneur $APP..."
        docker exec -it shoptracker-${APP} /bin/bash
        ;;

    db)
        echo "[*] Accès à PostgreSQL de $APP..."
        docker exec -it shoptracker-${APP}-db psql -U shoptracker_admin -d shoptracker
        ;;

    clean)
        echo "[*] Nettoyage Docker..."
        echo "[ATTENTION] Ceci va supprimer les images et conteneurs inutilisés"
        read -p "Continuer? (y/N) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            docker system prune -a
            echo "[OK] Nettoyage terminé"
        fi
        ;;

    nginx)
        echo ""
        echo "Commandes Nginx:"
        echo "  nginx -t                  # Tester la config"
        echo "  systemctl reload nginx    # Recharger"
        echo "  systemctl status nginx    # Statut"
        echo "  tail -f /var/log/nginx/multi_app_error.log  # Logs d'erreur"
        echo "  tail -f /var/log/nginx/multi_app_access.log # Logs d'accès"
        echo ""
        ;;

    monitor)
        echo "[*] Monitoring des ressources..."
        echo ""
        echo "=== Utilisation Disque ==="
        df -h | grep -E 'Filesystem|/dev/vda1|/dev/sda1'
        echo ""
        echo "=== Utilisation Mémoire ==="
        free -h
        echo ""
        echo "=== Conteneurs Docker ==="
        docker stats --no-stream
        echo ""
        echo "=== Espace Docker ==="
        docker system df
        echo ""
        ;;

    *)
        show_help
        ;;
esac

