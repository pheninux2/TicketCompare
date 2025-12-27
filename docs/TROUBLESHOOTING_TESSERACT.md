# 🔧 TROUBLESHOOTING TESSERACT - Erreur "Invalid memory access"

## ⚠️ PROBLÈME: "Invalid memory access" lors de l'OCR

### 🎯 Cause Racine

Cette erreur signifie que Tesseract ne trouve pas les données linguistiques (tessdata) ou qu'elles ne sont pas accessibles.

```
Error: Invalid memory access
  at com.sun.jna.Native.invokePointer(Native.Method)
  at net.sourceforge.tess4j.Tesseract.doOCR(Tesseract.java:415)
```

---

## ✅ SOLUTIONS

### Solution 1: Installer Tesseract sur Windows (Recommandé)

#### Étape 1: Télécharger l'Installateur
1. Aller sur: https://github.com/UB-Mannheim/tesseract/wiki
2. Télécharger: `tesseract-ocr-w64-setup-v5.x.x.exe` (dernière version)

#### Étape 2: Installer
1. Lancer le `.exe`
2. Accepter la licence
3. **⚠️ IMPORTANT: Sélectionner la langue FRANÇAISE (fra)**
   - Cliquer sur "French"
   - Vérifier que la case est cochée
4. Dossier d'installation: `C:\Program Files\Tesseract-OCR`
5. Cliquer "Finish"

#### Étape 3: Ajouter au PATH (TRÈS IMPORTANT)

**Méthode 1: Via l'interface Windows**
1. Appuyer sur `Windows + R`
2. Taper: `sysdm.cpl`
3. Cliquer "Paramètres système avancés"
4. Cliquer "Variables d'environnement"
5. Sous "Variables système", cliquer "Nouveau"
   - Nom: `Path`
   - Valeur: `C:\Program Files\Tesseract-OCR`
6. Cliquer OK × 3

**Méthode 2: Via PowerShell (Admin)**
```powershell
$path = [Environment]::GetEnvironmentVariable("Path", "User")
$newPath = "$path;C:\Program Files\Tesseract-OCR"
[Environment]::SetEnvironmentVariable("Path", $newPath, "User")
```

#### Étape 4: Vérifier l'Installation
Ouvrir une **nouvelle** fenêtre PowerShell:
```bash
tesseract --version
```

Vous devriez voir:
```
tesseract 5.x.x
leptonica-1.8.x
...
```

#### Étape 5: Redémarrer IntelliJ IDEA
```
File → Exit
(Attendre quelques secondes)
Relancer IntelliJ
```

#### Étape 6: Relancer l'Application
```bash
mvn clean
mvn spring-boot:run
```

---

### Solution 2: Vérifier le Chemin Tesseract (Si installé)

Si vous avez déjà Tesseract, vérifier le chemin:

```bash
# Windows: Trouver où Tesseract est installé
where tesseract
# Output: C:\Program Files\Tesseract-OCR\tesseract.exe
```

Vérifier que `C:\Program Files\Tesseract-OCR\tessdata` existe:
- Ouvrir l'Explorateur
- Aller à: `C:\Program Files\Tesseract-OCR`
- Voir si le dossier `tessdata` est présent
- Voir si le fichier `fra.traineddata` est présent

---

### Solution 3: Vérifier les Données Linguistiques

#### Windows
```
C:\Program Files\Tesseract-OCR\tessdata\fra.traineddata
```
Si ce fichier n'existe pas:
1. Réinstaller Tesseract
2. **Sélectionner français** pendant l'installation
3. Vérifier que `tessdata` existe avec `fra.traineddata`

#### macOS
```bash
ls /usr/local/share/tessdata/ | grep fra
# ou
ls /opt/homebrew/share/tessdata/ | grep fra
```
Si absent:
```bash
brew install tesseract-lang
```

#### Linux
```bash
ls /usr/share/tesseract-ocr/4.00/tessdata/ | grep fra
# ou
ls /usr/share/tesseract-ocr/tessdata/ | grep fra
```
Si absent:
```bash
sudo apt-get install tesseract-ocr-fra
```

---

### Solution 4: Désactiver OCR Temporairement (Fallback)

Si vous ne pouvez pas installer Tesseract, le système propose un mode fallback:

1. Importer quand même une image
2. Remplir manuellement les produits
3. Les produits seront créés manuellement

Le service a été amélioré pour gérer les erreurs Tesseract gracieusement.

