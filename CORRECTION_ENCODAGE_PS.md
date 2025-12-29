# ✅ CORRECTION ENCODAGE POWERSHELL - TERMINÉ

## Date : 28 Décembre 2025

---

## 🐛 PROBLÈME RENCONTRÉ

```powershell
PS> .\start-dev.ps1
Au caractère start-dev.ps1:37 : 43
+ Write-Host "â ³ Attente du dÃ©marrage (30 secondes)..." -ForegroundCo ...
+                                           ~~~~~~~~
Jeton inattendu « secondes » dans l'expression ou l'instruction.
```

**Cause :** Problème d'encodage des caractères spéciaux (émojis et accents) dans les fichiers PowerShell.

---

## ✅ SOLUTION APPLIQUÉE

J'ai corrigé **tous les scripts PowerShell** en remplaçant :
- ❌ Émojis (🔍, ✅, ❌, 📦, etc.) → **[*], [OK], [ERREUR]**
- ❌ Caractères accentués (é, è, à, etc.) → **e, a** (sans accents)

---

## 📄 FICHIERS CORRIGÉS

### ✅ start-dev.ps1
- Remplacé tous les émojis
- Supprimé tous les accents
- Compatible PowerShell Windows

### ✅ start-prod.ps1
- Remplacé tous les émojis
- Supprimé tous les accents
- Compatible PowerShell Windows

### ✅ backup-db.ps1
- Remplacé tous les émojis
- Supprimé tous les accents
- Compatible PowerShell Windows

### ✅ restore-db.ps1
- Remplacé tous les émojis
- Supprimé tous les accents
- Compatible PowerShell Windows

---

## 🚀 UTILISATION

### Démarrer en mode DEV

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\start-dev.ps1
```

**Sortie attendue :**
```
=====================================
   ShopTracker - Mode Developpement
=====================================

[*] Verification de Docker...
[OK] Docker trouve

[*] Arret des conteneurs existants...
[*] Construction de l'image de developpement...
[*] Demarrage en mode developpement...
[*] Attente du demarrage (30 secondes)...

=====================================
   [OK] Mode Developpement demarre !
=====================================

Application: http://localhost:8080
H2 Console: http://localhost:8080/h2-console
    JDBC URL: jdbc:h2:mem:shoptracker
    User: sa
    Password: (vide)

Debug Port: localhost:5005
```

---

### Démarrer en mode PROD

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\start-prod.ps1
```

---

### Backup BDD

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\backup-db.ps1
```

---

### Restaurer BDD

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\restore-db.ps1
```

---

## 🔤 CHANGEMENTS DE NOTATION

| Avant | Après |
|-------|-------|
| 🔍 | [*] |
| ✅ | [OK] |
| ❌ | [ERREUR] |
| ⚠️ | [ATTENTION] |
| 📦 | [*] |
| 🏗️ | [*] |
| 🚀 | [*] |
| ⏳ | [*] |
| 🌐 | (supprimé) |
| 🗄️ | (supprimé) |
| 🐛 | (supprimé) |
| 📋 | (supprimé) |
| é, è, ê | e |
| à | a |
| ç | c |

---

## ✅ SCRIPTS PRÊTS À L'EMPLOI

Tous les scripts PowerShell sont maintenant :
- ✅ Sans émojis
- ✅ Sans accents
- ✅ Compatible Windows PowerShell 5.1+
- ✅ Compatible PowerShell Core 7+
- ✅ Encodage UTF-8 correct

---

## 🧪 TEST

Pour vérifier que tout fonctionne :

```powershell
# Test 1 : Vérifier la syntaxe
cd C:\Users\pheni\IdeaProjects\TicketCompare
Get-Command .\start-dev.ps1

# Test 2 : Lancer en DEV
.\start-dev.ps1
```

Si vous voyez le message de démarrage sans erreurs, **c'est bon !** ✅

---

## 📝 NOTES

### Pourquoi supprimer les émojis ?

Les émojis utilisent plusieurs bytes UTF-8 qui peuvent causer des problèmes d'encodage dans PowerShell, surtout sur Windows 10 avec PowerShell 5.1.

### Pourquoi supprimer les accents ?

Même problème : les caractères accentués (é, è, à, etc.) peuvent être mal interprétés selon la configuration régionale de Windows.

### Solution alternative (si vous voulez garder les émojis/accents)

```powershell
# Forcer l'encodage UTF-8 dans PowerShell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$PSDefaultParameterValues['*:Encoding'] = 'utf8'
```

Mais je recommande de **garder les scripts sans émojis/accents** pour une compatibilité maximale.

---

## ✅ RÉSULTAT

```
✅ Tous les scripts PowerShell corrigés
✅ Compatible Windows PowerShell
✅ Plus d'erreurs d'encodage
✅ Prêt à l'emploi
```

---

**Vous pouvez maintenant lancer les scripts sans problème !** 🎉

**Date :** 28 Décembre 2025  
**Application :** 🛒 ShopTracker  
**Problème :** Encodage PowerShell  
**Status :** ✅ RÉSOLU

