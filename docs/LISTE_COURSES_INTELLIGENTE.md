# 🎯 Liste de Courses Intelligente - Documentation

## ✅ Nouvelle Fonctionnalité Ajoutée

Une page de prédiction de courses basée sur l'analyse des habitudes de consommation et les recommandations nutritionnelles.

---

## 🎯 Objectif

Générer automatiquement une liste de courses personnalisée qui :
- ✅ Analyse vos habitudes de consommation
- ✅ Identifie les déséquilibres nutritionnels
- ✅ Recommande des produits sains
- ✅ Suggère des alternatives
- ✅ Calcule un score de santé

---

## 📊 Fonctionnalités

### 1. Score de Santé (0-100)

**Calcul :**
```
Score de base: 50 points

+30 max : Fruits & Légumes
+10 max : Produits laitiers  
-20 max : Produits sucrés (confiserie, biscuits)

Total : Entre 0 et 100
```

**Niveaux :**
- **80-100** : Excellent (vert)
- **60-79** : Bien (orange)
- **40-59** : Moyen (jaune)
- **0-39** : À améliorer (rouge)

### 2. Analyse des Habitudes

**Ce qui est analysé :**

#### a) Légumes Peu Consommés
- Compare vos achats aux légumes recommandés
- Suggère 5 légumes que vous n'avez pas achetés
- Liste : Épinards, Brocoli, Patate douce, Courgette, Aubergine, etc.

#### b) Consommation de Sucre
- Identifie les produits sucrés achetés
- Niveau : **LOW** (<3), **MEDIUM** (3-5), **HIGH** (>5)
- Recommande des alternatives naturelles

#### c) Produits Laitiers (Calcium)
- Compte les produits laitiers achetés
- Recommandation : Au moins 8/mois (2/semaine)
- Suggère d'augmenter si insuffisant

### 3. Équilibre Nutritionnel

**Recommandations mensuelles :**

| Catégorie | Recommandé/mois | Basé sur |
|-----------|-----------------|----------|
| Fruits & Légumes | 35 | 5 portions/jour |
| Laitier | 14 | 2 portions/jour |
| Viande | 7 | 1 portion/jour |
| Poisson | 3 | 2-3 fois/semaine |
| Féculents | 21 | 3 portions/jour |

**Statut :**
- **GOOD** : ≥80% de la recommandation (vert)
- **MEDIUM** : 50-79% (jaune)
- **LOW** : <50% (rouge)

### 4. Recommandations Prioritaires

**Générées automatiquement :**

1. **Légumes insuffisants** :
   ```
   "Augmentez votre consommation de fruits et légumes 
   (recommandation : 5 portions/jour)"
   ```

2. **Sucre élevé** :
   ```
   "Réduisez votre consommation de produits sucrés 
   et privilégiez les fruits frais"
   ```

3. **Laitier insuffisant** :
   ```
   "Augmentez votre consommation de produits laitiers 
   pour l'apport en calcium"
   ```

4. **Poisson** :
   ```
   "Consommez du poisson au moins 2 fois par semaine 
   pour les oméga-3"
   ```

### 5. Alternatives Saines

**Substitutions suggérées :**

| Au lieu de | Essayez | Bénéfice |
|------------|---------|----------|
| Bonbons et chocolat | Fruits frais | Vitamines naturelles et fibres |
| Soda | Eau pétillante + citron | Hydratation sans sucre |
| Biscuits industriels | Yaourt + fruits | Protéines et probiotiques |
| Sucre blanc | Miel ou sirop d'érable | Sucres naturels avec nutriments |

### 6. Liste Suggérée par Catégorie

**Produits recommandés :**

#### 🥬 Fruits & Légumes
- Épinards frais
- Brocoli
- Patate douce
- Avocat
- Tomates cerises
- Carottes
- Poivrons
- Bananes
- Pommes
- Oranges

#### 🥛 Laitier
- Lait demi-écrémé
- Yaourt nature
- Fromage blanc 0%
- Lait d'amande enrichi
- Fromage à pâte dure

