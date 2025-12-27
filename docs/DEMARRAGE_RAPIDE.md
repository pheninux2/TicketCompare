# ⚡ Démarrage Rapide - TicketCompare

Guide ultra-rapide pour démarrer TicketCompare en 5 minutes.

## 🎯 Choix Rapide

| Je veux... | Script à lancer |
|------------|-----------------|
| **Production avec PostgreSQL** | `.\scripts\start-docker.bat` |
| **Test rapide avec H2** | `.\scripts\start-docker-h2.bat` |
| **Développement local** | `.\scripts\start-dev.bat` |

---

## 🐳 Option 1 : Docker + PostgreSQL (Recommandé)

### Étapes

1. **Ouvrir PowerShell dans le dossier du projet**
   ```powershell
   cd C:\Users\votre-user\IdeaProjects\TicketCompare
   ```

2. **Configurer (première fois uniquement)**
   ```powershell
   cd docker
   copy .env.example .env
   notepad .env  # Modifier les mots de passe
   cd ..
   ```

3. **Lancer**
   ```powershell
   .\scripts\start-docker.bat
   ```

4. **Accéder**
   - 🌐 Application : http://localhost:8080
   - 💾 PostgreSQL : localhost:5432

### Arrêter
```powershell
.\scripts\stop-docker.bat
```

---

## 🧪 Option 2 : Docker + H2 (Test)

### Pour les tests rapides sans PostgreSQL

```powershell
.\scripts\start-docker-h2.bat
```

- 🌐 Application : http://localhost:8080
- 💾 Console H2 : http://localhost:8080/h2-console

---

## 💻 Option 3 : Développement Local (Sans Docker)

### Prérequis
- Java 21 installé
- Maven installé

### Lancer
```powershell
.\scripts\start-dev.bat
```

### Accès
- 🌐 Application : http://localhost:8080
- 💾 Console H2 : http://localhost:8080/h2-console
  - JDBC URL : `jdbc:h2:mem:ticketcomparedb`
  - Username : `sa`
  - Password : (vide)

---

## 📋 Commandes Utiles

```powershell
# Voir les logs Docker
.\scripts\logs.bat

# Arrêter Docker
.\scripts\stop-docker.bat

# Reconstruire l'image Docker
.\scripts\rebuild-docker.bat
```

---

## ✅ Vérifier que Tout Fonctionne

1. **Ouvrir** : http://localhost:8080
2. **Créer un ticket** manuellement
3. **Voir les statistiques**
4. **Exporter en CSV**

---

## 🆘 Problème ?

### Port 8080 déjà utilisé
```powershell
# Modifier le port dans docker\.env
APP_PORT=8081
```

### Erreur Docker
```powershell
# Réinitialiser
cd docker
docker-compose down -v
docker-compose up -d
```

### Autre problème
Consultez : `docs\INSTALLATION.md`

---

## 📚 Pour Aller Plus Loin

- **[Installation Complète](INSTALLATION.md)**
- **[Guide Utilisateur](GUIDE_COMPLET.md)**
- **[Architecture](ARCHITECTURE.md)**
- **[Réorganisation](REORGANISATION.md)**

---

## 🎉 C'est Tout !

Votre application TicketCompare est maintenant opérationnelle !

**Prochaines étapes :**
1. Créer votre premier ticket
2. Scanner un ticket avec OCR (si configuré)
3. Visualiser vos statistiques
4. Explorer les fonctionnalités

**Bon usage ! 🚀**

