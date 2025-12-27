# 🚀 Guide d'Exécution - manage-h2.ps1

## 📋 Prérequis

- Windows avec PowerShell 5.1 ou supérieur
- Docker Desktop installé et démarré
- Projet TicketCompare

---

## 🎯 Méthode 1 : Exécution Simple (Recommandée)

### Depuis le Répertoire du Projet

```powershell
# 1. Ouvrir PowerShell
# Clic droit sur le dossier du projet → "Ouvrir dans Terminal" ou "Git Bash Here"

# 2. Naviguer vers le projet (si nécessaire)
cd C:\Users\pheni\IdeaProjects\TicketCompare

# 3. Exécuter le script
.\scripts\manage-h2.ps1 start
```

---

## 📝 Commandes Disponibles

### Démarrage et Arrêt

```powershell
# Démarrer (mode persistant - par défaut)
.\scripts\manage-h2.ps1 start

# Démarrer en mode mémoire (données non sauvegardées)
.\scripts\manage-h2.ps1 start -Mode mem

# Arrêter l'application
.\scripts\manage-h2.ps1 stop

# Redémarrer
.\scripts\manage-h2.ps1 restart
```

### Monitoring

```powershell
# Voir le statut
.\scripts\manage-h2.ps1 status

# Voir les logs en temps réel
.\scripts\manage-h2.ps1 logs
```

### Gestion des Données

```powershell
# Backup de la base de données
.\scripts\manage-h2.ps1 backup

# Backup avec nom personnalisé
.\scripts\manage-h2.ps1 backup -BackupFile "mon-backup.tar.gz"

# Restore d'un backup
.\scripts\manage-h2.ps1 restore -BackupFile "h2-backup-20241227-143052.tar.gz"

# Reset complet (supprime toutes les données)
.\scripts\manage-h2.ps1 reset
```

### Utilitaires

```powershell
# Ouvrir la console H2 dans le navigateur
.\scripts\manage-h2.ps1 console
```

---

## 🔧 Méthode 2 : Exécution avec Chemin Complet

Si vous n'êtes pas dans le répertoire du projet :

```powershell
# Remplacer le chemin par votre chemin réel
C:\Users\pheni\IdeaProjects\TicketCompare\scripts\manage-h2.ps1 start
```

---

## 🛠️ Méthode 3 : Exécution via PowerShell ISE

1. **Ouvrir PowerShell ISE**
   - Rechercher "PowerShell ISE" dans le menu Démarrer
   - Clic droit → "Exécuter en tant qu'administrateur" (si nécessaire)

2. **Ouvrir le script**
   - Menu : `Fichier` → `Ouvrir`
   - Naviguer vers : `C:\Users\pheni\IdeaProjects\TicketCompare\scripts\manage-h2.ps1`

3. **Modifier les paramètres en haut du script** (optionnel)
   ```powershell
   param(
       [Parameter(Position=0)]
       [string]$Action = 'start',  # ← Changer 'start' par 'status', 'backup', etc.
       
       [Parameter()]
       [string]$Mode = 'file',     # ← 'file' ou 'mem'
   )
   ```

4. **Exécuter**
   - Appuyer sur `F5` ou cliquer sur ▶️ "Exécuter le script"

---

## 🚫 Problème : Script Non Exécutable

### Erreur : "L'exécution de scripts est désactivée"

**Message d'erreur :**
```
manage-h2.ps1 cannot be loaded because running scripts is disabled on this system.
```

**Solution :**

```powershell
# 1. Ouvrir PowerShell en tant qu'Administrateur
# Clic droit sur PowerShell → "Exécuter en tant qu'administrateur"

# 2. Autoriser l'exécution de scripts (une seule fois)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# 3. Confirmer avec 'O' (Oui)

# 4. Fermer et rouvrir PowerShell normalement

# 5. Réessayer
.\scripts\manage-h2.ps1 start
```

