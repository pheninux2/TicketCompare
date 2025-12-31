# 📦 Récapitulatif des Changements - Multi-App Deployment

## ✅ Ce qui a été créé

### 1. Configuration Nginx Multi-Apps
**Fichier:** `environments/prod/nginx/multi-app.conf`
- Support de plusieurs applications sur le même VPS
- Routage par chemin: `/app1/`, `/app2/`, `/app3/`
- Page d'accueil listant toutes les applications

### 2. GitHub Actions Workflow
**Fichier:** `.github/workflows/docker-build.yml`
- Build automatique de l'application Maven
- Création et push de l'image Docker vers GHCR
- Déclenchement sur push vers main/master
- Support des tags de version

### 3. Script de Déploiement Simplifié
**Fichier:** `deploy/deploy-image.sh`
- Déploiement sans cloner le code source
- Pull de l'image depuis GitHub Container Registry
- Configuration automatique de Nginx multi-apps
- Création de docker-compose-pull.yml

### 4. Script de Mise à Jour Rapide
**Fichier:** `deploy/update-image.sh`
- Mise à jour en une commande
- Pull de la nouvelle image + redémarrage
- Support de plusieurs applications

### 5. Documentation Complète
**Fichiers:**
- `deploy/GUIDE_MULTI_APPS.md` - Guide détaillé complet (avec troubleshooting)
- `deploy/QUICK_START.md` - Quick start pour démarrage rapide
- `deploy/SPRING_PROXY_CONFIG.md` - Configuration Spring Boot pour reverse proxy
- `deploy/README.md` - Mis à jour avec les nouveaux modes

### 6. Script PowerShell
**Fichier:** `deploy/push-to-github.ps1`
- Push facilité depuis Windows
- Affiche les prochaines étapes après le push

### 7. Configuration Spring Boot
**Fichiers modifiés:**
- `src/main/resources/application.yml` - Support reverse proxy
- `src/main/resources/application-prod.yml` - Configuration production avec proxy

## 🎯 Ce qui a changé

### Avant
```
VPS: 178.128.162.253
├── Clone tout le code source
├── Build sur le VPS (lent, consomme de l'espace)
└── Une seule application possible
```

### Après
```
VPS: 178.128.162.253
├── Pull image Docker uniquement (rapide, léger)
├── Plusieurs applications possibles:
│   ├── /app1/ → Port 8080
│   ├── /app2/ → Port 8081
│   └── /app3/ → Port 8082
└── Nginx gère le routage
```

## 🚀 Comment utiliser

### Mode 1: Déploiement Initial (Image)

```bash
# 1. Pousser le code sur GitHub (depuis Windows)
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\deploy\push-to-github.ps1

# 2. Attendre que GitHub Actions construise l'image (~5-10 min)
# Vérifier sur: https://github.com/pheninux2/TicketCompare/actions

# 3. Déployer sur le VPS
ssh root@178.128.162.253
mkdir -p /opt/shoptracker/app1 && cd /opt/shoptracker/app1
curl -o deploy.sh https://raw.githubusercontent.com/pheninux2/TicketCompare/main/deploy/deploy-image.sh
chmod +x deploy.sh
./deploy.sh
```

### Mode 2: Mise à Jour

```bash
# Sur le VPS
cd /opt/shoptracker/app1
curl -o update.sh https://raw.githubusercontent.com/pheninux2/TicketCompare/main/deploy/update-image.sh
chmod +x update.sh
./update.sh app1
```

### Mode 3: Ajouter une 2ème Application

```bash
# Sur le VPS
mkdir -p /opt/shoptracker/app2 && cd /opt/shoptracker/app2
cp /opt/shoptracker/app1/deploy.sh .

# Modifier pour app2 (port 8081)
sed -i 's/app1/app2/g' deploy.sh
sed -i 's/8080:8080/8081:8080/g' deploy.sh

./deploy.sh
```

## 📊 Structure Finale

