# ✅ CORRECTION TRANSFER-TO-VPS.PS1 - TERMINÉ

## Date : 30 Décembre 2025

---

## 🐛 PROBLÈMES RENCONTRÉS

### 1. Références de variables invalides
```powershell
# ERREUR
Write-Host "VPS: $VPS_USER@$VPS_IP:$VPS_PORT"
#                          ^ PowerShell pense que c'est un lecteur (C:, D:)
```

**Erreur :** `La référence de variable n'est pas valide. «:» n'est pas suivi d'un caractère de nom de variable valide.`

### 2. Séparateur && invalide
```powershell
# ERREUR
$sshCommand = "cd $VPS_SCRIPTS_DIR && chmod +x *.sh && ls -la *.sh"
#                                   ^^ && n'existe pas en PowerShell
```

**Erreur :** `Le jeton « && » n'est pas un séparateur d'instruction valide.`

### 3. Caractères accentués
```powershell
# ERREUR
Write-Host "Pour exécuter l'installation:"
#                ^  Problème d'encodage
```

**Erreur :** `Le terminateur ' est manquant dans la chaîne.`

---

## ✅ CORRECTIONS APPLIQUÉES

### 1. Variables avec délimiteurs `${}`
```powershell
# AVANT
Write-Host "VPS: $VPS_USER@$VPS_IP:$VPS_PORT"

# APRÈS
Write-Host "VPS: ${VPS_USER}@${VPS_IP}:${VPS_PORT}"
```

**Pourquoi ?** PowerShell pense que `$VPS_IP:$VPS_PORT` est un chemin de lecteur. Utiliser `${}` délimite clairement la variable.

---

### 2. Séparateur `;` au lieu de `&&`
```powershell
# AVANT
$sshCommand = "cd $VPS_SCRIPTS_DIR && chmod +x *.sh && ls -la *.sh"

# APRÈS
$sshCommand = "cd $VPS_SCRIPTS_DIR ; chmod +x *.sh ; ls -la *.sh"
```

**Pourquoi ?** 
- `&&` = Bash/Linux (exécute la commande suivante seulement si la précédente réussit)
- `;` = Séparateur de commandes universel (fonctionne aussi dans SSH/Bash)

---

### 3. Suppression des accents
```powershell
# AVANT
Write-Host "Pour exécuter l'installation:"
Write-Host "[OK] $file transféré"
Write-Host "Vérifier que le dossier..."

# APRÈS
Write-Host "Pour executer l'installation:"
Write-Host "[OK] $file transfere"
Write-Host "Verifier que le dossier..."
```

**Pourquoi ?** Les accents (é, è, à) causent des problèmes d'encodage dans PowerShell.

---

### 4. Mise à jour du chemin utilisateur
```powershell
# AVANT
$LOCAL_DEPLOY_DIR = "C:\Users\MHA25660\IdeaProjects\TicketCompare\deploy"

# APRÈS
$LOCAL_DEPLOY_DIR = "C:\Users\pheni\IdeaProjects\TicketCompare\deploy"
```

---

## 📄 FICHIER CORRIGÉ

Le script `transfer-to-vps.ps1` est maintenant :
- ✅ Sans erreurs de syntaxe PowerShell
- ✅ Sans accents problématiques
- ✅ Compatible Windows PowerShell 5.1+
- ✅ Prêt à transférer des fichiers vers le VPS

---

## 🚀 UTILISATION

### Prérequis

1. **OpenSSH installé** (pour `scp` et `ssh`)
   ```powershell
   # Vérifier si OpenSSH est installé
   Get-Command ssh
   Get-Command scp
   ```

2. **Dossier `deploy/` créé** avec les scripts
   ```
   C:\Users\pheni\IdeaProjects\TicketCompare\deploy\
   ├── setup-vps.sh
   ├── deploy-app.sh
   ├── update-app.sh
   ├── backup.sh
   ├── restore.sh
   ├── monitor.sh
   ├── docker-compose.prod.yml
   └── .env.production.template
   ```

3. **Accès SSH au VPS** configuré
   - IP : 178.128.162.253
   - Port : 443
   - User : root

