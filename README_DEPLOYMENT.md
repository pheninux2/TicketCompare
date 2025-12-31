# 🚀 Multi-App Deployment - README

## 📋 Vue d'ensemble

Ce projet supporte maintenant **deux modes de déploiement** et permet d'héberger **plusieurs applications** sur le même VPS avec des chemins différents.

---

## 🎯 Réponses à vos Questions

### ✅ Question 1 : Plusieurs applications sur le même VPS ?

**OUI !** Vous pouvez maintenant accéder à plusieurs applications comme :
- `http://178.128.162.253/app1/` → Application 1
- `http://178.128.162.253/app2/` → Application 2
- `http://178.128.162.253/app3/` → Application 3

**Configuration :** Voir `environments/prod/nginx/multi-app.conf`

### ✅ Question 2 : Pull seulement l'image Docker ?

**OUI !** Le nouveau script `deploy-image.sh` :
- Ne clone **PAS** tout le code source
- Pull **uniquement** l'image Docker depuis GitHub Container Registry
- L'image est construite automatiquement par GitHub Actions

**Workflow :** Voir `.github/workflows/docker-build.yml`

---

## 🚀 Démarrage Ultra-Rapide

### Depuis votre PC Windows

```powershell
# 1. Vérifier que tout est prêt
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\deploy\check-before-push.ps1

# 2. Le script va pousser automatiquement si tout est OK
```

### Sur votre VPS

```bash
# 3. Attendre que GitHub Actions finisse (~5-10 min)
# Vérifier : https://github.com/pheninux2/TicketCompare/actions

# 4. Déployer
ssh root@178.128.162.253
mkdir -p /opt/shoptracker/app1 && cd /opt/shoptracker/app1
curl -o deploy.sh https://raw.githubusercontent.com/pheninux2/TicketCompare/main/deploy/deploy-image.sh
chmod +x deploy.sh
./deploy.sh

# 5. Tester
curl http://localhost:8080/actuator/health
# Accéder : http://178.128.162.253/app1/
```

---

## 📁 Fichiers Importants

### 📖 Documentation

| Fichier | Description |
|---------|-------------|
| **`deploy/QUICK_START.md`** | ⭐ Commencez ici ! Guide rapide |
| **`deploy/GUIDE_MULTI_APPS.md`** | Guide complet avec troubleshooting |
| **`deploy/SPRING_PROXY_CONFIG.md`** | Configuration Spring Boot |
| **`CHANGES.md`** | Récapitulatif de tous les changements |

### 🛠️ Scripts

| Fichier | Usage |
|---------|-------|
| **`deploy/check-before-push.ps1`** | Vérifier avant de pousser (Windows) |
| **`deploy/push-to-github.ps1`** | Pousser vers GitHub (Windows) |
| **`deploy/deploy-image.sh`** | Déployer avec image Docker (VPS) |
| **`deploy/update-image.sh`** | Mettre à jour rapidement (VPS) |
| **`deploy/commands.sh`** | Commandes utiles (VPS) |

### ⚙️ Configuration

| Fichier | Description |
|---------|-------------|
| `.github/workflows/docker-build.yml` | Build automatique de l'image |
| `environments/prod/nginx/multi-app.conf` | Nginx multi-applications |
| `src/main/resources/application-prod.yml` | Config Spring Boot production |

---

## 🔄 Workflow de Développement

```
┌─────────────────────────────────────────────────────────┐
│  1. Développement Local (PC Windows)                    │
│     - Coder les fonctionnalités                         │
│     - Tester en local avec H2                           │
│     - Commit les changements                            │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  2. Vérification et Push                                │
│     > .\deploy\check-before-push.ps1                    │
│     - Vérifie les fichiers critiques                    │
│     - Push automatique vers GitHub                      │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  3. GitHub Actions (Automatique)                        │
│     - Build Maven                                       │
│     - Créer image Docker                                │
│     - Push vers ghcr.io                                 │
│     ⏱️ Durée : 5-10 minutes                            │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  4. Déploiement VPS                                     │
│     > ssh root@178.128.162.253                          │
│     > ./deploy.sh (première fois)                       │
│     > ./update.sh (mises à jour)                        │
│     - Pull de l'image                                   │
│     - Redémarrage automatique                           │
│     ⏱️ Durée : 2-3 minutes                             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  5. Application Accessible !                            │
│     http://178.128.162.253/app1/                        │
└─────────────────────────────────────────────────────────┘
```

---

## 🎯 Architecture Multi-Applications