---

## 🔍 DIAGNOSTIC COMPLET

### 1. Vérifier l'Installation Tesseract
```bash
# Windows
tesseract --version
# Output should show: tesseract 5.x.x

# Si erreur "tesseract not recognized":
# → Tesseract n'est pas installé
# → OU le PATH n'est pas mis à jour
# → Solution: Réinstaller et redémarrer IntelliJ
```

### 2. Vérifier le Chemin
```bash
# Windows
echo %PATH%
# Chercher: C:\Program Files\Tesseract-OCR

# Si absent: Ajouter via sysdm.cpl
```

### 3. Vérifier les Données Linguistiques
```bash
# Windows
dir "C:\Program Files\Tesseract-OCR\tessdata"
# Chercher: fra.traineddata

# Si absent: Réinstaller en sélectionnant français
```

### 4. Tester Tesseract Directement
```bash
# Créer un test.txt avec du texte français
tesseract test.jpg test.txt -l fra
```

Si cela marche, mais que l'app ne marche pas:
- IntelliJ n'a pas rechargé les variables d'environnement
- Solution: Redémarrer complètement IntelliJ

---

## 📋 CHECKLIST DÉPANNAGE

- [ ] Tesseract téléchargé depuis https://github.com/UB-Mannheim/tesseract/wiki
- [ ] Installer le fichier `.exe`
- [ ] **Français (fra) sélectionné** pendant l'installation
- [ ] Dossier `tessdata` existe: `C:\Program Files\Tesseract-OCR\tessdata`
- [ ] Fichier `fra.traineddata` existe
- [ ] PATH contient: `C:\Program Files\Tesseract-OCR`
- [ ] `tesseract --version` fonctionne en PowerShell
- [ ] IntelliJ IDEA complètement relancé
- [ ] Application relancée: `mvn spring-boot:run`
- [ ] Essayer le scanner: http://localhost:8080/tickets/scan

---

## 🎯 ÉTAPES RAPIDES (5 min)

1. **Télécharger**: https://github.com/UB-Mannheim/tesseract/wiki
2. **Installer**: Exe + Sélectionner FRANÇAIS
3. **Ajouter PATH**: `C:\Program Files\Tesseract-OCR`
4. **Redémarrer**: IntelliJ IDEA complètement
5. **Relancer**: `mvn spring-boot:run`
6. **Tester**: http://localhost:8080/tickets/scan

---

## 💡 CONSEILS IMPORTANT

⚠️ **Ne pas oublier:**
- Tesseract doit être installé **AVANT** de lancer l'app
- Le français (fra) doit être sélectionné
- IntelliJ doit être complètement redémarré après installation Tesseract
- Le PATH doit être mis à jour et pris en compte
- La nouvelle fenêtre PowerShell doit être utilisée après ajout au PATH

---

## 🆘 SI CELA NE MARCHE TOUJOURS PAS

### Option 1: Vérifier les Logs

Regarder les logs IntelliJ:
```
View → Tool Windows → Services
Ou: View → Tool Windows → Run
```

Chercher les messages:
```
Tesseract data found at: ...
Tesseract configured with OCR engine mode: TESSERACT_ONLY
```

### Option 2: Recherche Manuelle

Vérifier où Tesseract est installé:
```bash
# Chercher le dossier tessdata
dir /s /b tessdata
# Si trouvé à un autre endroit, utiliser ce chemin
```

### Option 3: Installation Alternative

Sur Windows, installer via Chocolatey:
```bash
choco install tesseract
```

Puis vérifier le chemin d'installation et l'ajouter au PATH.

---

## 📞 CONTACTS & RESSOURCES

- **Installation Officielle**: https://github.com/UB-Mannheim/tesseract/wiki
- **Forum Tesseract**: https://groups.google.com/g/tesseract-ocr
- **Tess4j (Java)**: https://github.com/nguyenq/tess4j

---

## ✅ VÉRIFICATION FINALE

Après correction:

1. Ouvrir: http://localhost:8080/tickets/scan
2. Cliquer "Importer une Image"
3. Sélectionner une image de ticket
4. Cliquer "Analyser le Ticket"
5. ✅ Voir les produits extraits

Si cela marche → Tesseract est correctement installé! 🎉

---

**Créé:** 20 Décembre 2025  
**Last Update:** Version 1.0  
**Status:** Guide complet pour résoudre l'erreur OCR