**Alternative (Bypass ponctuel) :**
```powershell
# Exécuter sans changer la politique
powershell -ExecutionPolicy Bypass -File .\scripts\manage-h2.ps1 start
```

---

## 🎯 Méthode 4 : Créer un Raccourci

### Créer un fichier .bat pour accès rapide

1. **Créer un fichier** `start-h2.bat` à la racine du projet :

```batch
@echo off
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -File .\scripts\manage-h2.ps1 start
pause
```

2. **Double-cliquer** sur `start-h2.bat` pour démarrer

### Autres raccourcis utiles

**stop-h2.bat :**
```batch
@echo off
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -File .\scripts\manage-h2.ps1 stop
pause
```

**status-h2.bat :**
```batch
@echo off
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -File .\scripts\manage-h2.ps1 status
pause
```

**backup-h2.bat :**
```batch
@echo off
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -File .\scripts\manage-h2.ps1 backup
pause
```

---

## 📊 Exemples d'Utilisation

### Scénario 1 : Démarrage Quotidien

```powershell
# Se placer dans le projet
cd C:\Users\pheni\IdeaProjects\TicketCompare

# Démarrer
.\scripts\manage-h2.ps1 start

# Résultat :
# ========================================
#  TicketCompare - Gestion H2 Persistance
# ========================================
# 
# 🚀 Démarrage de l'application (Mode: file)
#    Mode PERSISTANT - Les données seront sauvegardées
# 
# ✅ Application démarrée avec succès !
# 
# 📊 Accès:
#   Application: http://localhost:8080
#   H2 Console:  http://localhost:8080/h2-console
```

### Scénario 2 : Vérifier le Statut

```powershell
.\scripts\manage-h2.ps1 status

# Résultat :
# 📊 Statut de l'application
# 
# Conteneur: ✅ En cours d'exécution
# Détails: Up 2 hours
# 
# Volume de données: ✅ Existe (ticketcompare_h2_data)
# Mountpoint: /var/lib/docker/volumes/ticketcompare_h2_data/_data
# 
# URLs:
#   Application: http://localhost:8080
#   H2 Console:  http://localhost:8080/h2-console
```

### Scénario 3 : Backup Avant Modification

```powershell
# 1. Faire un backup
.\scripts\manage-h2.ps1 backup

# Résultat :
# 💾 Backup de la base de données
#    Fichier: h2-backup-20241227-143052.tar.gz
# ✅ Backup créé: h2-backup-20241227-143052.tar.gz
#    Taille: 2.45 MB

# 2. Modifier l'application

# 3. Si problème, restaurer
.\scripts\manage-h2.ps1 restore -BackupFile "h2-backup-20241227-143052.tar.gz"
```

### Scénario 4 : Tests Jetables

```powershell
# Démarrer en mode mémoire (données temporaires)
.\scripts\manage-h2.ps1 start -Mode mem

# Tester l'application

# Arrêter (données automatiquement effacées)
.\scripts\manage-h2.ps1 stop

# Redémarrer avec base vide
.\scripts\manage-h2.ps1 start -Mode mem
```

### Scénario 5 : Reset Complet

```powershell
# Supprimer toutes les données et recommencer
.\scripts\manage-h2.ps1 reset

# Résultat :
# 🗑️  Reset de la base de données
# 
# ⚠️  ATTENTION: Cette opération va SUPPRIMER TOUTES les données !
#    - Tous les tickets
#    - Tous les produits
#    - Toutes les statistiques
# 
#    Êtes-vous sûr ? (SUPPRIMER pour confirmer): SUPPRIMER
# 
#    Arrêt de l'application...
# ✅ Application arrêtée
#    Suppression du volume...
# ✅ Volume supprimé
#    Redémarrage avec base vide...
```

---

## 🔍 Aide et Options

### Voir l'Aide

```powershell
# Aide PowerShell native
Get-Help .\scripts\manage-h2.ps1

# Voir les paramètres
Get-Help .\scripts\manage-h2.ps1 -Parameter Action
```

### Paramètres Disponibles

