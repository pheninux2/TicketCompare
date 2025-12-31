# 🚀 Quick Start - Déploiement Multi-Apps

## Résumé Ultra-Rapide

### 1. Sur votre PC Windows (PowerShell)

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
git add .
git commit -m "Add multi-app deployment support"
git push origin main
```

### 2. Attendez que GitHub Actions construise l'image
- Allez sur: https://github.com/pheninux2/TicketCompare/actions
- Attendez que le workflow se termine (environ 5-10 minutes)

### 3. Sur votre VPS

```bash
# Connexion
ssh root@178.128.162.253

# Installation App1
mkdir -p /opt/shoptracker/app1 && cd /opt/shoptracker/app1
curl -o deploy.sh https://raw.githubusercontent.com/pheninux2/TicketCompare/main/deploy/deploy-image.sh
chmod +x deploy.sh
./deploy.sh
```

### 4. Testez
Ouvrez votre navigateur: `http://178.128.162.253/app1/`

---

## Pour ajouter une 2ème application

```bash
# Sur le VPS
mkdir -p /opt/shoptracker/app2 && cd /opt/shoptracker/app2
cp /opt/shoptracker/app1/deploy.sh .

# Modifier le script pour app2
sed -i 's/app1/app2/g' deploy.sh
sed -i 's/8080:8080/8081:8080/g' deploy.sh

# Déployer
./deploy.sh
```

Accès: `http://178.128.162.253/app2/`

---

## Mise à jour rapide

```bash
cd /opt/shoptracker/app1
curl -o update.sh https://raw.githubusercontent.com/pheninux2/TicketCompare/main/deploy/update-image.sh
chmod +x update.sh
./update.sh app1
```

---

## Structure finale sur le VPS

```
/opt/shoptracker/
├── app1/                          # Application 1 (port 8080)
│   └── environments/prod/
│       ├── .env
│       ├── docker-compose-pull.yml
│       └── data/
├── app2/                          # Application 2 (port 8081)
│   └── environments/prod/
│       └── ...
└── app3/                          # Application 3 (port 8082)
    └── environments/prod/
        └── ...

/etc/nginx/sites-available/
└── multi-app                      # Configuration Nginx

Accès:
- http://178.128.162.253/           → Liste des apps
- http://178.128.162.253/app1/      → Application 1
- http://178.128.162.253/app2/      → Application 2
- http://178.128.162.253/app3/      → Application 3
```

---

## Commandes essentielles

```bash
# Voir tous les conteneurs
docker ps

# Logs d'une app
docker logs -f shoptracker-app1

# Redémarrer une app
cd /opt/shoptracker/app1/environments/prod
docker compose -f docker-compose-pull.yml restart

# Mettre à jour une app
docker pull ghcr.io/pheninux2/ticketcompare:latest
docker compose -f docker-compose-pull.yml up -d

# Nginx
nginx -t              # Tester la config
systemctl reload nginx # Recharger
```

---

## Dépannage express

**502 Bad Gateway?**
```bash
# Vérifier que l'app répond
curl http://localhost:8080/actuator/health

# Voir les logs
docker logs shoptracker-app1
```

**Image ne se télécharge pas?**
```bash
# Vérifier l'image
docker pull ghcr.io/pheninux2/ticketcompare:latest

# Si privée, authentifier
echo $GITHUB_TOKEN | docker login ghcr.io -u pheninux2 --password-stdin
```

**App ne démarre pas?**
```bash
# Voir les logs
docker compose -f docker-compose-pull.yml logs app

# Vérifier la config
cat .env
```

---

## Liens utiles

- **Guide complet**: `GUIDE_MULTI_APPS.md`
- **GitHub Actions**: https://github.com/pheninux2/TicketCompare/actions
- **Packages GHCR**: https://github.com/pheninux2?tab=packages
- **IP VPS**: http://178.128.162.253

---

## Support

Pour plus de détails, consultez:
- `GUIDE_MULTI_APPS.md` - Guide détaillé complet
- `README.md` - Guide général de déploiement

