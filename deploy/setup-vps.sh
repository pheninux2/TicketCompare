#!/bin/bash
# ==========================================
# Script d'Installation VPS - ShopTracker
# DigitalOcean Ubuntu 22.04 LTS
# ==========================================

set -e  # Arrêter en cas d'erreur

echo ""
echo "========================================="
echo "   ShopTracker - Installation VPS      "
echo "   DigitalOcean - Ubuntu 22.04         "
echo "========================================="
echo ""

# Vérifier que le script est exécuté en tant que root
if [ "$EUID" -ne 0 ]; then
    echo "[ERREUR] Ce script doit être exécuté en tant que root"
    echo "Utilisez: sudo ./setup-vps.sh"
    exit 1
fi

echo "[*] Mise à jour du système..."
apt-get update -qq
apt-get upgrade -y -qq

echo "[*] Installation des outils de base..."
apt-get install -y -qq \
    curl \
    wget \
    git \
    ufw \
    htop \
    vim \
    nano \
    jq \
    openssl \
    ca-certificates \
    gnupg \
    lsb-release

echo ""
echo "========================================="
echo "   Installation de Docker              "
echo "========================================="
echo ""

# Vérifier si Docker est déjà installé
if command -v docker &> /dev/null; then
    echo "[OK] Docker est déjà installé"
else
    echo "[*] Installation de Docker..."

    # Ajouter la clé GPG officielle de Docker
    install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    chmod a+r /etc/apt/keyrings/docker.gpg

    # Ajouter le repository Docker
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
      $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

    # Installer Docker
    apt-get update -qq
    apt-get install -y -qq docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

    echo "[OK] Docker installé avec succès"
fi

# Démarrer et activer Docker
systemctl start docker
systemctl enable docker

echo ""
echo "========================================="
echo "   Installation de Nginx               "
echo "========================================="
echo ""

if command -v nginx &> /dev/null; then
    echo "[OK] Nginx est déjà installé"
else
    echo "[*] Installation de Nginx..."
    apt-get install -y -qq nginx
    systemctl enable nginx
    echo "[OK] Nginx installé avec succès"
fi

echo ""
echo "========================================="
echo "   Installation de Certbot (SSL)       "
echo "========================================="
echo ""

if command -v certbot &> /dev/null; then
    echo "[OK] Certbot est déjà installé"
else
    echo "[*] Installation de Certbot..."
    apt-get install -y -qq certbot python3-certbot-nginx
    echo "[OK] Certbot installé avec succès"
fi

echo ""
echo "========================================="
echo "   Configuration du Pare-feu (UFW)     "
echo "========================================="
echo ""

echo "[*] Configuration UFW..."
ufw --force reset
ufw default deny incoming
ufw default allow outgoing
ufw allow 22/tcp comment 'SSH'
ufw allow 80/tcp comment 'HTTP'
ufw allow 443/tcp comment 'HTTPS'
ufw --force enable

echo "[OK] Pare-feu configuré"
ufw status

echo ""
echo "========================================="
echo "   Création de l'utilisateur deployer  "
echo "========================================="
echo ""

if id "deployer" &>/dev/null; then
    echo "[OK] L'utilisateur deployer existe déjà"
else
    echo "[*] Création de l'utilisateur deployer..."
    useradd -m -s /bin/bash deployer
    usermod -aG docker deployer

    # Générer un mot de passe aléatoire
    DEPLOYER_PASSWORD=$(openssl rand -base64 32)
    echo "deployer:$DEPLOYER_PASSWORD" | chpasswd

    # Créer le fichier de credentials
    mkdir -p /root/credentials
    echo "Deployer Password: $DEPLOYER_PASSWORD" > /root/credentials/deployer_credentials.txt
    chmod 600 /root/credentials/deployer_credentials.txt

    echo "[OK] Utilisateur deployer créé"
    echo "[INFO] Mot de passe sauvegardé dans: /root/credentials/deployer_credentials.txt"
fi

echo ""
echo "========================================="
echo "   Création de la structure de dossiers"
echo "========================================="
echo ""

echo "[*] Création des dossiers..."

# Créer la structure pour l'application
mkdir -p /opt/shoptracker/{app,backups,logs,ssl}
mkdir -p /opt/shoptracker/logs/{nginx,app,postgres}

# Créer les dossiers de données Docker
mkdir -p /opt/shoptracker/data/{postgres,uploads}

# Permissions
chown -R deployer:deployer /opt/shoptracker
chmod -R 755 /opt/shoptracker

echo "[OK] Structure de dossiers créée dans /opt/shoptracker"

echo ""
echo "========================================="
echo "   Configuration de Git                "
echo "========================================="
echo ""

# Configurer Git pour deployer
su - deployer -c "git config --global user.name 'Deployer'"
su - deployer -c "git config --global user.email 'deployer@shoptracker.local'"

echo "[OK] Git configuré"

echo ""
echo "========================================="
echo "   Copie des scripts de déploiement    "
echo "========================================="
echo ""

# Créer le dossier pour les scripts
mkdir -p /opt/shoptracker/scripts

# Télécharger les scripts depuis GitHub (sera fait manuellement après push)
echo "[INFO] Les scripts de déploiement doivent être copiés manuellement"
echo "[INFO] Emplacement: /opt/shoptracker/scripts/"

echo ""
echo "========================================="
echo "   Configuration de la rotation des logs"
echo "========================================="
echo ""

