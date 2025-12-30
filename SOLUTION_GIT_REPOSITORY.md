# ✅ SOLUTION - "fatal: not a git repository"

## 🐛 Problème identifié

```
fatal: not a git repository (or any of the parent directories): .git
```

**Causes :**
1. Le dossier `/opt/shoptracker/app` existe mais n'est pas un repo Git
2. Les fichiers ont été transférés via SCP au lieu d'être clonés
3. Le script essayait de faire `git pull` sans vérifier si c'est un repo Git

---

## ✅ CORRECTIONS APPLIQUÉES

### 1. Script deploy-app.sh corrigé
- ✅ Vérifie maintenant si `.git` existe avant de faire `git pull`
- ✅ Supprime et reclone si ce n'est pas un repo Git
- ✅ Retours à la ligne convertis (Windows → Linux)

### 2. Tous les scripts .sh convertis
- ✅ Enlève les `\r\n` (Windows) → `\n` (Linux)
- ✅ Évite l'erreur "required file not found"

---

## 🚀 SOLUTION RAPIDE

### Méthode 1 : Retransférer le script corrigé (RECOMMANDÉ)

**Sur votre PC Windows :**

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\retransfer-deploy.ps1
```

**Sur le VPS :**

```bash
su - deployer
cd /opt/shoptracker/scripts && ./deploy-app.sh
```

---

### Méthode 2 : Corriger directement sur le VPS

**Sur le VPS :**

```bash
# 1. Supprimer le dossier app qui n'est pas un repo Git
rm -rf /opt/shoptracker/app

# 2. Corriger les retours à la ligne
cd /opt/shoptracker/scripts
sed -i 's/\r$//' *.sh
chmod +x *.sh

# 3. Relancer le déploiement
su - deployer
cd /opt/shoptracker/scripts && ./deploy-app.sh
```

Le script va maintenant demander l'URL du repository GitHub et cloner proprement.

---

## 📋 Quand le script demande l'URL GitHub

```
[INFO] Entrez l'URL de votre repository GitHub:
```

**Tapez l'URL de votre repository :**
```
https://github.com/pheninux2/TicketCompare.git
```

ou si c'est un repo privé :
```
https://votre-username:votre-token@github.com/pheninux2/TicketCompare.git
```

---

## 🔍 Vérification après le clone

Le script va :
1. ✅ Cloner le repository dans `/opt/shoptracker/app`
2. ✅ Copier et configurer `.env.production`
3. ✅ Configurer Nginx
4. ✅ Builder l'image Docker
5. ✅ Démarrer l'application

---

## 🐛 Si vous voyez encore des erreurs

### Erreur : "Permission denied"
```bash
# Donner les droits au user deployer
sudo chown -R deployer:deployer /opt/shoptracker
```

### Erreur : "command not found"
```bash
# Vérifier que Git est installé
git --version

# Si pas installé
sudo apt-get update
sudo apt-get install -y git
```

---

## ✅ Résultat attendu

Après correction, vous devriez voir :

```
=========================================
   ShopTracker - Déploiement           
=========================================

[*] Vérification des prérequis...
[OK] Prérequis vérifiés

=========================================
   Clonage du Repository                
=========================================

[ATTENTION] Le dossier existe mais n'est pas un repository Git
[*] Suppression et clone du repository...
[*] Clonage du repository...
[INFO] Entrez l'URL de votre repository GitHub:
https://github.com/pheninux2/TicketCompare.git

Cloning into '/opt/shoptracker/app'...
[OK] Code récupéré

=========================================
   Configuration de l'environnement     
=========================================
...
```

---

## 📝 Fichiers corrigés

- ✅ `deploy-app.sh` - Logique de détection Git améliorée
- ✅ Tous les `.sh` - Retours à la ligne Linux
- ✅ `retransfer-deploy.ps1` - Script de retransfert créé

---

## 🎯 ACTIONS IMMÉDIATES

**Choisissez une méthode :**

### Option A : Retransférer (plus simple)
```powershell
# Sur Windows
.\retransfer-deploy.ps1
```

### Option B : Corriger sur place
```bash
# Sur le VPS
rm -rf /opt/shoptracker/app
cd /opt/shoptracker/scripts
sed -i 's/\r$//' *.sh
chmod +x *.sh
```

**Puis relancez le déploiement !**

---

**Date :** 30 Décembre 2025  
**Problème :** fatal: not a git repository  
**Status :** ✅ CORRIGÉ

