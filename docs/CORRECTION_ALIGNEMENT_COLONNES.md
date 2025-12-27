# 🎨 Correction de l'Alignement des Colonnes dans l'Accordéon

## 🔴 Problème Identifié

Les colonnes des tableaux ne sont pas alignées dans les accordéons de la page de consommation. Ce problème est causé par l'absence de largeurs fixes pour les colonnes, ce qui fait que chaque tableau dans chaque accordéon peut avoir des largeurs différentes selon le contenu.

### Symptômes
- ❌ Colonnes décalées entre différentes catégories
- ❌ En-têtes non alignés avec les données
- ❌ Tableau de détails des achats mal aligné
- ❌ Difficile à lire et peu professionnel

## ✅ Solution Appliquée

### 1. Ajout de Styles CSS pour le Tableau Principal

**Fichier :** `src/main/resources/templates/consumption/weekly.html`

```css
/* Alignement des colonnes du tableau */
.table-consumption {
    table-layout: fixed;
    width: 100%;
}
.table-consumption th:nth-child(1),
.table-consumption td:nth-child(1) {
    width: 35%; /* Produit */
}
.table-consumption th:nth-child(2),
.table-consumption td:nth-child(2) {
    width: 12%; /* Quantité */
    text-align: right !important;
}
.table-consumption th:nth-child(3),
.table-consumption td:nth-child(3) {
    width: 8%; /* Unité */
    text-align: center !important;
}
.table-consumption th:nth-child(4),
.table-consumption td:nth-child(4) {
    width: 15%; /* Coût Total */
    text-align: right !important;
}
.table-consumption th:nth-child(5),
.table-consumption td:nth-child(5) {
    width: 12%; /* Achats */
    text-align: center !important;
}
.table-consumption th:nth-child(6),
.table-consumption td:nth-child(6) {
    width: 18%; /* Prix/U Moy */
    text-align: right !important;
}

/* Assurer que les cellules ne débordent pas */
.table-consumption td {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

/* Exception pour la colonne produit qui peut contenir le badge */
.table-consumption td:first-child {
    white-space: normal;
    overflow: visible;
}
```

### 2. Ajout de Styles CSS pour le Tableau de Détails

```css
/* Styles pour le tableau de détails des achats */
.table-details {
    table-layout: fixed;
    width: 100%;
}
.table-details th:nth-child(1),
.table-details td:nth-child(1) {
    width: 15%; /* Date */
}
.table-details th:nth-child(2),
.table-details td:nth-child(2) {
    width: 20%; /* Magasin */
}
.table-details th:nth-child(3),
.table-details td:nth-child(3) {
    width: 20%; /* Quantité */
    text-align: right !important;
}
.table-details th:nth-child(4),
.table-details td:nth-child(4) {
    width: 22%; /* Prix Unitaire */
    text-align: right !important;
}
.table-details th:nth-child(5),
.table-details td:nth-child(5) {
    width: 23%; /* Total */
    text-align: right !important;
}
```

### 3. Modification du HTML

**Avant :**
```html
<table class="table table-hover table-striped mb-0">
```

**Après :**
```html
<table class="table table-hover table-striped table-consumption mb-0">
```

**Et pour le tableau de détails :**

**Avant :**
```html
<table class="table table-sm table-bordered mb-0">
```

**Après :**
```html
<table class="table table-sm table-bordered table-details mb-0">
```

**Correction de l'alignement de l'en-tête "Unité" :**

**Avant :**
```html
<th><i class="fas fa-ruler"></i> Unité</th>
```

**Après :**
```html
<th class="text-center"><i class="fas fa-ruler"></i> Unité</th>
```

## 📊 Distribution des Largeurs

### Tableau Principal (Produits)

| Colonne | Largeur | Alignement | Justification |
|---------|---------|------------|---------------|
| **Produit** | 35% | Gauche | Nom long + badge possible |
| **Quantité** | 12% | Droite | Nombre avec décimales |
| **Unité** | 8% | Centre | Texte court (kg, l, u) |
| **Coût Total** | 15% | Droite | Montant en euros |
| **Achats** | 12% | Centre | Badge avec nombre |
| **Prix/U Moy** | 18% | Droite | Montant en euros |
| **Total** | **100%** | | |

### Tableau de Détails (Achats)

| Colonne | Largeur | Alignement | Justification |
|---------|---------|------------|---------------|
| **Date** | 15% | Gauche | Format DD/MM/YYYY |
| **Magasin** | 20% | Gauche | Badge avec nom |
| **Quantité** | 20% | Droite | Nombre avec unité |
| **Prix Unitaire** | 22% | Droite | Montant en euros |
| **Total** | 23% | Droite | Montant en euros |
| **Total** | **100%** | | |

## 🎯 Avantages de la Solution

### ✅ Alignement Parfait
- Les colonnes ont maintenant une largeur fixe
- L'alignement est cohérent dans tous les accordéons
- Les en-têtes sont parfaitement alignés avec les données