```
Internet (http://178.128.162.253)
          ↓
     ┌────────┐
     │ Nginx  │ Port 80
     └────────┘
          ↓
    ┌─────┴─────┬─────────┬─────────┐
    ↓           ↓         ↓         ↓
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│   /    │ │ /app1/ │ │ /app2/ │ │ /app3/ │
│ Index  │ │Port8080│ │Port8081│ │Port8082│
└────────┘ └────────┘ └────────┘ └────────┘
              ↓           ↓           ↓
         ┌────────┐  ┌────────┐  ┌────────┐
         │Postgres│  │Postgres│  │Postgres│
         │  5432  │  │  5433  │  │  5434  │
         └────────┘  └────────┘  └────────┘
```

---

## 📊 Comparaison des Modes

| Critère | Mode Classique | Mode Image (Nouveau) |
|---------|----------------|----------------------|
| **Clone code** | ✅ Oui (~2 GB) | ❌ Non (~100 MB) |
| **Build sur VPS** | ✅ Oui (10-15 min) | ❌ Non (2-3 min) |
| **Dépendances** | Git, Maven, Docker | Docker seulement |
| **Espace disque** | ~2 GB | ~500 MB |
| **Build où ?** | Sur le VPS | GitHub Actions |
| **Multi-apps** | ❌ Difficile | ✅ Facile |
| **Script** | `deploy-app.sh` | `deploy-image.sh` |

**Recommandation :** ⭐ Utilisez le **Mode Image** pour la production

---

## 🔧 Configuration Requise

### Sur votre PC Windows

- ✅ Git installé
- ✅ PowerShell (natif Windows)
- ✅ Accès GitHub

### Sur le VPS

- ✅ Ubuntu 22.04 LTS
- ✅ Docker & Docker Compose
- ✅ Nginx (optionnel, installé par le script)
- ✅ 2 GB RAM minimum
- ✅ 10 GB espace disque minimum

---

## 📝 Commandes Essentielles

### Windows (PowerShell)

```powershell
# Vérifier avant de pousser
.\deploy\check-before-push.ps1

# Pousser vers GitHub
.\deploy\push-to-github.ps1

# Vérifier le build GitHub Actions
start https://github.com/pheninux2/TicketCompare/actions
```

### VPS (Bash)

```bash
# Première installation
./deploy.sh

# Mettre à jour
./update.sh app1

# Voir les logs
docker logs -f shoptracker-app1

# Redémarrer
cd /opt/shoptracker/app1/environments/prod
docker compose -f docker-compose-pull.yml restart

# Sauvegarder la BDD
./commands.sh backup app1

# Accéder au shell
docker exec -it shoptracker-app1 /bin/bash
```

---

## 🆘 Aide Rapide

### L'image ne se télécharge pas ?

```bash
# Vérifier que l'image existe
docker pull ghcr.io/pheninux2/ticketcompare:latest

# Si image privée, authentifier
export GITHUB_TOKEN=ghp_votre_token
echo $GITHUB_TOKEN | docker login ghcr.io -u pheninux2 --password-stdin
```

### L'application ne répond pas ?

```bash
# Vérifier les conteneurs
docker ps

# Voir les logs
docker logs shoptracker-app1

# Tester localement
curl http://localhost:8080/actuator/health
```

### Nginx retourne 502 ?

```bash
# Vérifier Nginx
nginx -t
systemctl status nginx

# Voir les logs Nginx
tail -f /var/log/nginx/multi_app_error.log
```

---

## 📚 Documentation Complète

1. **`deploy/QUICK_START.md`** - Démarrage rapide (5 minutes)
2. **`deploy/GUIDE_MULTI_APPS.md`** - Guide complet (30 minutes)
3. **`deploy/SPRING_PROXY_CONFIG.md`** - Configuration avancée
4. **`CHANGES.md`** - Liste des changements

---

## 🎉 Prochaines Étapes

1. ✅ **Lire ce README**
2. ⏳ **Exécuter** `.\deploy\check-before-push.ps1`
3. ⏳ **Attendre** le build GitHub Actions
4. ⏳ **Déployer** sur le VPS
5. ⏳ **Tester** votre application

---

## 🌟 Fonctionnalités Avancées

- 🔄 **Mises à jour** en une commande
- 📦 **Multi-applications** sur même VPS
- 🚀 **CI/CD** automatique via GitHub Actions
- 🔒 **HTTPS** avec Let's Encrypt (optionnel)
- 💾 **Sauvegardes** automatiques
- 📊 **Monitoring** des ressources
- 🔧 **Configuration** flexible par environnement

---

## 📞 Support

- **GitHub Actions** : https://github.com/pheninux2/TicketCompare/actions
- **Packages** : https://github.com/pheninux2?tab=packages
- **Documentation** : Fichiers dans `deploy/`

---

**Bonne chance avec votre déploiement ! 🚀**

