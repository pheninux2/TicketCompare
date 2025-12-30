#!/bin/bash
# ===================================
# ShopTracker - Creation structure VPS
# ===================================

echo "========================================="
echo "   Creation de la structure VPS"
echo "========================================="
echo ""

# Creer les dossiers necessaires
echo "[*] Creation des dossiers..."

mkdir -p /opt/shoptracker/scripts
mkdir -p /opt/shoptracker/data
mkdir -p /opt/shoptracker/backups
mkdir -p /opt/shoptracker/logs

echo "[OK] Dossiers crees:"
ls -la /opt/shoptracker/

echo ""
echo "[OK] Structure VPS prete!"
echo ""

