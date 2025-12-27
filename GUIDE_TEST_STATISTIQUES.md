# 🧪 Guide de Test - Statistiques par Catégorie

## 🎯 Objectif

Vérifier que le graphique des statistiques par catégorie affiche maintenant les vraies données.

## ✅ Pré-requis

- Application TicketCompare installée
- Au moins quelques produits dans la base de données
- Plusieurs catégories avec des produits

## 📋 Procédure de Test

### Étape 1 : Redémarrer l'Application

#### Option A : Via IntelliJ IDEA (Recommandé)

1. **Arrêter** l'application
   - Cliquer sur le bouton Stop rouge ⏹️
   - Ou `Ctrl+F2`

2. **Rebuild** le projet
   - Menu : `Build` → `Rebuild Project`
   - Ou `Ctrl+Shift+F9`
   - Attendre la fin de la compilation

3. **Redémarrer** l'application
   - Cliquer sur le bouton Run vert ▶️
   - Ou `Shift+F10`

#### Option B : Via Docker

```bash
cd C:\Users\pheni\IdeaProjects\TicketCompare
docker-compose down
docker-compose up --build
```

### Étape 2 : Accéder au Dashboard Statistiques

1. **Ouvrir le navigateur**
   - URL : http://localhost:8080/statistics/dashboard

2. **Vérifier la page dashboard**
   - ✅ Une carte par catégorie doit s'afficher
   - ✅ Chaque carte affiche : Nombre de produits, Prix moyen, Min, Max
   - ✅ Les prix sont formatés en euros (€X.XX)

### Étape 3 : Tester une Catégorie avec Données

1. **Cliquer sur "Fruits & Légumes"** (ou toute catégorie qui contient des produits)

2. **Vérifier l'affichage de la page :**

   **a) Les 4 cartes en haut doivent afficher :**
   - ✅ Nombre de produits (ex: 15)
   - ✅ Prix moyen (ex: €1.25)
   - ✅ Prix le plus bas en vert (ex: €0.50)
   - ✅ Prix le plus haut en rouge (ex: €3.00)

   **b) Le graphique doit afficher :**
   - ✅ Titre : "Prix des produits (Top 15)"
   - ✅ Barres de couleurs :
     - 🔵 Bleu = Prix Moyen
     - 🟢 Vert = Prix Min
     - 🔴 Rouge = Prix Max
   - ✅ Noms de produits sur l'axe X (ex: Citron, Tomate, Orange...)
   - ✅ Prix en euros sur l'axe Y (ex: €0.00, €0.50, €1.00...)
   - ✅ Au moins 3 barres visibles (si vous avez au moins 1 produit)

   **c) Le tableau en dessous doit afficher :**
   - ✅ Liste complète des produits de la catégorie
   - ✅ Colonnes : Produit, Prix Min, Prix Moyen, Prix Max, Observations
   - ✅ Prix formatés en euros
   - ✅ Badges colorés pour le prix moyen
   - ✅ Tri par prix moyen décroissant

3. **Tester l'interactivité du graphique :**
   - ✅ Survoler une barre → Tooltip affiche "Prix Moyen: €X.XX"
   - ✅ La légende en haut montre les 3 types de prix
   - ✅ Cliquer sur la légende cache/affiche les barres

### Étape 4 : Tester Plusieurs Catégories

Répéter l'Étape 3 pour chaque catégorie :
- ✅ Fruits & Légumes
- ✅ Laitier
- ✅ Boulangerie
- ✅ Viande
- ✅ Autre

**Vérifier que :**
- Chaque catégorie affiche SES propres produits
- Les graphiques sont différents pour chaque catégorie
- Les statistiques (min, max, moyenne) correspondent aux produits affichés

### Étape 5 : Tester une Catégorie Vide

1. **Accéder à une catégorie sans produits** (si elle existe)
   - Ex: Une catégorie créée mais vide

2. **Vérifier l'affichage :**
   - ✅ Message : "Aucune donnée statistique disponible pour cette catégorie"
   - ✅ Pas de graphique affiché
   - ✅ Pas de tableau affiché
   - ✅ Bouton "Retour au tableau de bord" fonctionne

### Étape 6 : Test de Performance

1. **Tester avec une catégorie contenant beaucoup de produits** (ex: 50+)

2. **Vérifier :**
   - ✅ Le graphique affiche seulement le Top 15
   - ✅ Le tableau affiche TOUS les produits
   - ✅ La page se charge en moins de 2 secondes
   - ✅ Pas d'erreur JavaScript dans la console (F12)

## 🐛 Problèmes Possibles et Solutions

### Problème 1 : Graphique Vide (Pas de Barres)

**Symptômes :**
- Le graphique s'affiche mais sans barres
- Axes visibles mais pas de données