---

### Exécution

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\transfer-to-vps.ps1
```

**Le script va :**
1. ✅ Vérifier que le dossier `deploy/` existe
2. ✅ Lister les fichiers à transférer
3. ✅ Demander confirmation
4. ✅ Transférer chaque fichier via SCP
5. ✅ Rendre les scripts `.sh` exécutables sur le VPS
6. ✅ Afficher un résumé

---

## 📊 EXEMPLE DE SORTIE

```
=========================================
   Transfert des Scripts vers VPS
=========================================

VPS: root@178.128.162.253:443
Dossier local: C:\Users\pheni\IdeaProjects\TicketCompare\deploy
Dossier VPS: /opt/shoptracker/scripts

Fichiers a transferer:
  ✓ setup-vps.sh
  ✓ deploy-app.sh
  ✓ update-app.sh
  ✓ backup.sh
  ✓ restore.sh
  ✓ monitor.sh
  ✓ docker-compose.prod.yml
  ✓ .env.production.template

Continuer le transfert? (O/n): O

[*] Transfert en cours...

[*] Transfert de setup-vps.sh...
[OK] setup-vps.sh transfere
[*] Transfert de deploy-app.sh...
[OK] deploy-app.sh transfere
...

=========================================
   Resume du Transfert
=========================================

Reussis: 8
Echoues: 0

[*] Rendre les scripts executables sur le VPS...
-rwxr-xr-x 1 root root  2547 Dec 30 10:30 setup-vps.sh
-rwxr-xr-x 1 root root  1823 Dec 30 10:30 deploy-app.sh
...

[OK] Scripts prets sur le VPS!

Pour executer l'installation:
  ssh -p 443 root@178.128.162.253
  cd /opt/shoptracker/scripts
  ./setup-vps.sh
```

---

## 🔍 VÉRIFICATION AVANT TRANSFERT

### 1. Créer le dossier deploy (si besoin)
```powershell
New-Item -ItemType Directory -Force -Path "C:\Users\pheni\IdeaProjects\TicketCompare\deploy"
```

### 2. Vérifier OpenSSH
```powershell
# Installer OpenSSH si nécessaire
Add-WindowsCapability -Online -Name OpenSSH.Client~~~~0.0.1.0
```

### 3. Tester la connexion SSH
```powershell
ssh -p 443 root@178.128.162.253
```

---

## 🐛 DÉPANNAGE

### Erreur : "scp : Le terme 'scp' n'est pas reconnu"
**Solution :** Installer OpenSSH Client
```powershell
Add-WindowsCapability -Online -Name OpenSSH.Client~~~~0.0.1.0
```

### Erreur : "Permission denied (publickey)"
**Solution :** Utiliser un mot de passe ou configurer une clé SSH
```powershell
# Avec mot de passe (sera demandé)
scp -P 443 fichier.txt root@178.128.162.253:/opt/shoptracker/scripts/
```

### Erreur : "[ERREUR] Le dossier deploy n'existe pas!"
**Solution :** Créer le dossier et y placer les scripts
```powershell
New-Item -ItemType Directory -Force -Path "C:\Users\pheni\IdeaProjects\TicketCompare\deploy"
```

---

## ✅ RÉSULTAT

```
✅ Toutes les erreurs PowerShell corrigées
✅ Syntaxe compatible Windows
✅ Séparateurs de commandes corrects (;)
✅ Variables délimitées correctement (${}))
✅ Sans accents problématiques
✅ Prêt à transférer vers le VPS
```

---

## 🎯 PROCHAINES ÉTAPES

1. **Créer le dossier deploy** (si pas encore fait)
2. **Placer les scripts VPS** dans `deploy/`
3. **Exécuter le transfert** avec `.\transfer-to-vps.ps1`
4. **Se connecter au VPS** et exécuter `./setup-vps.sh`

---

**Date :** 30 Décembre 2025  
**Application :** 🛒 ShopTracker  
**Script :** transfer-to-vps.ps1  
**Status :** ✅ CORRIGÉ ET PRÊT