| Paramètre | Valeurs | Description |
|-----------|---------|-------------|
| `Action` | start, stop, restart, status, backup, restore, reset, logs, console | Action à exécuter |
| `Mode` | file, mem | Mode de persistance (file = sauvegardé, mem = temporaire) |
| `BackupFile` | nom_fichier.tar.gz | Nom du fichier de backup |

---

## 🚨 Résolution de Problèmes

### Problème 1 : "docker-compose : command not found"

**Solution :**
```powershell
# Vérifier que Docker Desktop est démarré
# Ouvrir Docker Desktop manuellement

# Vérifier l'installation
docker --version
docker-compose --version
```

### Problème 2 : "Cannot load module docker-compose"

**Solution :**
```powershell
# Le script utilise docker-compose en ligne de commande
# S'assurer que Docker Desktop est installé et démarré
```

### Problème 3 : "Permission denied"

**Solution :**
```powershell
# Exécuter PowerShell en tant qu'administrateur
# Ou utiliser le bypass :
powershell -ExecutionPolicy Bypass -File .\scripts\manage-h2.ps1 start
```

### Problème 4 : Le script ne fait rien

**Vérifier :**
```powershell
# 1. Être dans le bon répertoire
pwd
# Devrait afficher : C:\Users\pheni\IdeaProjects\TicketCompare

# 2. Le fichier existe
Test-Path .\scripts\manage-h2.ps1
# Devrait afficher : True

# 3. Docker est démarré
docker ps
# Devrait afficher la liste des conteneurs
```

---

## 📱 Utilisation sur Terminal Intégré (VS Code / IntelliJ)

### VS Code

1. Ouvrir le terminal intégré : `` Ctrl+` ``
2. S'assurer d'être en PowerShell (pas CMD ou Git Bash)
3. Exécuter :
   ```powershell
   .\scripts\manage-h2.ps1 start
   ```

### IntelliJ IDEA

1. Ouvrir le terminal : `Alt+F12`
2. Changer en PowerShell si nécessaire
3. Exécuter :
   ```powershell
   .\scripts\manage-h2.ps1 start
   ```

---

## 🎨 Personnalisation

### Modifier les Valeurs par Défaut

Éditer le script `manage-h2.ps1` :

```powershell
# Ligne 6-14 : Changer les valeurs par défaut
param(
    [Parameter(Position=0)]
    [string]$Action = 'status',  # ← Changer en 'status' pour voir l'état par défaut
    
    [Parameter()]
    [string]$Mode = 'file',      # ← 'file' ou 'mem'
    
    [Parameter()]
    [string]$BackupFile = "backup-$(Get-Date -Format 'yyyy-MM-dd').tar.gz"  # ← Format personnalisé
)
```

---

## ✅ Résumé des Commandes

```powershell
# Navigation
cd C:\Users\pheni\IdeaProjects\TicketCompare

# Commandes principales
.\scripts\manage-h2.ps1 start              # Démarrer (mode persistant)
.\scripts\manage-h2.ps1 start -Mode mem    # Démarrer (mode mémoire)
.\scripts\manage-h2.ps1 stop               # Arrêter
.\scripts\manage-h2.ps1 restart            # Redémarrer
.\scripts\manage-h2.ps1 status             # Voir le statut
.\scripts\manage-h2.ps1 logs               # Voir les logs
.\scripts\manage-h2.ps1 backup             # Faire un backup
.\scripts\manage-h2.ps1 restore -BackupFile "fichier.tar.gz"  # Restaurer
.\scripts\manage-h2.ps1 reset              # Reset complet
.\scripts\manage-h2.ps1 console            # Ouvrir H2 Console
```

---

## 🎯 Commande la Plus Simple

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\scripts\manage-h2.ps1 start
```

**C'est tout ! L'application démarre avec persistance des données.** 🚀

---

**Date : 27 Décembre 2024**  
**Script : manage-h2.ps1**  
**Statut : ✅ Prêt à l'emploi**