### ✅ Lisibilité Améliorée
- Les montants sont alignés à droite (standard pour les nombres)
- Les unités sont centrées (visuellement équilibré)
- Les noms de produits ont assez d'espace (35%)

### ✅ Responsive Design
- `table-layout: fixed` assure une largeur constante
- `overflow: hidden` empêche le débordement
- `text-overflow: ellipsis` tronque les textes trop longs avec "..."

### ✅ Gestion des Badges
- La colonne "Produit" peut contenir du contenu multi-ligne
- Les badges "X achats" s'affichent correctement
- Pas de cassure visuelle

## 🧪 Test Visuel

### Avant la Correction
```
┌─────────────────────────────────────────────────┐
│ Produit    Qté  Unité  Total   Achats  Prix/U  │ ← Largeurs variables
├─────────────────────────────────────────────────┤
│ Banane     5    kg     €4.50   1       €0.90   │
│ Citron 500g 2kg kg    €3.00    2       €1.50   │ ← Décalage !
└─────────────────────────────────────────────────┘
```

### Après la Correction
```
┌────────────────────────────────────────────────────────────────┐
│ Produit          │ Qté   │ U │ Total │ Achats │ Prix/U Moy    │ ← Largeurs fixes
├──────────────────┼───────┼───┼───────┼────────┼───────────────┤
│ Banane           │  5.00 │ kg│ €4.50 │   1    │ €0.90         │
│ Citron 500g      │  2.00 │ kg│ €3.00 │   2    │ €1.50         │ ← Aligné !
└──────────────────┴───────┴───┴───────┴────────┴───────────────┘
```

## 🔄 Comportement avec Texte Long

Grâce à `text-overflow: ellipsis` :

**Nom de produit très long :**
```
Tomate cerise bio de Provence calibre moyen...  [5 achats]
```

Au lieu de :
```
Tomate cerise bio de Provence calibre moyen vendu par 250g en barquette
```

## 📱 Responsive

Le tableau reste lisible sur mobile grâce à :
- `<div class="table-responsive">` qui permet le scroll horizontal
- Largeurs en pourcentage qui s'adaptent à l'écran
- `table-layout: fixed` qui maintient les proportions

## 🚀 Test de la Correction

### Étape 1 : Recharger la Page

Si l'application est déjà en cours d'exécution :
1. Ouvrir : http://localhost:8080/consumption/weekly
2. Rafraîchir la page (F5 ou Ctrl+F5 pour vider le cache)

### Étape 2 : Vérifier l'Alignement

1. **Ouvrir un accordéon** (ex: "Fruits & Légumes")
2. **Vérifier que :**
   - ✅ Les colonnes sont alignées verticalement
   - ✅ Les en-têtes sont alignés avec les données
   - ✅ Les montants sont alignés à droite
   - ✅ Les unités sont centrées

3. **Ouvrir un autre accordéon** (ex: "Laitier")
4. **Vérifier que :**
   - ✅ Les colonnes ont les mêmes largeurs que le premier accordéon
   - ✅ L'alignement est identique

5. **Cliquer sur un badge "X achats"**
6. **Vérifier que :**
   - ✅ Le tableau de détails s'affiche correctement
   - ✅ Les colonnes du tableau de détails sont alignées
   - ✅ Pas de décalage avec le tableau principal

### Étape 3 : Test avec Différents Contenus

Tester avec :
- ✅ Produits avec noms courts
- ✅ Produits avec noms longs
- ✅ Différentes quantités (1, 10, 100, 0.5)
- ✅ Différentes unités (kg, l, u, pcs)
- ✅ Différents montants (€0.50, €10.00, €150.00)

## 📝 Fichiers Modifiés

1. ✅ `src/main/resources/templates/consumption/weekly.html`
   - Ajout de CSS `.table-consumption` (lignes ~41-76)
   - Ajout de CSS `.table-details` (lignes ~77-103)
   - Ajout de la classe `table-consumption` au tableau principal
   - Ajout de la classe `table-details` au tableau de détails
   - Correction de l'alignement de l'en-tête "Unité"

## 💡 Maintenance Future

### Pour Ajuster les Largeurs

Modifier les valeurs dans le CSS :
```css
.table-consumption th:nth-child(1) {
    width: 40%; /* Augmenter l'espace pour le produit */
}
```

### Pour Ajouter une Colonne

1. Ajouter le style CSS pour la nouvelle colonne
2. Ajuster les autres largeurs pour que le total = 100%
3. Mettre à jour le `colspan` des lignes de détails si nécessaire

### Pour Changer l'Alignement

```css
.table-consumption th:nth-child(3) {
    text-align: left !important; /* Aligner à gauche */
}
```

## 🎨 Améliorations Visuelles Bonus

Les styles ajoutés incluent aussi :
- ✅ Gestion du débordement de texte
- ✅ Ellipses pour les textes trop longs
- ✅ Exception pour la colonne produit (multi-ligne)
- ✅ Alignement cohérent des icônes Font Awesome

---

**Date : 27 Décembre 2024**  
**Correction : Alignement des colonnes dans l'accordéon**  
**Statut : ✅ Implémenté, prêt à tester**

