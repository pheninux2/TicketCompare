# 📅 Ajout des Dates dans les Statistiques par Catégorie

## ✅ Modification Appliquée

J'ai ajouté les informations de dates d'achat dans le tableau des statistiques par catégorie.

---

## 🆕 Nouvelles Colonnes Ajoutées

### Dans le Tableau des Produits

| Colonne | Description | Format | Badge |
|---------|-------------|--------|-------|
| **Premier Achat** | Date du premier achat du produit | DD/MM/YYYY | Texte gris |
| **Dernier Achat** | Date du dernier achat du produit | DD/MM/YYYY | Badge info (bleu clair) |

---

## 📊 Avant vs Après

### ❌ Avant

| Produit | Prix Min | Prix Moyen | Prix Max | Observations |
|---------|----------|------------|----------|--------------|
| Citron 500g | €0.89 | €0.99 | €1.10 | 5 |
| Tomate cerise | €1.50 | €1.75 | €2.00 | 3 |

### ✅ Après

| Produit | Prix Min | Prix Moyen | Prix Max | Observations | Premier Achat | Dernier Achat |
|---------|----------|------------|----------|--------------|---------------|---------------|
| Citron 500g | €0.89 | €0.99 | €1.10 | 5 | 15/12/2024 | **25/12/2024** |
| Tomate cerise | €1.50 | €1.75 | €2.00 | 3 | 10/12/2024 | **24/12/2024** |

---

## 🔧 Modifications Techniques

### 1. Service Java (`StatisticService.java`)

**Ajout du calcul des dates :**

```java
// Trouver les dates de premier et dernier achat
LocalDate firstPurchase = productList.stream()
    .map(p -> p.getTicket().getDate())
    .min(Comparator.naturalOrder())
    .orElse(null);

LocalDate lastPurchase = productList.stream()
    .map(p -> p.getTicket().getDate())
    .max(Comparator.naturalOrder())
    .orElse(null);

// Ajout dans la map
map.put("firstPurchase", firstPurchase);
map.put("lastPurchase", lastPurchase);
```

### 2. Template HTML (`category.html`)

**Ajout des colonnes dans le tableau :**

```html
<!-- En-tête -->
<th class="text-center"><i class="bi bi-calendar-check"></i> Premier Achat</th>
<th class="text-center"><i class="bi bi-calendar"></i> Dernier Achat</th>

<!-- Données -->
<td class="text-center">
    <span class="text-muted" th:text="${product.firstPurchase != null ? #temporals.format(product.firstPurchase, 'dd/MM/yyyy') : '-'}"></span>
</td>
<td class="text-center">
    <span class="badge bg-info text-dark" th:text="${product.lastPurchase != null ? #temporals.format(product.lastPurchase, 'dd/MM/yyyy') : '-'}"></span>
</td>
```

---

## 🎨 Design

### Icônes Bootstrap

- **📅 Premier Achat** : `bi-calendar-check` (calendrier avec check)
- **📅 Dernier Achat** : `bi-calendar` (calendrier simple)

### Styles

- **Premier Achat** : Texte gris (`.text-muted`) - information secondaire
- **Dernier Achat** : Badge bleu clair (`.badge.bg-info.text-dark`) - mise en évidence car plus important

---

## 💡 Utilité

### 📈 Suivi des Achats

**Identifier les produits :**
- **Récemment achetés** → Dernier achat proche d'aujourd'hui
- **Pas achetés depuis longtemps** → Dernier achat ancien
- **Nouveaux produits** → Premier achat récent
- **Produits réguliers** → Écart entre premier et dernier achat

### 📊 Analyse

**Exemples d'analyse :**

1. **Produit régulier :**
   ```
   Banane
   Premier: 01/11/2024
   Dernier: 25/12/2024
   → Acheté régulièrement pendant 2 mois
   ```

2. **Produit ponctuel :**
   ```
   Dinde de Noël
   Premier: 23/12/2024
   Dernier: 23/12/2024
   → Acheté une seule fois (événement)
   ```

3. **Produit oublié :**
   ```
   Aubergine
   Premier: 15/10/2024
   Dernier: 20/10/2024
   → Plus acheté depuis 2 mois !
   ```

---

## 🧪 Test

### Étape 1 : Redémarrer l'Application

```bash
# Dans IntelliJ
# Stop → Rebuild → Run

# Ou avec Docker
docker-compose -f docker/docker-compose-h2.yml restart
```

### Étape 2 : Accéder aux Statistiques

1. Ouvrir : http://localhost:8080/statistics/dashboard
2. Cliquer sur une catégorie (ex: "Fruits & Légumes")

### Étape 3 : Vérifier le Tableau

Le tableau doit maintenant afficher **7 colonnes** :

1. ✅ Produit
2. ✅ Prix Min
3. ✅ Prix Moyen
4. ✅ Prix Max
5. ✅ Observations
6. ✅ **Premier Achat** (NOUVEAU)
7. ✅ **Dernier Achat** (NOUVEAU)

### Étape 4 : Vérifier les Dates