cat > /etc/logrotate.d/shoptracker << 'EOF'
/opt/shoptracker/logs/**/*.log {
    daily
    rotate 14
    compress
    delaycompress
    notifempty
    missingok
    create 0640 deployer deployer
    sharedscripts
    postrotate
        docker restart shoptracker-app 2>/dev/null || true
    endscript
}
EOF

echo "[OK] Rotation des logs configurée (14 jours)"

echo ""
echo "========================================="
echo "   Configuration des backups automatiques"
echo "========================================="
echo ""

# Créer un script de backup quotidien
cat > /opt/shoptracker/scripts/auto-backup.sh << 'EOF'
#!/bin/bash
# Backup automatique quotidien

BACKUP_DIR="/opt/shoptracker/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/auto_backup_$DATE.dump"

# Créer le backup
docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c -b > "$BACKUP_FILE"

# Vérifier le succès
if [ $? -eq 0 ]; then
    echo "[$(date)] Backup réussi: $BACKUP_FILE" >> /opt/shoptracker/logs/backup.log

    # Supprimer les backups de plus de 30 jours
    find "$BACKUP_DIR" -name "auto_backup_*.dump" -mtime +30 -delete
else
    echo "[$(date)] ERREUR: Backup échoué" >> /opt/shoptracker/logs/backup.log
fi
EOF

chmod +x /opt/shoptracker/scripts/auto-backup.sh
chown deployer:deployer /opt/shoptracker/scripts/auto-backup.sh

# Ajouter au crontab
(crontab -u deployer -l 2>/dev/null; echo "0 2 * * * /opt/shoptracker/scripts/auto-backup.sh") | crontab -u deployer -

echo "[OK] Backup automatique configuré (tous les jours à 2h du matin)"

echo ""
echo "========================================="
echo "   Optimisation du système             "
echo "========================================="
echo ""

# Augmenter les limites de fichiers ouverts
cat >> /etc/security/limits.conf << EOF
* soft nofile 65536
* hard nofile 65536
EOF

# Optimisation réseau
cat >> /etc/sysctl.conf << EOF
# Optimisations réseau pour production
net.core.somaxconn = 65535
net.ipv4.tcp_max_syn_backlog = 8096
net.ipv4.ip_local_port_range = 1024 65535
EOF

sysctl -p > /dev/null 2>&1

echo "[OK] Optimisations système appliquées"

echo ""
echo "========================================="
echo "   Installation de monitoring tools    "
echo "========================================="
echo ""

# Installer ctop (monitoring Docker)
wget -q https://github.com/bcicen/ctop/releases/download/v0.7.7/ctop-0.7.7-linux-amd64 -O /usr/local/bin/ctop
chmod +x /usr/local/bin/ctop

echo "[OK] Outils de monitoring installés (htop, ctop)"

echo ""
echo "========================================="
echo "   Génération des credentials          "
echo "========================================="
echo ""

# Générer des mots de passe sécurisés
DB_PASSWORD=$(openssl rand -base64 32)
ADMIN_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 64)

# Sauvegarder les credentials
cat > /root/credentials/app_credentials.txt << EOF
# ShopTracker - Credentials de Production
# Généré le: $(date)
# IP VPS: $(curl -s ifconfig.me)

# PostgreSQL
DB_PASSWORD=$DB_PASSWORD

# Admin Application
ADMIN_PASSWORD=$ADMIN_PASSWORD

# JWT Secret
JWT_SECRET=$JWT_SECRET

# Deployer User
$(cat /root/credentials/deployer_credentials.txt)
EOF

chmod 600 /root/credentials/app_credentials.txt

echo "[OK] Credentials générés et sauvegardés dans: /root/credentials/app_credentials.txt"

echo ""
echo "========================================="
echo "   Résumé de l'installation            "
echo "========================================="
echo ""

echo "✅ Système mis à jour"
echo "✅ Docker installé: $(docker --version)"
echo "✅ Docker Compose installé: $(docker compose version)"
echo "✅ Nginx installé: $(nginx -v 2>&1)"
echo "✅ Certbot installé: $(certbot --version)"
echo "✅ Pare-feu configuré (UFW)"
echo "✅ Utilisateur deployer créé"
echo "✅ Structure de dossiers créée (/opt/shoptracker)"
echo "✅ Rotation des logs configurée"
echo "✅ Backup automatique configuré (2h du matin)"
echo "✅ Optimisations système appliquées"
echo "✅ Credentials générés"
echo ""

echo "========================================="
echo "   Informations Importantes            "
echo "========================================="
echo ""

echo "📁 Dossier principal: /opt/shoptracker"
echo "🔑 Credentials: /root/credentials/"
echo "📊 Logs: /opt/shoptracker/logs/"
echo "💾 Backups: /opt/shoptracker/backups/"
echo ""

echo "🔐 Utilisateur deployer:"
echo "   Username: deployer"
echo "   Password: Voir /root/credentials/deployer_credentials.txt"
echo ""

echo "📝 Prochaines étapes:"
echo "   1. Consultez les credentials: cat /root/credentials/app_credentials.txt"
echo "   2. Copiez les scripts de déploiement dans /opt/shoptracker/scripts/"
echo "   3. Connectez-vous en tant que deployer: su - deployer"
echo "   4. Lancez le déploiement: cd /opt/shoptracker/scripts && ./deploy-app.sh"
echo ""

echo "========================================="
echo "   Installation Terminée ! ✅           "
echo "========================================="
echo ""

# Afficher l'IP publique
PUBLIC_IP=$(curl -s ifconfig.me)
echo "🌐 Votre application sera accessible à: http://$PUBLIC_IP"
echo ""