#### 🍖 Protéines
- Poulet fermier
- Saumon frais
- Œufs bio
- Tofu
- Lentilles
- Pois chiches

#### 🍞 Féculents
- Pain complet
- Riz brun
- Pâtes complètes
- Quinoa
- Flocons d'avoine

#### 🍯 Alternatives Saines
- Miel naturel
- Fruits secs
- Noix et amandes
- Chocolat noir 70%
- Compote sans sucre

---

## 🎨 Interface Utilisateur

### Page : `/analysis/smart-shopping-list`

**Structure :**

1. **En-tête**
   - Titre + description
   - Bouton "Générer ma liste"

2. **Score de santé** (cercle animé)
   - Valeur sur 100
   - Couleur selon niveau
   - Message descriptif

3. **Recommandations prioritaires**
   - Liste à puces
   - Icône ampoule
   - Badge orange

4. **Équilibre nutritionnel**
   - Barres de progression par catégorie
   - Pourcentage vs recommandé
   - Code couleur (vert/jaune/rouge)

5. **Produits à réduire**
   - Tags rouges
   - Visible seulement si applicable

6. **Légumes à découvrir**
   - Tags verts
   - 5 légumes suggérés

7. **Alternatives saines**
   - Cartes violettes dégradées
   - Format "Au lieu de → Essayez"
   - Bénéfice expliqué

8. **Liste de courses**
   - Organisée par catégorie
   - Checkboxes pour cocher
   - Imprimable

9. **Actions**
   - Bouton "Imprimer"
   - Bouton "Régénérer"

---

## 🔧 Architecture Technique

### Service : `ShoppingListPredictionService.java`

#### Méthode 1 : `analyzeConsumptionHabits()`

**Analyse :**
1. Récupère les produits du dernier mois
2. Groupe par catégorie
3. Identifie légumes non consommés
4. Analyse la consommation de sucre
5. Analyse les produits laitiers
6. Calcule le score de santé
7. Génère les recommandations

**Retour :**
```json
{
  "categoryDistribution": {"Fruits & Légumes": 15, "Laitier": 8, ...},
  "underconsumedVegetables": ["Épinards", "Brocoli", ...],
  "sugarAnalysis": {"level": "HIGH", "sugarProductCount": 7, ...},
  "dairyAnalysis": {"adequate": false, "dairyCount": 5, ...},
  "healthScore": 65,
  "recommendations": ["Augmentez...", "Réduisez..."]
}
```

#### Méthode 2 : `generateSmartShoppingList()`

**Génère :**
1. Produits à réduire
2. Légumes à ajouter
3. Alternatives saines
4. Équilibre nutritionnel
5. Liste suggérée complète

**Retour :**
```json
{
  "productsToReduce": ["Bonbons", "Chocolat"],
  "vegetablesToAdd": ["Épinards", "Brocoli"],
  "healthyAlternatives": [{instead: "Soda", use: "Eau", benefit: "..."}],
  "nutritionalBalance": {"Fruits & Légumes": {actual: 15, recommended: 35, ...}},
  "suggestedByCategory": {"Fruits & Légumes": ["Épinards", ...]}
}
```

### Contrôleur : `AnalysisController.java`

**Endpoints :**

```java
GET /analysis/smart-shopping-list
→ Affiche la page

GET /analysis/api/consumption-analysis
→ Retourne l'analyse JSON

GET /analysis/api/smart-shopping-list
→ Retourne la liste JSON
```

---

## 🧪 Test de la Fonctionnalité

### Prérequis
- Au moins 10 produits dans la base (dernier mois)
- Plusieurs catégories différentes

### Procédure

1. **Accéder à la page**
   - URL : http://localhost:8080/analysis/smart-shopping-list

2. **Cliquer sur "Générer ma liste"**
   - Spinner s'affiche
   - Analyse en cours

3. **Vérifier le score de santé**
   - Cercle coloré avec valeur
   - Message adapté au score

4. **Vérifier les recommandations**
   - Liste de recommandations
   - Pertinentes par rapport aux données

