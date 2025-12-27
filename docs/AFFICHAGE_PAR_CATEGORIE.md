# 📊 Affichage par Catégorie - Page Consommation

## ✨ Nouvelle Fonctionnalité

La page Consommation affiche maintenant les produits **organisés par catégorie** avec un système d'accordéon.

## 🎯 Avantages

✅ **Organisation claire** : Produits groupés par catégorie  
✅ **Navigation facile** : Accordéons dépliables/repliables  
✅ **Sous-totaux** : Coût par catégorie visible immédiatement  
✅ **Total général** : Récapitulatif en bas de page  
✅ **Détails préservés** : Badge cliquable pour voir les achats multiples

---

## 📊 Apparence

### Vue Globale

```
╔═══════════════════════════════════════════════════════════╗
║ 📊 Produits Achetés par Catégorie [15 produits]         ║
╠═══════════════════════════════════════════════════════════╣
║                                                           ║
║ ▼ 🏷️ Fruits & Légumes [3 produits] [€15.50]            ║
║   ┌─────────────────────────────────────────────┐       ║
║   │ Tomates   │ 2.5kg │ kg │ €9.50  │ 2 │ €3.80 │       ║
║   │ Pommes    │ 1.0kg │ kg │ €3.00  │ 1 │ €3.00 │       ║
║   │ Salade    │ 1.0pc │ pc │ €3.00  │ 1 │ €3.00 │       ║
║   │ Sous-total: 3 produits           │ €15.50  │       ║
║   └─────────────────────────────────────────────┘       ║
║                                                           ║
║ ▶ 🏷️ Laitier [2 produits] [€9.00]                      ║
║                                                           ║
║ ▶ 🏷️ Boulangerie [4 produits] [€12.30]                 ║
║                                                           ║
║ ▶ 🏷️ Épicerie [5 produits] [€23.45]                    ║
║                                                           ║
╠═══════════════════════════════════════════════════════════╣
║ TOTAL GÉNÉRAL : €60.25                    15 achats     ║
╚═══════════════════════════════════════════════════════════╝
```

### Catégorie Ouverte

```
┌───────────────────────────────────────────────────────────┐
│ ▼ 🏷️ Laitier [3 produits] [€9.00]                       │
├───────────────────────────────────────────────────────────┤
│ Produit      │ Qté  │ Unité │ Coût  │ Achats │ Prix/U  │
├──────────────┼──────┼───────┼───────┼────────┼─────────┤
│ Lait         │ 6.00 │ L     │ €9.00 │   3    │ €1.50   │
│ [▼ 3 achats] │      │       │       │        │         │ ← Clic
├──────────────┴──────┴───────┴───────┴────────┴─────────┤
│ Détails des achats :                                    │
│ • 25/12 LIDL      : 2.00L à €1.50 = €3.00              │
│ • 20/12 Carrefour : 2.00L à €1.50 = €3.00              │
│ • 15/12 LIDL      : 2.00L à €1.50 = €3.00              │
├─────────────────────────────────────────────────────────┤
│ Sous-total Laitier : 3 produits │ €9.00 │ 3 achats    │
└─────────────────────────────────────────────────────────┘
```

---

## 🎨 Fonctionnement

### 1. Accordéon par Catégorie

Chaque catégorie est un accordéon cliquable :

**En-tête de catégorie :**
- 🏷️ **Icône catégorie**
- **Nom de la catégorie** (ex: "Fruits & Légumes")
- **Badge bleu** : Nombre de produits
- **Badge rouge** : Coût total de la catégorie

**Comportement :**
- ▼ **Ouvert** : Fond vert, affiche les produits
- ▶ **Fermé** : Fond blanc, masque les produits
- **Clic** : Ouvre/Ferme la catégorie

### 2. Tableau par Catégorie

Chaque catégorie contient un tableau avec :

