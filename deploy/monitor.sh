#!/bin/bash
# ==========================================
# Script de Monitoring - ShopTracker
# VPS DigitalOcean
# ==========================================

APP_DIR="/opt/shoptracker/app"

echo ""
echo "========================================="
echo "   ShopTracker - Monitoring             "
echo "========================================="
echo ""

# Informations système
echo "📊 Système:"
echo "   Uptime: $(uptime -p)"
echo "   Load: $(uptime | awk -F'load average:' '{print $2}')"
echo ""

# CPU et RAM
echo "💻 Ressources:"
echo "   CPU Usage:"
top -bn1 | grep "Cpu(s)" | sed "s/.*, *\([0-9.]*\)%* id.*/\1/" | awk '{print "      Idle: " $1 "% | Used: " (100 - $1) "%"}'

echo "   Memory Usage:"
free -h | awk 'NR==2{printf "      Total: %s | Used: %s (%.2f%%) | Free: %s\n", $2,$3,$3*100/$2,$4}'

echo "   Disk Usage:"
df -h / | awk 'NR==2{printf "      Total: %s | Used: %s (%s) | Free: %s\n", $2,$3,$5,$4}'
echo ""

# Docker
echo "🐳 Docker:"
if command -v docker &> /dev/null; then
    echo "   Conteneurs actifs:"
    docker ps --format "      - {{.Names}}: {{.Status}}"
    echo ""

    # Stats des conteneurs
    echo "   Ressources des conteneurs:"
    docker stats --no-stream --format "      - {{.Name}}: CPU {{.CPUPerc}} | RAM {{.MemUsage}}"
else
    echo "   Docker non installé"
fi
echo ""

# Services
echo "🔧 Services:"

# PostgreSQL
if docker exec shoptracker-db pg_isready -U shoptracker_admin > /dev/null 2>&1; then
    echo "   ✅ PostgreSQL: Opérationnel"

    # Taille de la BDD
    DB_SIZE=$(docker exec shoptracker-db psql -U shoptracker_admin -d shoptracker -t -c "SELECT pg_size_pretty(pg_database_size('shoptracker'));" 2>/dev/null | xargs)
    echo "      Taille BDD: $DB_SIZE"

    # Nombre de connexions
    CONN_COUNT=$(docker exec shoptracker-db psql -U shoptracker_admin -d shoptracker -t -c "SELECT count(*) FROM pg_stat_activity WHERE datname='shoptracker';" 2>/dev/null | xargs)
    echo "      Connexions actives: $CONN_COUNT"
else
    echo "   ❌ PostgreSQL: Hors ligne"
fi

# Application Spring Boot
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "   ✅ Application: Opérationnelle"

    # Health check détaillé
    HEALTH=$(curl -s http://localhost:8080/actuator/health 2>/dev/null)
    if command -v jq &> /dev/null; then
        echo "$HEALTH" | jq '.'
    fi
else
    echo "   ❌ Application: Hors ligne"
fi

# Nginx
if systemctl is-active --quiet nginx; then
    echo "   ✅ Nginx: Opérationnel"

    # Connexions Nginx
    NGINX_CONN=$(netstat -an | grep :80 | wc -l)
    echo "      Connexions actives: $NGINX_CONN"
else
    echo "   ❌ Nginx: Hors ligne"
fi
echo ""

# Logs récents
echo "📝 Logs récents (dernières erreurs):"

# Logs application
echo "   Application (dernières 5 erreurs):"
docker logs shoptracker-app 2>&1 | grep -i "error" | tail -5 | sed 's/^/      /'

# Logs Nginx
if [ -f "/opt/shoptracker/logs/nginx/error.log" ]; then
    echo "   Nginx (dernières 5 erreurs):"
    tail -5 /opt/shoptracker/logs/nginx/error.log | sed 's/^/      /'
fi
echo ""

# Backups
echo "💾 Backups:"
BACKUP_COUNT=$(ls /opt/shoptracker/backups/backup_*.dump 2>/dev/null | wc -l)
echo "   Nombre de backups: $BACKUP_COUNT"

if [ $BACKUP_COUNT -gt 0 ]; then
    LAST_BACKUP=$(ls -t /opt/shoptracker/backups/backup_*.dump 2>/dev/null | head -1)
    LAST_BACKUP_DATE=$(stat -c %y "$LAST_BACKUP" | cut -d' ' -f1,2 | cut -d'.' -f1)
    LAST_BACKUP_SIZE=$(du -h "$LAST_BACKUP" | cut -f1)
    echo "   Dernier backup: $LAST_BACKUP_DATE ($LAST_BACKUP_SIZE)"
fi
echo ""

# Réseau
echo "🌐 Réseau:"
PUBLIC_IP=$(curl -s ifconfig.me 2>/dev/null || echo "N/A")
echo "   IP Publique: $PUBLIC_IP"
echo "   Application: http://$PUBLIC_IP"
echo ""

# Alertes
echo "⚠️  Alertes:"
ALERTS=0

# Vérifier l'espace disque
DISK_USAGE=$(df / | awk 'NR==2{print $5}' | sed 's/%//')
if [ "$DISK_USAGE" -gt 80 ]; then
    echo "   ⚠️  Espace disque critique: ${DISK_USAGE}%"
    ALERTS=$((ALERTS + 1))
fi

# Vérifier la RAM
MEM_USAGE=$(free | awk 'NR==2{printf "%.0f", $3*100/$2}')
if [ "$MEM_USAGE" -gt 90 ]; then
    echo "   ⚠️  Utilisation RAM élevée: ${MEM_USAGE}%"
    ALERTS=$((ALERTS + 1))
fi

# Vérifier les services
if ! docker exec shoptracker-db pg_isready -U shoptracker_admin > /dev/null 2>&1; then
    echo "   ❌ PostgreSQL hors ligne"
    ALERTS=$((ALERTS + 1))
fi

if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "   ❌ Application hors ligne"
    ALERTS=$((ALERTS + 1))
fi

if [ $ALERTS -eq 0 ]; then
    echo "   ✅ Aucune alerte"
fi

echo ""
echo "========================================="
echo "   Monitoring Terminé                   "
echo "========================================="
echo ""

# Actions rapides
echo "Actions disponibles:"
echo "   1. Redémarrer l'application: docker compose -f $APP_DIR/deploy/docker-compose.prod.yml restart"
echo "   2. Voir les logs: docker logs -f shoptracker-app"
echo "   3. Backup maintenant: /opt/shoptracker/scripts/backup.sh"
echo "   4. Monitoring temps réel: htop"
echo "   5. Docker stats: ctop"
echo ""

