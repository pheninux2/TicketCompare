# 🚀 Déploiement VPS DigitalOcean - Guide Rapide

## 📋 Informations VPS

```
IP Publique : 178.128.162.253
OS          : Ubuntu 22.04 LTS
Provider    : DigitalOcean
RAM         : 2 GB
CPU         : 1 vCPU
```

---

## ⚡ Quick Start

### 1️⃣ Connexion SSH

```powershell
# Depuis Windows PowerShell
ssh root@178.128.162.253
```

**Mot de passe initial :** `DoBaygo1Pignando`  
⚠️ Vous serez invité à le changer lors de la première connexion.

---

### 2️⃣ Installation VPS (15 min)

```bash
# Télécharger le script
curl -o setup-vps.sh https://raw.githubusercontent.com/VOTRE_USER/TicketCompare/main/deploy/setup-vps.sh

# Rendre exécutable
chmod +x setup-vps.sh

# Installer
./setup-vps.sh
```

**Ce qui sera installé :**
- Docker + Docker Compose
- Nginx (reverse proxy)
- Certbot (SSL)
- Pare-feu UFW
- Utilisateur deployer
- Backup automatique quotidien

---

### 3️⃣ Déploiement Application (10 min)

```bash
# Se connecter en deployer
su - deployer

# Naviguer vers les scripts
cd /opt/shoptracker/scripts

# Copier les scripts depuis GitHub
git clone https://github.com/VOTRE_USER/TicketCompare.git /tmp/repo
cp -r /tmp/repo/deploy/* /opt/shoptracker/scripts/
rm -rf /tmp/repo

# Rendre exécutables
chmod +x *.sh

# Déployer
./deploy-app.sh
```

**Le script vous demandera l'URL de votre repository GitHub.**

---

### 4️⃣ Accès à l'Application

```
http://178.128.162.253
```

**Credentials Admin :**
```bash
# Sur le VPS
cat /root/credentials/app_credentials.txt
```

---

## 📊 Commandes Utiles

### Monitoring

```bash
# Script de monitoring complet
./monitor.sh

# Logs en temps réel
docker logs -f shoptracker-app

# Monitoring des conteneurs
ctop

# Monitoring système
htop
```

### Gestion de l'Application

```bash
# Redémarrer
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml restart

# Arrêter
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml down

# Démarrer
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml up -d

# Status
docker ps
```

### Backup & Restauration

```bash
# Backup manuel
./backup.sh

# Restauration interactive
./restore.sh

# Lister les backups
ls -lh /opt/shoptracker/backups/
```

### Mise à Jour

```bash
# Mettre à jour l'application
./update-app.sh
```

---

## 🔐 Sécurité

**Credentials générés automatiquement et sauvegardés dans :**
```
/root/credentials/app_credentials.txt
```

**Contient :**
- Mot de passe PostgreSQL
- Mot de passe Admin application
- Secret JWT
- Mot de passe deployer

---

## 🛠️ Workflow de Développement

### Développement Local → Production

**1. Sur votre machine Windows :**

```powershell
# Faire vos modifications
git add .
git commit -m "Nouvelle fonctionnalité"
git push origin main
```

**2. Sur le VPS :**

```bash
ssh deployer@178.128.162.253
cd /opt/shoptracker/scripts
./update-app.sh
```

**C'est tout ! ✅**

---

## 📂 Structure sur le VPS

```
/opt/shoptracker/
├── app/                  # Code de l'application
├── scripts/              # Scripts de maintenance
├── data/                 # Données persistantes
│   ├── postgres/        # Base de données
│   └── uploads/         # Fichiers uploadés
├── backups/             # Backups quotidiens
└── logs/                # Logs centralisés
```

---

## 🚨 Dépannage Rapide

### Application ne répond pas

```bash
# Voir les logs
docker logs shoptracker-app

# Redémarrer
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml restart
```

### Nginx 502 Bad Gateway

```bash
# L'application démarre (attendez 1-2 minutes)
curl http://localhost:8080/actuator/health

# Vérifier les logs
docker logs -f shoptracker-app
```

### Manque d'espace disque

```bash
# Nettoyer Docker
docker system prune -a

# Supprimer anciens backups
find /opt/shoptracker/backups -name "backup_*.dump" -mtime +30 -delete
```

---

## 🌐 Configuration SSL (Optionnel)

**Si vous avez un domaine :**

```bash
# Pointer le DNS vers 178.128.162.253
# Puis obtenir le certificat SSL
sudo certbot --nginx -d votre-domaine.com
```

---

## 📚 Documentation Complète

Consultez : `deploy/README.md`

---

## ✅ Checklist

- [ ] VPS accessible via SSH
- [ ] `setup-vps.sh` exécuté
- [ ] Docker fonctionnel
- [ ] Application déployée
- [ ] Accessible via http://178.128.162.253
- [ ] Credentials sauvegardés
- [ ] Backup automatique configuré

---

**🎉 Votre application est maintenant en production !**

**Besoin d'aide ?** Consultez `deploy/README.md` pour la documentation complète.

---

**Date : 2025-12-30**  
**Version : 1.0**  
**VPS : DigitalOcean**