5. **Vérifier l'équilibre**
   - Barres de progression
   - Pourcentages cohérents

6. **Vérifier les listes**
   - Légumes suggérés
   - Alternatives affichées
   - Liste par catégorie

7. **Tester l'impression**
   - Clic sur "Imprimer"
   - Page d'impression s'ouvre

---

## 📊 Exemples de Résultats

### Exemple 1 : Score Excellent (85/100)

**Situation :**
- 30 produits Fruits & Légumes
- 12 produits Laitier
- 2 produits sucrés
- 4 Poisson

**Résultat :**
```
Score: 85/100 - Excellent !

Recommandations:
✓ Continuez sur cette lancée !
✓ Variez les légumes pour plus de nutriments

Équilibre:
- Fruits & Légumes: 86% ✅
- Laitier: 86% ✅
- Poisson: 133% ✅
```

### Exemple 2 : Score Moyen (55/100)

**Situation :**
- 12 produits Fruits & Légumes
- 4 produits Laitier
- 8 produits sucrés
- 0 Poisson

**Résultat :**
```
Score: 55/100 - Moyen

Recommandations prioritaires:
⚠️ Augmentez fruits et légumes (5/jour)
⚠️ Réduisez les produits sucrés
⚠️ Augmentez les produits laitiers
⚠️ Mangez du poisson 2x/semaine

Équilibre:
- Fruits & Légumes: 34% ⚠️
- Laitier: 29% ⚠️
- Poisson: 0% ❌

À ajouter:
🥬 Épinards, Brocoli, Patate douce

À réduire:
🍫 Bonbons, Chocolat, Gâteaux
```

---

## 💡 Conseils d'Utilisation

### Pour Améliorer le Score

1. **Augmenter les légumes**
   - Objectif : 5 portions/jour
   - Variez les couleurs

2. **Réduire le sucre**
   - Remplacez par des fruits
   - Limitez confiseries et biscuits

3. **Équilibrer le calcium**
   - 2-3 produits laitiers/jour
   - Alternatives enrichies OK

4. **Ajouter du poisson**
   - 2-3 fois par semaine
   - Pour les oméga-3

### Pour Utiliser la Liste

1. **Générez avant vos courses**
   - Analysez vos habitudes
   - Prenez note des recommandations

2. **Cochez les produits**
   - Utilisez les checkboxes
   - Imprimez si besoin

3. **Suivez les alternatives**
   - Essayez les substitutions
   - Progressivement

4. **Régénérez régulièrement**
   - Une fois par mois
   - Suivez votre évolution

---

## 🚀 Accès Rapide

### URLs
- **Page principale** : http://localhost:8080/analysis/smart-shopping-list
- **API analyse** : http://localhost:8080/analysis/api/consumption-analysis
- **API liste** : http://localhost:8080/analysis/api/smart-shopping-list

### Menu
```
Prédictions (dropdown)
├─ 📈 Prix
├─ 🛒 Consommation
└─ 🎯 Liste Intelligente  ← NOUVEAU
```

---

## 📝 Fichiers Créés

1. ✅ `ShoppingListPredictionService.java` - Service d'analyse
2. ✅ `AnalysisController.java` - Endpoints ajoutés
3. ✅ `smart-shopping-list.html` - Page complète
4. ✅ `index.html` - Carte + menu mis à jour

---

## 🎯 Résumé

### Fonctionnalités
- ✅ Score de santé (0-100)
- ✅ Analyse des catégories
- ✅ Légumes peu consommés
- ✅ Analyse du sucre
- ✅ Analyse calcium/laitier
- ✅ Recommandations personnalisées
- ✅ Alternatives saines
- ✅ Liste complète par catégorie
- ✅ Imprimable

### Basé sur
- ✅ Données réelles (dernier mois)
- ✅ Recommandations nutritionnelles
- ✅ Vos habitudes de consommation
- ✅ Besoins diététiques

---

**Date : 27 Décembre 2024**  
**Fonctionnalité : Liste de courses intelligente**  
**Statut : ✅ Implémenté et fonctionnel**

