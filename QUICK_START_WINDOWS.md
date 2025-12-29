# 🚀 DÉMARRAGE RAPIDE - ShopTracker Production Windows

## ⚡ Installation en 3 étapes

### 1️⃣ Installer Docker Desktop
Téléchargez et installez Docker Desktop pour Windows :
👉 https://www.docker.com/products/docker-desktop

### 2️⃣ Configurer
```powershell
# Copier le fichier d'environnement
cd docker
copy .env.prod.example .env

# Modifier avec vos mots de passe
notepad .env
```

### 3️⃣ Déployer
```powershell
# Lancer le déploiement
.\deploy-windows.ps1
```

---

## 🌐 Accès

```
Application : http://localhost:8080
```

---

## 💾 Backup

```powershell
# Créer un backup
.\backup-db.ps1

# Restaurer un backup
.\restore-db.ps1
```

---

## 📚 Documentation complète

Consultez **DEPLOIEMENT_WINDOWS.md** pour le guide complet.

---

## 🔐 Sécurité

- ✅ Base de données accessible uniquement en localhost
- ✅ Mots de passe personnalisables dans `.env`
- ✅ Données sauvegardées dans `docker/data/`
- ✅ Backups automatiques dans `docker/backups/`

---

## 🆘 Support

📧 adil.haddad.xdev@gmail.com

---

**ShopTracker v1.0.0** - Optimisez vos courses ! 🛒

