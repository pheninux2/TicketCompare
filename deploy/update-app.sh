#!/bin/bash
# ==========================================
# Script de Mise à Jour - ShopTracker
# VPS DigitalOcean
# ==========================================

set -e

echo ""
echo "========================================="
echo "   ShopTracker - Mise à Jour            "
echo "========================================="
echo ""

APP_DIR="/opt/shoptracker/app"

cd "$APP_DIR"

echo "[*] Pull des dernières modifications..."
git pull origin main

echo "[*] Rebuild de l'image Docker..."
docker compose -f deploy/docker-compose.prod.yml build --no-cache

echo "[*] Redémarrage des services..."
docker compose -f deploy/docker-compose.prod.yml down
docker compose -f deploy/docker-compose.prod.yml up -d

echo "[*] Attente du redémarrage (30 secondes)..."
sleep 30

echo ""
echo "[OK] Mise à jour terminée !"
echo ""
echo "Vérifiez les logs: docker compose -f deploy/docker-compose.prod.yml logs -f app"
echo ""

