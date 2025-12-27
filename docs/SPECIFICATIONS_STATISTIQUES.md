# 📊 Spécifications - Page Statistiques Améliorée

## 🎯 Objectif

Créer une page de statistiques intelligente qui aide à analyser les habitudes d'achat et identifier les économies potentielles.

## ✨ Fonctionnalités Demandées

### 1. 📅 Filtres Temporels

**Période d'analyse :**
- ✅ Semaine actuelle
- ✅ Mois actuel
- ✅ Mois dernier
- ✅ 3 derniers mois
- ✅ Année actuelle
- ✅ Période personnalisée

### 2. 📈 Tri et Affichage des Produits

**Critères de tri :**
- Prix (croissant/décroissant)
- Date d'achat (récent/ancien)
- Fréquence d'achat
- Magasin
- Catégorie

**Affichage :**
```
Produit | Prix | Qté | Date | Magasin | Fréquence
--------------------------------------------------------
Lait    | 1.50€| 2L  | 20/12| Carrefour | 4x cette semaine
Pain    | 2.30€| 1   | 22/12| Lidl      | 2x ce mois
Tomates | 3.80€| 2kg | 23/12| Monoprix  | 1x ce mois
```

### 3. 🎨 Surlignement Intelligent

#### A. Surlignement par Fréquence

**Couleurs :**
- 🔴 **Rouge** : Acheté 3+ fois dans la semaine
- 🟠 **Orange** : Acheté 2 fois dans la semaine
- 🟡 **Jaune** : Acheté 1 fois dans la semaine
- 🟢 **Vert** : Moins fréquent

**CSS Classes :**
```css
.frequent-week { background-color: #ffebee; } /* Rouge clair */
.moderate-week { background-color: #fff3e0; } /* Orange clair */
.occasional { background-color: #fffde7; }     /* Jaune clair */
.rare { background-color: #e8f5e9; }          /* Vert clair */
```

#### B. Surlignement par Prix

**Pour chaque produit :**
- 🟢 **Vert** : Prix le plus bas (meilleure affaire)
- 🔴 **Rouge** : Prix le plus élevé (attention !)
- ⚪ **Blanc** : Prix moyen

**Exemple :**
```
Lait 1.45€ 🟢 (Lidl - Meilleur prix!)
Lait 1.50€    (Carrefour)
Lait 1.65€ 🔴 (Monoprix - Plus cher de 13%)
```

### 4. 📊 Statistiques Calculées

#### A. Par Produit

```
┌─────────────────────────────────────────┐
│ Lait Demi-Écrémé                        │
├─────────────────────────────────────────┤
│ Achats ce mois : 4 fois                 │
│ Prix moyen     : 1.52€                  │
│ Prix min       : 1.45€ (Lidl)          │
│ Prix max       : 1.65€ (Monoprix)      │
│ Écart          : 0.20€ (13%)           │
│ Meilleur jour  : Mardi (toujours 1.45€)│
│ Économie pot.  : 0.80€ si acheté chez  │
│                  le moins cher          │
└─────────────────────────────────────────┘
```

#### B. Globales

```
┌─────────────────────────────────────────┐
│ STATISTIQUES MOIS DE DÉCEMBRE           │
├─────────────────────────────────────────┤
│ Total dépensé      : 457.80€            │
│ Nombre de tickets  : 12                 │
│ Panier moyen       : 38.15€             │
│ Économies possibles: 23.40€ (5%)       │
│                                         │
│ Top 5 Dépenses                          │
│  1. Viande         : 89.50€             │
│  2. Fruits/Légumes : 67.20€             │
│  3. Laitier        : 45.30€             │
│  4. Épicerie       : 38.90€             │
│  5. Boulangerie    : 27.60€             │
└─────────────────────────────────────────┘
```

### 5. 🏪 Comparaison Magasins

```
Pour le même produit : Lait Demi-Écrémé

Magasin      | Prix | Écart vs meilleur
─────────────┼──────┼──────────────────
Lidl      🥇 | 1.45€| Meilleur prix
Carrefour    | 1.50€| +0.05€ (+3%)
Monoprix     | 1.65€| +0.20€ (+13%)
```

