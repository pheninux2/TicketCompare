# 🚀 Guide de Déploiement - ShopTracker sur VPS

## 📋 Informations VPS

```yaml
Provider: DigitalOcean
IP: 178.128.162.253
OS: Ubuntu 22.04 LTS
RAM: 2 GB
CPU: 1 vCPU
SSD: 50 GB
```

---

## 🎯 Vue d'Ensemble

Ce guide vous accompagne pour déployer **ShopTracker** sur votre VPS DigitalOcean en **3 étapes simples**.

### Durée Totale : ~30 minutes

1. **Installation du VPS** (15 min) - Automatique
2. **Déploiement de l'application** (10 min) - Automatique  
3. **Vérification** (5 min) - Manuel

---

## 📂 Structure des Scripts

```
deploy/
├── README.md                      # Ce fichier
├── setup-vps.sh                   # 1️⃣ Installation initiale VPS
├── deploy-app.sh                  # 2️⃣ Déploiement de l'application
├── update-app.sh                  # Mise à jour de l'application
├── backup.sh                      # Backup manuel de la BDD
├── restore.sh                     # Restauration de la BDD
├── monitor.sh                     # Monitoring du système
├── .env.production.template       # Template des variables d'environnement
├── docker-compose.prod.yml        # Configuration Docker production
└── nginx/
    └── shoptracker.conf           # Configuration Nginx
```

---

## 🚀 Déploiement Rapide (Quick Start)

### Étape 1 : Connexion au VPS

**Depuis Windows PowerShell :**

```powershell
ssh root@178.128.162.253
# Mot de passe : DoBaygo1Pignando
```

**⚠️ Important :** Lors de la première connexion, vous serez invité à **changer le mot de passe root**. Choisissez un mot de passe fort.

---

### Étape 2 : Installation du VPS (15 min)

Une fois connecté en SSH :

```bash
# Télécharger le script d'installation
curl -o setup-vps.sh https://raw.githubusercontent.com/VOTRE_USER/TicketCompare/main/deploy/setup-vps.sh

# Rendre le script exécutable
chmod +x setup-vps.sh

# Lancer l'installation
./setup-vps.sh
```

