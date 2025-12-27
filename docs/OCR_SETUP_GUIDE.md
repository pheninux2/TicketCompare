# Scanner OCR de Tickets - Guide d'Installation et d'Utilisation

## 📋 Vue d'ensemble

Le système de scanner OCR de TicketCompare permet de :
- **Photographier** un ticket de caisse avec votre webcam
- **Importer** une image de ticket depuis votre ordinateur
- **Charger** un ticket depuis un chemin local
- **Extraire automatiquement** tous les produits, prix et catégories
- **Analyser** le ticket pour créer automatiquement un enregistrement en base de données

## 🔧 Installation

### 1. Windows

#### Installation de Tesseract OCR
Tesseract est nécessaire pour la reconnaissance optique de caractères (OCR).

**Téléchargement:**
- Téléchargez l'installateur : https://github.com/UB-Mannheim/tesseract/wiki
- Choisissez la version la plus récente (recommandé : tesseract-ocr-w64-setup-v5.x.x.exe)

**Installation:**
1. Lancez l'installateur
2. Acceptez les termes de la licence
3. Lors de l'installation, assurez-vous de sélectionner la langue française (fra)
4. Choisissez le dossier d'installation (par défaut : C:\Program Files\Tesseract-OCR)
5. Complétez l'installation

**Vérification:**
```bash
# Ouvrez PowerShell et vérifiez
tesseract --version
# Vous devriez voir la version de Tesseract
```

**Configuration Windows (Important):**
Ajoutez Tesseract au chemin du système :
1. Clic droit sur "Ce PC" → Propriétés
2. Cliquez sur "Paramètres système avancés"
3. Cliquez sur "Variables d'environnement"
4. Sous "Variables système", ajoutez ou modifiez `Path`
5. Ajoutez : `C:\Program Files\Tesseract-OCR`
6. Redémarrez l'IDE (IntelliJ IDEA)

### 2. macOS

```bash
# Installation via Homebrew
brew install tesseract

# Installation des langues (français)
brew install tesseract-lang
```

### 3. Linux (Ubuntu/Debian)

```bash
# Installation
sudo apt-get update
sudo apt-get install tesseract-ocr
sudo apt-get install libtesseract-dev

# Installation des langues (français)
sudo apt-get install tesseract-ocr-fra
```

## 🚀 Utilisation

### Accès au Scanner

1. Allez à : `http://localhost:8080/tickets`
2. Cliquez sur le bouton **"Scanner Ticket"** (🎥 caméra)
3. Choisissez l'une des trois méthodes :

### Méthode 1: Photographier avec la Webcam
```
1. Cliquez sur "Photographier"
2. Cliquez sur "Activer Caméra"
3. Autorisez l'accès à la caméra
4. Positionnez correctement le ticket
5. Cliquez sur "Capturer"
6. Ajoutez optionnellement le magasin et des notes
7. Cliquez sur "Analyser le Ticket"
```

### Méthode 2: Importer une Image
```
1. Cliquez sur "Importer une Image"
2. Glissez-déposez l'image OU cliquez pour parcourir
3. Sélectionnez votre image de ticket
4. L'aperçu s'affiche automatiquement
5. Ajoutez optionnellement le magasin et des notes
6. Cliquez sur "Analyser le Ticket"
```

### Méthode 3: Charger depuis le Disque
```
1. Cliquez sur "Chemin Local"
2. Entrez le chemin complet du fichier
   Exemple: C:\Users\pheni\Documents\mon-ticket.jpg
3. Ajoutez optionnellement le magasin et des notes
4. Cliquez sur "Analyser le Ticket"
```

## 📊 Résultat du Scan

Après l'analyse, vous verrez :
- ✅ Nombre de produits détectés
- 💰 Montant total calculé
- 📅 Date du scan
- 📦 Liste complète des produits avec :
  - Nom du produit
  - Catégorie (détectée automatiquement)
  - Prix unitaire
  - Quantité
  - Prix total

## 🤖 Intelligence OCR

Le système :
- **Extrait le texte** avec Tesseract OCR (français)
- **Parse les prix** en format français (12,50€)
- **Détecte les quantités** (kg, L, pièces, etc.)
- **Classe automatiquement** les produits dans les catégories :
  - 🥛 Laitier
  - 🍖 Viande
  - 🐟 Poisson
  - 🥕 Fruits & Légumes
  - 🍝 Féculents
  - 🍫 Confiserie
  - 🍪 Biscuiterie
  - 🥫 Condiments
  - ☕ Boissons

## ⚠️ Conseils Importants

### Pour de Meilleurs Résultats :

1. **Éclairage** : Photographiez le ticket sous une bonne lumière
2. **Angle** : Prenez le ticket de face, pas en biais
3. **Netteté** : Assurez-vous que le texte est net et lisible
4. **Complétude** : Essayez de capturer le ticket entier
5. **Propreté** : Les tickets propres donnent de meilleurs résultats

### Limitations :

- L'OCR peut ne pas extraire **100% des produits** avec précision
- Les **petits textes** peuvent être mal reconnus
- Les **tickets endommagés** ou **mal scannés** peuvent être incorrects
- Les **prix** peuvent nécessiter une vérification

### Correction Manuelle :

Après le scan, vous pouvez :
1. Cliquer sur "Éditer" pour modifier les produits
2. Ajouter des produits manquants manuellement
3. Corriger les prix ou quantités mal détectés

## 🔍 Dépannage

### Erreur : "Tesseract not found"
- Vérifiez que Tesseract est installé
- Redémarrez l'IDE après l'installation
- Vérifiez que le chemin est correct dans PATH

### Erreur : "Langue fra non trouvée"
- Téléchargez les données linguistiques
- Windows : Re-lancez l'installateur Tesseract et sélectionnez fra
- Linux/Mac : `sudo apt-get install tesseract-ocr-fra`

### Erreur : "Fichier trop volumineux"
- Le fichier dépasse 10MB
- Compressez ou réduisez la résolution de l'image

### Caméra ne fonctionne pas
- Vérifiez les permissions du navigateur
- Essayez une autre méthode (importer une image)
- Testez avec une autre application

## 📱 Formats Supportés

**Images :** JPG, PNG, BMP, GIF, TIFF, WebP
**Taille maximale :** 10 MB
**Résolution :** Plus c'est high-res, mieux c'est

## 🎯 Intégration avec TicketCompare

Une fois le ticket créé, vous pouvez :
- ✅ Voir les statistiques par catégorie
- ✅ Comparer les prix entre magasins
- ✅ Analyser la consommation hebdomadaire
- ✅ Prédire l'évolution des prix
- ✅ Générer des rapports

## 📚 Technologies Utilisées

- **Tesseract OCR** : Reconnaissance optique de caractères
- **Java ImageIO** : Traitement d'images
- **Spring Boot** : Framework web
- **Thymeleaf** : Templates HTML
- **Bootstrap 5** : Interface utilisateur
- **HTMX** : Interactions web modernes

---

**Besoin d'aide ?** Consultez la documentation complète dans le fichier GUIDE_COMPLET.md