**Colonnes :**
- 📦 **Produit** (avec badge d'achats multiples si applicable)
- ⚖️ **Quantité totale**
- 📏 **Unité**
- 💰 **Coût total** (en rouge)
- 🛒 **Nombre d'achats** (badge bleu)
- 🧮 **Prix moyen par unité**

**Pied de tableau :**
- **Sous-total** de la catégorie
- Nombre de produits
- Coût total
- Nombre d'achats

### 3. Détails des Achats

Si un produit est acheté plusieurs fois :
- Badge jaune : `[▼ X achats]`
- Clic → Déploie les détails
- Tableau des achats individuels

### 4. Total Général

Footer noir avec :
- 💰 **Total** : Somme de tous les coûts
- 🛒 **Achats** : Nombre total d'achats

---

## 🔧 Modifications Techniques

### 1. ConsumptionController.java

**Ajout du groupement par catégorie :**

```java
// Grouper par catégorie
var consumptionByCategory = consumption.stream()
    .collect(Collectors.groupingBy(
        item -> item.getCategory() != null ? item.getCategory() : "Non catégorisé",
        LinkedHashMap::new,
        Collectors.toList()
    ));

model.addAttribute("consumptionByCategory", consumptionByCategory);
```

### 2. weekly.html

**Structure avec accordéons Bootstrap :**

```html
<div class="accordion" id="categoryAccordion">
    <th:block th:each="categoryEntry : ${consumptionByCategory}">
        <div class="accordion-item">
            <!-- En-tête -->
            <h2 class="accordion-header">
                <button class="accordion-button" 
                        data-bs-toggle="collapse">
                    Catégorie + Badges
                </button>
            </h2>
            
            <!-- Contenu -->
            <div class="accordion-collapse collapse">
                <table>
                    <!-- Produits de la catégorie -->
                </table>
            </div>
        </div>
    </th:block>
</div>
```

### 3. CSS Personnalisé

```css
.accordion-button:not(.collapsed) {
    background-color: #198754; /* Vert quand ouvert */
    color: white;
}

.accordion-item {
    border: 2px solid #dee2e6;
    margin-bottom: 0.5rem;
    border-radius: 0.5rem;
}
```

---

## 📋 Structure des Données

### Modèle de Données

```
consumptionByCategory (Map)
├─ "Fruits & Légumes" (clé)
│  └─ List<ConsumptionDTO> (valeur)
│     ├─ Tomates
│     ├─ Pommes
│     └─ Salade
│
├─ "Laitier"
│  └─ List<ConsumptionDTO>
│     ├─ Lait (avec 3 achats)
│     └─ Yaourt
│
├─ "Boulangerie"
│  └─ List<ConsumptionDTO>
│     ├─ Pain
│     └─ Croissants
│
└─ "Non catégorisé"
   └─ List<ConsumptionDTO>
      └─ Produit sans catégorie
```

---

## 🎯 Exemple d'Utilisation

### Cas 1 : Vue d'Ensemble

**Action :** Charger la page `/consumption/weekly`

**Résultat :**
```
Fruits & Légumes [3] [€15.50] ▼ (ouvert par défaut)
Laitier [2] [€9.00] ▶
Boulangerie [4] [€12.30] ▶
Épicerie [5] [€23.45] ▶
```

### Cas 2 : Explorer une Catégorie

**Action :** Cliquer sur "Laitier"

**Résultat :**
```
Laitier [2] [€9.00] ▼
├─ Lait : 6.00L → €9.00 (3 achats)
└─ Sous-total : €9.00
```

### Cas 3 : Voir les Détails d'un Produit

**Action :** Cliquer sur le badge `[▼ 3 achats]` du Lait

**Résultat :**
```
Détails des achats :
├─ 25/12 LIDL      : 2.00L à €1.50 = €3.00
├─ 20/12 Carrefour : 2.00L à €1.50 = €3.00
└─ 15/12 LIDL      : 2.00L à €1.50 = €3.00
```

### Cas 4 : Filtrer par Recherche

**Action :** Rechercher "lait"

**Résultat :**
```
Laitier [1] [€9.00] ▼
└─ Lait : 6.00L → €9.00
```

Seule la catégorie "Laitier" s'affiche avec le produit "Lait".

---

## ✨ Avantages de l'Organisation par Catégorie

### Pour l'Analyse

✅ **Vision claire** : Voir immédiatement quelles catégories coûtent le plus  
✅ **Comparaison facile** : Comparer les dépenses par type de produit  
✅ **Budget** : Suivre les dépenses par catégorie  
✅ **Tendances** : Identifier les catégories de consommation

### Pour la Navigation

✅ **Rapide** : Trouver un produit en ouvrant sa catégorie  
✅ **Organisé** : Plus besoin de scroller dans une longue liste  
✅ **Focus** : Se concentrer sur une catégorie à la fois

### Exemples Pratiques

**Analyser les Fruits & Légumes :**
```
▼ Fruits & Légumes [€45.30 ce mois]
  → 15% du budget total
  → Catégorie la plus chère
```

**Budget par Catégorie :**
```
1. Fruits & Légumes : €45.30 (15%)
2. Viande           : €39.50 (13%)
3. Laitier          : €28.70 (10%)
4. Épicerie         : €67.20 (22%)
5. Boulangerie      : €22.15 (7%)
```

---

## 🎨 Design et Couleurs

### En-têtes d'Accordéon

**Fermé (▶)** :
- Fond : Blanc
- Texte : Noir
- Bordure : Grise

**Ouvert (▼)** :
- Fond : **Vert** (#198754)
- Texte : **Blanc**
- Badges : Blanc avec texte vert

### Badges

- **Nombre de produits** : Bleu (#0d6efd)
- **Coût total** : Rouge (#dc3545)
- **Achats multiples** : Jaune (#ffc107)

### Footer Total

- Fond : Noir (#212529)
- Texte : Blanc
- Total : **Jaune** (#ffc107)

---

## 🚀 Résultat Final

### Interface Complète

```
╔═══════════════════════════════════════════════════════════╗
║ 📊 Consommation de Produits - Décembre 2024             ║
╠═══════════════════════════════════════════════════════════╣
║ [Filtres : Période | Recherche | Tri]                   ║
╠═══════════════════════════════════════════════════════════╣
║ 📦 Produits Achetés par Catégorie [15]                  ║
╟───────────────────────────────────────────────────────────╢
║                                                           ║
║ ▼ 🏷️ Fruits & Légumes [3] [€15.50]                     ║
║   [Tableau des 3 produits + sous-total]                 ║
║                                                           ║
║ ▼ 🏷️ Laitier [2] [€9.00]                               ║
║   [Tableau des 2 produits + sous-total]                 ║
║                                                           ║
║ ▶ 🏷️ Boulangerie [4] [€12.30]                          ║
║                                                           ║
║ ▶ 🏷️ Épicerie [5] [€23.45]                             ║
║                                                           ║
╠═══════════════════════════════════════════════════════════╣
║ TOTAL GÉNÉRAL : €60.25                    15 achats     ║
╚═══════════════════════════════════════════════════════════╝
```

---

## 🎉 Résumé

✅ **Accordéons par catégorie** : Organisation claire et intuitive  
✅ **Sous-totaux** : Coût et nombre de produits par catégorie  
✅ **Total général** : Récapitulatif en bas  
✅ **Détails préservés** : Badge cliquable pour achats multiples  
✅ **Design soigné** : Couleurs, icônes, animations  
✅ **Responsive** : Fonctionne sur mobile et desktop

**Date : 26 Décembre 2024**  
**Fonctionnalité : Affichage par catégorie**  
**Statut : ✅ Implémenté**