**Ce script va installer automatiquement :**
- ✅ Docker + Docker Compose
- ✅ Nginx (reverse proxy)
- ✅ Certbot (SSL Let's Encrypt)
- ✅ Pare-feu UFW
- ✅ Utilisateur `deployer`
- ✅ Structure de dossiers
- ✅ Backup automatique quotidien
- ✅ Monitoring tools

**⏱️ Durée : ~15 minutes**

---

### Étape 3 : Déploiement de l'Application (10 min)

```bash
# Se connecter avec l'utilisateur deployer
su - deployer

# Aller dans le dossier scripts
cd /opt/shoptracker/scripts

# Copier les scripts de déploiement depuis votre repo
git clone https://github.com/VOTRE_USER/TicketCompare.git /tmp/repo
cp -r /tmp/repo/deploy/* /opt/shoptracker/scripts/
rm -rf /tmp/repo

# Rendre les scripts exécutables
chmod +x *.sh

# Lancer le déploiement
./deploy-app.sh
```

**Le script va demander :**
- L'URL de votre repository GitHub (ex: https://github.com/VOTRE_USER/TicketCompare.git)

**Puis il va automatiquement :**
- ✅ Cloner votre repository
- ✅ Configurer les variables d'environnement
- ✅ Build l'image Docker
- ✅ Démarrer PostgreSQL
- ✅ Démarrer l'application Spring Boot
- ✅ Configurer Nginx

**⏱️ Durée : ~10 minutes**

---

### Étape 4 : Vérification

**Votre application est maintenant accessible à :**

```
http://178.128.162.253
```

**Test de connexion :**

```bash
# Depuis le VPS
curl http://localhost:8080/actuator/health

# Depuis votre navigateur
http://178.128.162.253
```

---

## 🔐 Credentials

### Root Credentials

Sauvegardés dans : `/root/credentials/app_credentials.txt`

```bash
# Voir les credentials
cat /root/credentials/app_credentials.txt
```

**Contient :**
- Mot de passe PostgreSQL
- Mot de passe Admin de l'application
- Secret JWT
- Mot de passe utilisateur deployer

### Admin Application

```
Username: admin
Password: Voir /root/credentials/app_credentials.txt
```

---

## 📊 Commandes Utiles

### Gestion de l'Application

```bash
# Voir les logs en temps réel
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml logs -f

# Logs de l'application uniquement
docker logs -f shoptracker-app

# Logs de PostgreSQL
docker logs -f shoptracker-db

# Redémarrer l'application
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml restart

# Arrêter tous les services
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml down

# Démarrer tous les services
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml up -d

# Status des conteneurs
docker ps
```

### Monitoring

```bash
# Script de monitoring complet
cd /opt/shoptracker/scripts
./monitor.sh

# Monitoring temps réel des conteneurs
ctop

# Monitoring système
htop

# Espace disque
df -h

# Utilisation RAM
free -h
```

### Backup & Restauration

```bash
# Backup manuel
cd /opt/shoptracker/scripts
./backup.sh

# Lister les backups
ls -lh /opt/shoptracker/backups/

# Restaurer un backup (interactif)
./restore.sh

# Restaurer un backup spécifique
./restore.sh /opt/shoptracker/backups/backup_20251230_140000.dump
```

### Mise à Jour de l'Application

```bash
# Depuis le dossier scripts
cd /opt/shoptracker/scripts
./update-app.sh
```

**Ce script va :**
1. Pull les dernières modifications depuis GitHub
2. Rebuild l'image Docker
3. Redémarrer les services

---

## 🔒 Configuration SSL/HTTPS (Optionnel)

### Si Vous Avez un Domaine

**1. Pointez votre domaine vers le VPS**

Dans votre gestionnaire DNS, créez un enregistrement A :

```
Type: A
Name: @  (ou shoptracker)
Value: 178.128.162.253
TTL: 3600
```

**2. Attendez la propagation DNS** (5-30 minutes)

Vérifiez avec :
```bash
nslookup votre-domaine.com
```

**3. Obtenez le certificat SSL**

```bash
sudo certbot --nginx -d votre-domaine.com

# Si vous avez aussi www
sudo certbot --nginx -d votre-domaine.com -d www.votre-domaine.com
```

**4. Renouvellement automatique**

Certbot configure automatiquement le renouvellement. Vérifiez :

```bash
sudo certbot renew --dry-run
```

---

## 🗂️ Structure des Dossiers sur le VPS

```
/opt/shoptracker/
├── app/                           # Code de l'application
│   ├── src/
│   ├── deploy/
│   │   ├── .env.production       # Variables d'environnement
│   │   └── docker-compose.prod.yml
│   └── ...
├── scripts/                       # Scripts de maintenance
│   ├── deploy-app.sh
│   ├── update-app.sh
│   ├── backup.sh
│   ├── restore.sh
│   └── monitor.sh
├── data/                          # Données persistantes
│   ├── postgres/                 # Données PostgreSQL
│   └── uploads/                  # Fichiers uploadés
├── backups/                       # Backups de la BDD
│   ├── backup_20251230_140000.dump
│   └── auto_backup_*.dump
├── logs/                          # Logs centralisés
│   ├── nginx/
│   ├── app/
│   └── postgres/
└── ssl/                           # Certificats SSL (si domaine)
```

---

## 🚨 Dépannage

### L'application ne démarre pas

```bash
# Voir les logs
docker logs shoptracker-app

# Vérifier PostgreSQL
docker exec shoptracker-db pg_isready -U shoptracker_admin

# Redémarrer
docker compose -f /opt/shoptracker/app/deploy/docker-compose.prod.yml restart
```

### Erreur de connexion à PostgreSQL

```bash
# Vérifier que PostgreSQL écoute
docker exec shoptracker-db netstat -tuln | grep 5432

# Vérifier les variables d'environnement
cat /opt/shoptracker/app/deploy/.env.production

# Recréer les conteneurs
cd /opt/shoptracker/app
docker compose -f deploy/docker-compose.prod.yml down
docker compose -f deploy/docker-compose.prod.yml up -d
```

### Nginx retourne 502 Bad Gateway

```bash
# L'application n'est pas encore démarrée
# Attendez 1-2 minutes et réessayez

# Vérifier que l'app écoute sur le port 8080
curl http://localhost:8080/actuator/health

# Vérifier les logs Nginx
tail -f /opt/shoptracker/logs/nginx/error.log
```

### Manque d'espace disque

```bash
# Nettoyer les anciennes images Docker
docker system prune -a

# Supprimer les anciens backups (>30 jours)
find /opt/shoptracker/backups -name "backup_*.dump" -mtime +30 -delete

# Nettoyer les logs
sudo journalctl --vacuum-time=7d
```

### Port déjà utilisé

```bash
# Trouver le processus qui utilise le port 8080
sudo lsof -i :8080

# Arrêter le processus
sudo kill -9 PID
```

---

## 🔄 Workflow de Mise à Jour

### Développement Local → VPS

**1. Sur votre machine locale (Windows) :**

```powershell
# Faire vos modifications
git add .
git commit -m "Nouvelle fonctionnalité"
git push origin main
```

**2. Sur le VPS :**

```bash
# Se connecter
ssh deployer@178.128.162.253

# Mettre à jour
cd /opt/shoptracker/scripts
./update-app.sh
```

**C'est tout ! Votre application est mise à jour automatiquement.**

---

## 📈 Monitoring & Alertes

### Script de Monitoring

```bash
# Monitoring complet
cd /opt/shoptracker/scripts
./monitor.sh
```

**Affiche :**
- ✅ Uptime du serveur
- ✅ CPU, RAM, Disque
- ✅ Status des conteneurs
- ✅ Status PostgreSQL
- ✅ Status de l'application
- ✅ Status Nginx
- ✅ Derniers logs d'erreur
- ✅ Informations sur les backups
- ✅ Alertes (disque >80%, RAM >90%, services down)

### Monitoring Temps Réel

```bash
# Monitoring des conteneurs Docker
ctop

# Monitoring système
htop

# Logs en temps réel
docker logs -f shoptracker-app
```

---

## 💾 Stratégie de Backup

### Backups Automatiques

**Configuration :**
- Fréquence : **Quotidien à 2h du matin**
- Rétention : **30 jours**
- Emplacement : `/opt/shoptracker/backups/`
- Format : PostgreSQL dump compressé (`.dump`)

### Backups Manuels

```bash
# Créer un backup maintenant
cd /opt/shoptracker/scripts
./backup.sh
```

### Restauration

```bash
# Mode interactif (sélection du backup)
./restore.sh

# Restaurer un backup spécifique
./restore.sh /opt/shoptracker/backups/backup_20251230_140000.dump
```

**⚠️ La restauration :**
- Crée un backup de sécurité avant
- Demande confirmation (tapez "RESTAURER")
- Arrête temporairement l'application

---

## 🛡️ Sécurité

### Bonnes Pratiques Appliquées

✅ **Pare-feu UFW** : Seuls ports 22, 80, 443 ouverts  
✅ **PostgreSQL** : Accessible uniquement en local (127.0.0.1)  
✅ **Credentials sécurisés** : Générés automatiquement (32+ caractères)  
✅ **Utilisateur dédié** : `deployer` (pas de root en production)  
✅ **SSL/TLS** : Ready pour Let's Encrypt  
✅ **Backups chiffrés** : PostgreSQL dump format  
✅ **Logs centralisés** : Rotation automatique (14 jours)  
✅ **Docker isolation** : Réseau bridge dédié  

### Commandes de Sécurité

```bash
# Vérifier le pare-feu
sudo ufw status

# Vérifier les ports ouverts
sudo netstat -tuln

# Vérifier les connexions actives
sudo netstat -an | grep ESTABLISHED

# Logs d'authentification
sudo tail -f /var/log/auth.log
```

---

## 📞 Support & Resources

### Logs Importants

```bash
# Logs application
docker logs shoptracker-app

# Logs PostgreSQL
docker logs shoptracker-db

# Logs Nginx
tail -f /opt/shoptracker/logs/nginx/error.log
tail -f /opt/shoptracker/logs/nginx/access.log

# Logs système
sudo journalctl -u docker -f
```

### Commandes de Diagnostic

```bash
# Vérifier Docker
docker --version
docker ps
docker images

# Vérifier Nginx
sudo nginx -t
sudo systemctl status nginx

# Vérifier l'espace disque
df -h
du -sh /opt/shoptracker/*

# Vérifier la RAM
free -h

# Vérifier le CPU
top -bn1 | head -20
```

---

## ✅ Checklist de Déploiement

### Avant le Déploiement

- [ ] VPS DigitalOcean créé et accessible
- [ ] Repository GitHub à jour
- [ ] Mot de passe root changé
- [ ] Accès SSH fonctionnel

### Installation VPS

- [ ] `setup-vps.sh` exécuté avec succès
- [ ] Docker installé et fonctionnel
- [ ] Nginx installé
- [ ] Pare-feu UFW actif
- [ ] Credentials sauvegardés dans `/root/credentials/`

### Déploiement Application

- [ ] Repository cloné
- [ ] Variables d'environnement configurées
- [ ] Image Docker buildée
- [ ] Conteneurs démarrés
- [ ] Nginx configuré
- [ ] Application accessible via HTTP

### Post-Déploiement

- [ ] Health check OK (`/actuator/health`)
- [ ] Connexion admin testée
- [ ] Backup automatique configuré
- [ ] Monitoring fonctionnel
- [ ] SSL configuré (si domaine disponible)

---

## 🎯 Prochaines Étapes

1. **Tester l'application** : http://178.128.162.253
2. **Se connecter en admin** : Utiliser les credentials de `/root/credentials/app_credentials.txt`
3. **Configurer un domaine** (optionnel) : Suivre la section SSL/HTTPS
4. **Surveiller les logs** : `docker logs -f shoptracker-app`
5. **Faire un premier backup** : `./backup.sh`

---

## 📚 Documentation Complémentaire

- **DigitalOcean Docs** : https://docs.digitalocean.com
- **Docker Docs** : https://docs.docker.com
- **Nginx Docs** : https://nginx.org/en/docs/
- **Spring Boot Actuator** : https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html

---

**✅ Votre application est maintenant en production sur DigitalOcean ! 🎉**

**Besoin d'aide ?** Consultez les logs ou utilisez `./monitor.sh` pour diagnostiquer.

---

**Date de création : 2025-12-30**  
**Version : 1.0**  
**VPS : DigitalOcean Ubuntu 22.04 LTS**