### 6. 📉 Alertes et Recommandations

**Alertes automatiques :**

```
⚠️ ALERTE PRIX
Tomates : +25% par rapport au mois dernier
Anciennement : 3.20€/kg
Maintenant   : 4.00€/kg
Suggestion   : Attendre ou changer de magasin

💡 RECOMMANDATION
Vous achetez du lait 4 fois par semaine à des prix variables.
Économie possible : 3.20€/mois
→ Achetez chez Lidl (toujours le moins cher pour ce produit)

🎯 BON PLAN
Pain complet : Prix stable à 2.30€
Meilleur jour : Jeudi (-10% chez Carrefour)
```

## 🖥️ Structure de la Page

### Layout Proposé

```
┌─────────────────────────────────────────┐
│ 📊 STATISTIQUES D'ACHAT                 │
├─────────────────────────────────────────┤
│                                         │
│ [Filtres]                               │
│ Période: [Mois actuel ▼]              │
│ Tri:     [Prix décroissant ▼]         │
│ Magasin: [Tous ▼]                     │
│ Catég.:  [Toutes ▼]                   │
│                                         │
├─────────────────────────────────────────┤
│                                         │
│ 📈 VUE D'ENSEMBLE                       │
│ [Graphiques et résumés]                │
│                                         │
├─────────────────────────────────────────┤
│                                         │
│ 🛒 PRODUITS ACHETÉS (12)               │
│                                         │
│ [Tableau trié avec surlignements]      │
│                                         │
├─────────────────────────────────────────┤
│                                         │
│ 💡 INSIGHTS ET RECOMMANDATIONS          │
│ [Alertes et suggestions]               │
│                                         │
└─────────────────────────────────────────┘
```

### Tableau Produits

```html
<table class="table table-hover">
  <thead>
    <tr>
      <th>Produit</th>
      <th>Prix <span class="sort-icon">⬍</span></th>
      <th>Quantité</th>
      <th>Date <span class="sort-icon">⬍</span></th>
      <th>Magasin</th>
      <th>Fréquence</th>
      <th>Comparaison</th>
    </tr>
  </thead>
  <tbody>
    <!-- Ligne avec surlignement fréquence élevée + prix élevé -->
    <tr class="frequent-week price-high">
      <td>🥛 Lait Demi-Écrémé</td>
      <td>1.65€ <span class="badge bg-danger">+13%</span></td>
      <td>1L</td>
      <td>23/12/2024</td>
      <td>Monoprix</td>
      <td><span class="badge bg-warning">4x/semaine</span></td>
      <td>
        <button class="btn btn-sm btn-outline-primary">Voir détails</button>
      </td>
    </tr>
    
    <!-- Ligne avec prix optimal -->
    <tr class="price-best">
      <td>🥛 Lait Demi-Écrémé</td>
      <td>1.45€ <span class="badge bg-success">Meilleur</span></td>
      <td>1L</td>
      <td>20/12/2024</td>
      <td>Lidl</td>
      <td><span class="badge bg-warning">4x/semaine</span></td>
      <td>
        <span class="text-success">✓ Meilleur choix</span>
      </td>
    </tr>
  </tbody>
</table>
```

## 🔧 Implémentation Technique

### Backend (Java/Spring)

**Nouveau Service : `StatisticsService.java`**

```java
@Service
public class StatisticsService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    // Obtenir les produits avec statistiques pour une période
    public List<ProductStatistics> getProductStatistics(LocalDate startDate, LocalDate endDate) {
        // Grouper par nom de produit
        // Calculer fréquence, prix min/max/moyen
        // Identifier les meilleurs prix par magasin
        // Calculer les économies potentielles
    }
    
    // Comparer les prix entre magasins
    public PriceComparison compareProductPrices(String productName) {
        // Par magasin
        // Écart vs meilleur prix
    }
    
    // Générer des alertes et recommandations
    public List<Alert> generateAlerts(LocalDate startDate, LocalDate endDate) {
        // Prix inhabituellement élevés
        // Produits achetés très fréquemment
        // Opportunités d'économie
    }
}
```

**DTO : `ProductStatistics.java`**