### Sur GitHub
```
.github/workflows/
└── docker-build.yml          # Build automatique

deploy/
├── deploy-image.sh           # Nouveau déploiement
├── update-image.sh           # Mise à jour
├── push-to-github.ps1        # Helper Windows
├── GUIDE_MULTI_APPS.md       # Documentation complète
├── QUICK_START.md            # Quick start
├── SPRING_PROXY_CONFIG.md    # Config Spring Boot
└── README.md                 # Index

environments/prod/
├── nginx/
│   └── multi-app.conf        # Config Nginx multi-apps
└── docker-compose.yml        # Compose original (mode build)

src/main/resources/
├── application.yml           # Config dev + proxy
└── application-prod.yml      # Config prod + proxy
```

### Sur le VPS
```
/opt/shoptracker/
├── app1/
│   └── environments/prod/
│       ├── .env
│       ├── docker-compose-pull.yml
│       └── data/
│           ├── postgres/
│           ├── uploads/
│           ├── logs/
│           └── backups/
├── app2/
│   └── [même structure]
└── app3/
    └── [même structure]

/etc/nginx/sites-available/
└── multi-app                 # Configuration Nginx

Docker Images:
└── ghcr.io/pheninux2/ticketcompare:latest
```

## 🔑 Points Clés

1. **Deux modes de déploiement:**
   - Mode classique: `deploy-app.sh` (clone + build)
   - Mode image: `deploy-image.sh` (pull image) ⭐ **Recommandé**

2. **Multi-applications:**
   - Support natif de plusieurs apps sur même VPS
   - Routage par chemin (/app1/, /app2/, etc.)
   - Isolation complète des données et logs

3. **CI/CD Automatique:**
   - GitHub Actions build l'image automatiquement
   - Push sur main → nouvelle image disponible
   - Mise à jour sur VPS en une commande

4. **Configuration Spring Boot:**
   - Support reverse proxy avec headers X-Forwarded-*
   - Context path configurable
   - Sessions et cookies correctement configurés

## 🎓 Ressources pour Apprendre

1. **Quick Start:** `QUICK_START.md` - Commencer en 5 minutes
2. **Guide Complet:** `GUIDE_MULTI_APPS.md` - Tout savoir sur le déploiement
3. **Config Spring:** `SPRING_PROXY_CONFIG.md` - Configuration détaillée
4. **README:** `README.md` - Index général

## ✨ Avantages du Nouveau Système

| Avant | Après |
|-------|-------|
| Clone tout le repo | Pull image seulement |
| Build sur VPS (lent) | Build par GitHub (rapide) |
| 1 seule app par VPS | Plusieurs apps possibles |
| ~2 GB espace disque | ~500 MB espace disque |
| 10-15 min déploiement | 2-3 min déploiement |
| Build dépend du VPS | Build dans le cloud |

## 📝 Checklist de Migration

- [ ] Pousser les modifications sur GitHub
- [ ] Vérifier que le workflow GitHub Actions fonctionne
- [ ] Rendre l'image publique ou configurer GITHUB_TOKEN
- [ ] Tester le déploiement sur le VPS
- [ ] Vérifier que l'app répond sur /app1/
- [ ] Configurer les domaines/DNS si nécessaire
- [ ] Mettre en place les sauvegardes automatiques
- [ ] Configurer HTTPS avec Let's Encrypt

## 🆘 Support

En cas de problème:
1. Consulter le fichier `GUIDE_MULTI_APPS.md` section "Dépannage"
2. Vérifier les logs: `docker logs shoptracker-app1`
3. Tester avec curl: `curl http://localhost:8080/actuator/health`
4. Vérifier Nginx: `nginx -t && tail -f /var/log/nginx/multi_app_error.log`

## 🎉 Prochaines Étapes

1. **Pousser les changements:**
   ```powershell
   .\deploy\push-to-github.ps1
   ```

2. **Attendre le build GitHub Actions**

3. **Déployer sur le VPS:**
   ```bash
   ssh root@178.128.162.253
   # Suivre le QUICK_START.md
   ```

4. **Profiter de votre application! 🚀**