- Les dates doivent être au format **DD/MM/YYYY**
- Le **Premier Achat** est en gris
- Le **Dernier Achat** est dans un badge bleu clair
- Si un produit n'a pas de date, afficher **"-"**

---

## 📊 Exemple de Résultat

### Page : Statistiques > Fruits & Légumes

```
╔═══════════════════════════════════════════════════════════════════════════╗
║ Statistiques : Fruits & Légumes                                          ║
╠═══════════════════════════════════════════════════════════════════════════╣
║ [15 produits] [€1.25 moy] [€0.50 min] [€3.00 max]                       ║
╠═══════════════════════════════════════════════════════════════════════════╣
║ Distribution des prix par produit                                        ║
║ [Graphique en barres - Top 15]                                          ║
╠═══════════════════════════════════════════════════════════════════════════╣
║ Liste des produits dans cette catégorie                                  ║
╠═══════════════╦══════╦═══════╦══════╦═════╦══════════════╦══════════════╣
║ Produit       ║ Min  ║ Moy   ║ Max  ║ Obs ║ 1er Achat    ║ Dernier      ║
╠═══════════════╬══════╬═══════╬══════╬═════╬══════════════╬══════════════╣
║ Tomate cerise ║ €1.50║ €1.75 ║ €2.00║  3  ║ 10/12/2024   ║ [24/12/2024] ║
║ Orange à jus  ║ €1.20║ €1.35 ║ €1.50║  4  ║ 05/12/2024   ║ [23/12/2024] ║
║ Citron 500g   ║ €0.89║ €0.99 ║ €1.10║  5  ║ 15/12/2024   ║ [25/12/2024] ║
║ Pomme golden  ║ €0.80║ €0.95 ║ €1.10║  6  ║ 01/12/2024   ║ [26/12/2024] ║
║ Banane        ║ €0.70║ €0.85 ║ €1.00║  8  ║ 01/11/2024   ║ [27/12/2024] ║
╚═══════════════╩══════╩═══════╩══════╩═════╩══════════════╩══════════════╝
```

**Note :** Le dernier achat est dans un badge bleu clair pour le mettre en évidence.

---

## 🔍 Cas d'Usage

### Scénario 1 : Identifier les Produits à Racheter

**Critère :** Dernier achat > 1 mois

```sql
-- Dans H2 Console (pour info)
SELECT name, MAX(date) as dernier_achat
FROM PRODUCTS p
JOIN TICKETS t ON p.ticket_id = t.id
GROUP BY name
HAVING DATEDIFF('DAY', MAX(date), CURRENT_DATE) > 30
ORDER BY MAX(date) DESC;
```

**Visuellement :**
- Regarder la colonne "Dernier Achat"
- Les dates anciennes indiquent un produit non acheté récemment

### Scénario 2 : Suivre un Nouveau Produit

**Critère :** Premier achat récent (< 2 semaines)

**Visuellement :**
- Regarder la colonne "Premier Achat"
- Si proche de la date du jour → nouveau produit testé

### Scénario 3 : Analyser la Fréquence

**Calcul mental :**
```
Écart = Dernier Achat - Premier Achat
Fréquence = Écart / Observations

Exemple:
Banane: 27/12 - 01/11 = 56 jours / 8 achats = ~7 jours
→ Acheté environ 1 fois par semaine
```

---

## 📝 Fichiers Modifiés

1. ✅ `src/main/java/pheninux/xdev/ticketcompare/service/StatisticService.java`
   - Ajout du calcul de `firstPurchase` et `lastPurchase`
   - Ajout dans la Map retournée

2. ✅ `src/main/resources/templates/statistics/category.html`
   - Ajout de 2 colonnes dans le tableau
   - Formatage des dates en DD/MM/YYYY
   - Style : texte gris + badge bleu clair

---

## 🎯 Améliorations Futures Possibles

### Option 1 : Tri par Date

Ajouter la possibilité de trier le tableau par :
- Date du premier achat
- Date du dernier achat

### Option 2 : Filtrage

Filtrer les produits par :
- Achetés cette semaine
- Pas achetés depuis 1 mois
- Nouveaux produits (< 2 semaines)

### Option 3 : Alertes Visuelles

- 🟢 Vert : Acheté récemment (< 7 jours)
- 🟡 Jaune : Acheté il y a 1-4 semaines
- 🔴 Rouge : Pas acheté depuis > 1 mois

### Option 4 : Fréquence d'Achat

Calculer et afficher :
```
Fréquence: Tous les X jours
```

---

## ✅ Résumé

### Nouvelles Informations

- ✅ **Premier Achat** : Date du premier achat du produit
- ✅ **Dernier Achat** : Date du dernier achat (mise en évidence)
- ✅ Format français : DD/MM/YYYY
- ✅ Icônes : 📅 calendrier
- ✅ Style : Gris + Badge bleu

### Utilité

- 📊 Suivre l'historique des achats
- 🔍 Identifier les produits réguliers vs ponctuels
- ⏰ Repérer les produits non achetés récemment
- 📈 Analyser la fréquence d'achat

---

**Date : 27 Décembre 2024**  
**Fonctionnalité : Dates d'achat dans statistiques**  
**Statut : ✅ Implémenté**