```java
@Data
public class ProductStatistics {
    private String productName;
    private int purchaseCount;           // Nombre d'achats
    private BigDecimal averagePrice;     // Prix moyen
    private BigDecimal minPrice;         // Prix minimum
    private BigDecimal maxPrice;         // Prix maximum
    private String bestStore;            // Magasin le moins cher
    private String worstStore;           // Magasin le plus cher
    private BigDecimal potentialSavings; // Économie possible
    private List<Purchase> purchases;    // Détail des achats
    private FrequencyLevel frequency;    // FREQUENT, MODERATE, OCCASIONAL, RARE
}
```

### Frontend (Thymeleaf + JavaScript)

**Fonctions JavaScript :**

```javascript
// Tri dynamique
function sortTable(column, order) {
    // Trier les lignes du tableau
}

// Mise à jour des surlignements
function updateHighlights() {
    // Appliquer les classes CSS selon les critères
}

// Filtrage en temps réel
function filterProducts(filters) {
    // Période, magasin, catégorie
}

// Affichage détails produit
function showProductDetails(productId) {
    // Modal avec graphique d'évolution des prix
}
```

## 📅 Plan de Développement

### Phase 1 : Fondations (Prioritaire)
- [ ] Créer `StatisticsService`
- [ ] Créer DTOs et modèles
- [ ] Implémenter calculs de base (fréquence, prix min/max)
- [ ] Page statistiques basique avec tableau

### Phase 2 : Visualisation
- [ ] Surlignement par fréquence
- [ ] Surlignement par prix
- [ ] Tri dynamique du tableau
- [ ] Filtres de période

### Phase 3 : Intelligence
- [ ] Comparaison entre magasins
- [ ] Calcul des économies potentielles
- [ ] Génération d'alertes
- [ ] Recommandations personnalisées

### Phase 4 : Graphiques
- [ ] Évolution des prix dans le temps
- [ ] Répartition des dépenses par catégorie
- [ ] Tendances d'achat

## 🎨 Maquette Visuelle

```
╔═══════════════════════════════════════════════════════╗
║ 📊 MES STATISTIQUES D'ACHAT - DÉCEMBRE 2024          ║
╠═══════════════════════════════════════════════════════╣
║                                                       ║
║  Période: [📅 Ce mois ▼]  Tri: [💰 Prix ▼]         ║
║  Magasin: [🏪 Tous ▼]     Vue: [📋 Tableau]        ║
║                                                       ║
╠═══════════════════════════════════════════════════════╣
║                                                       ║
║  📈 RÉSUMÉ                                            ║
║  ┌─────────┬─────────┬─────────┬──────────┐         ║
║  │ 457.80€ │ 12      │ 38.15€  │ 23.40€   │         ║
║  │ Dépensé │ Tickets │ Panier  │ Économies│         ║
║  └─────────┴─────────┴─────────┴──────────┘         ║
║                                                       ║
╠═══════════════════════════════════════════════════════╣
║                                                       ║
║  🛒 PRODUITS ACHETÉS                                  ║
║  ┌──────────────────────────────────────────────┐   ║
║  │ 🥛 Lait (1.65€) [4x] 🔴 +13%  │ Monoprix   │   ║
║  │ 🥛 Lait (1.50€) [4x]          │ Carrefour  │   ║
║  │ 🥛 Lait (1.45€) [4x] 🟢 Best! │ Lidl       │   ║
║  │ 🥖 Pain (2.30€) [2x]          │ Boulangerie│   ║
║  │ 🍅 Tomates (4.00€) ⚠️ +25%    │ Monoprix   │   ║
║  └──────────────────────────────────────────────┘   ║
║                                                       ║
╠═══════════════════════════════════════════════════════╣
║                                                       ║
║  💡 RECOMMANDATIONS                                   ║
║  • Économisez 3.20€/mois en achetant le lait chez   ║
║    Lidl au lieu de Monoprix                          ║
║  • Tomates : Prix en hausse, envisagez d'attendre   ║
║  • Pain : Meilleur prix le jeudi chez Carrefour     ║
║                                                       ║
╚═══════════════════════════════════════════════════════╝
```

---

**Document créé le : 25 Décembre 2024**  
**Statut : Spécifications - En attente d'implémentation**