**Solutions :**
1. Ouvrir la console JavaScript (F12)
2. Vérifier s'il y a des erreurs
3. Vérifier que `products` n'est pas vide :
   ```javascript
   console.log(products); // Dans la console
   ```
4. Si `products` est vide, vérifier la base de données

### Problème 2 : Erreur "Cannot resolve method 'getCategoryProducts'"

**Symptômes :**
- L'application ne démarre pas
- Erreur de compilation

**Solutions :**
1. Faire un **Clean Build** dans IntelliJ
   - Menu : `Build` → `Clean Project`
   - Puis : `Build` → `Rebuild Project`
2. Invalider le cache IntelliJ
   - Menu : `File` → `Invalidate Caches / Restart`
3. Redémarrer IntelliJ

### Problème 3 : Données Incorrectes dans le Graphique

**Symptômes :**
- Les prix affichés ne correspondent pas à la réalité
- Calculs erronés

**Solutions :**
1. Vérifier la base de données directement
2. Recatégoriser les produits (bouton sur /consumption/weekly)
3. Vérifier les logs de l'application pour des erreurs

### Problème 4 : Graphique Déformé

**Symptômes :**
- Le graphique est écrasé ou étiré
- Difficilement lisible

**Solutions :**
1. Vérifier que la div conteneur a bien `height: 500px`
2. Rafraîchir la page avec Ctrl+F5 (vider le cache)
3. Tester sur un autre navigateur

## ✅ Critères de Validation

Le test est **RÉUSSI** si :

1. ✅ Les 4 cartes affichent des nombres cohérents
2. ✅ Le graphique affiche au moins 3 barres de couleur
3. ✅ Les noms de produits sont lisibles sur l'axe X
4. ✅ Les tooltips fonctionnent au survol
5. ✅ Le tableau liste tous les produits avec leurs prix
6. ✅ Différentes catégories affichent différentes données
7. ✅ Pas d'erreur JavaScript dans la console (F12)
8. ✅ Le bouton "Retour au tableau de bord" fonctionne

## 📊 Exemple de Résultat Attendu

### Pour "Fruits & Légumes" avec 5 produits

**Cartes :**
```
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ 5 produits   │ │ €1.30        │ │ €0.70        │ │ €2.00        │
│              │ │ Prix moyen   │ │ Prix le plus │ │ Prix le plus │
│              │ │              │ │ bas          │ │ haut         │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
```

**Graphique :**
```
Prix des produits (Top 15)

€2.00 ┤        ███
€1.50 ┤    █   ███   █
€1.00 ┤  █ █ █ ███ █ █
€0.50 ┤█ █ █ █ ███ █ █ █
€0.00 ┴─────────────────
      C T O P B
      i o r a o
      t m a n m
      r a n a m
      o t g n e
      n e e e

🔵 Prix Moyen   🟢 Prix Min   🔴 Prix Max
```

**Tableau :**
```
┌────────────────┬─────────┬────────────┬─────────┬──────────────┐
│ Produit        │ Prix Min│ Prix Moyen │ Prix Max│ Observations │
├────────────────┼─────────┼────────────┼─────────┼──────────────┤
│ Tomate cerise  │ €1.50   │ €1.75      │ €2.00   │      3       │
│ Orange à jus   │ €1.20   │ €1.35      │ €1.50   │      4       │
│ Citron 500g    │ €0.89   │ €0.99      │ €1.10   │      5       │
│ Pomme golden   │ €0.80   │ €0.95      │ €1.10   │      6       │
│ Banane         │ €0.70   │ €0.85      │ €1.00   │      8       │
└────────────────┴─────────┴────────────┴─────────┴──────────────┘
```

## 📝 Rapport de Test

Après avoir effectué tous les tests, remplissez ce rapport :

```
Date du test : ___/___/2024
Testeur : _________________

Résultats :
☐ Étape 1 : Redémarrage OK
☐ Étape 2 : Dashboard OK
☐ Étape 3 : Graphique catégorie OK
☐ Étape 4 : Plusieurs catégories OK
☐ Étape 5 : Catégorie vide OK
☐ Étape 6 : Performance OK

Problèmes rencontrés :
_________________________________
_________________________________

Notes :
_________________________________
_________________________________

Statut final : ☐ RÉUSSI  ☐ ÉCHOUÉ
```

## 🆘 Support

Si le test échoue :
1. Consulter `docs/CORRECTION_STATISTIQUES_CATEGORIE.md` pour les détails techniques
2. Vérifier les logs de l'application
3. Vérifier la console JavaScript (F12) pour des erreurs
4. S'assurer que la base de données contient des produits

---

**Bonne chance pour les tests ! 🚀**

