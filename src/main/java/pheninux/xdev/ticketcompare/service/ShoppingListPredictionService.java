package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pheninux.xdev.ticketcompare.entity.Product;
import pheninux.xdev.ticketcompare.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingListPredictionService {
    private final ProductRepository productRepository;

    /**
     * Catégories alimentaires et leurs recommandations
     */
    private static final Map<String, Integer> RECOMMENDED_WEEKLY_SERVINGS = Map.of(
        "Fruits & Légumes", 35,  // 5 portions par jour
        "Laitier", 14,            // 2 portions par jour
        "Viande", 7,              // 1 portion par jour
        "Poisson", 3,             // 2-3 fois par semaine
        "Féculents", 21           // 3 portions par jour
    );

    @Transactional(readOnly = true)
    public Map<String, Object> analyzeConsumptionHabits() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        List<Product> recentProducts = productRepository.findAll().stream()
            .filter(p -> p.getTicket().getDate().isAfter(startDate))
            .collect(Collectors.toList());

        Map<String, Object> analysis = new HashMap<>();

        // 1. Analyse par catégorie
        Map<String, Long> categoryCount = recentProducts.stream()
            .collect(Collectors.groupingBy(
                p -> p.getCategory() != null ? p.getCategory() : "Autre",
                Collectors.counting()
            ));

        analysis.put("categoryDistribution", categoryCount);

        // 2. Légumes peu consommés
        List<String> underconsumedVegetables = findUnderconsumedVegetables(recentProducts);
        analysis.put("underconsumedVegetables", underconsumedVegetables);

        // 3. Analyse du sucre
        Map<String, Object> sugarAnalysis = analyzeSugarConsumption(recentProducts);
        analysis.put("sugarAnalysis", sugarAnalysis);

        // 4. Analyse des produits laitiers (calcium)
        Map<String, Object> dairyAnalysis = analyzeDairyConsumption(recentProducts);
        analysis.put("dairyAnalysis", dairyAnalysis);

        // 5. Score de santé global
        int healthScore = calculateHealthScore(categoryCount);
        analysis.put("healthScore", healthScore);

        // 6. Recommandations
        List<String> recommendations = generateRecommendations(categoryCount, sugarAnalysis, dairyAnalysis);
        analysis.put("recommendations", recommendations);

        return analysis;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generateSmartShoppingList() {
        Map<String, Object> analysis = analyzeConsumptionHabits();
        Map<String, Object> shoppingList = new HashMap<>();

        // 1. Produits à réduire
        @SuppressWarnings("unchecked")
        Map<String, Object> sugarAnalysis = (Map<String, Object>) analysis.get("sugarAnalysis");
        @SuppressWarnings("unchecked")
        List<String> highSugarProducts = (List<String>) sugarAnalysis.get("highSugarProducts");
        shoppingList.put("productsToReduce", highSugarProducts);

        // 2. Produits recommandés (légumes peu consommés)
        @SuppressWarnings("unchecked")
        List<String> vegetables = (List<String>) analysis.get("underconsumedVegetables");
        shoppingList.put("vegetablesToAdd", vegetables);

        // 3. Alternatives saines
        List<Map<String, String>> healthyAlternatives = generateHealthyAlternatives();
        shoppingList.put("healthyAlternatives", healthyAlternatives);

        // 4. Équilibre nutritionnel
        Map<String, Object> nutritionalBalance = calculateNutritionalBalance(analysis);
        shoppingList.put("nutritionalBalance", nutritionalBalance);

        // 5. Liste suggérée par catégorie
        Map<String, List<String>> suggestedByCategory = generateSuggestedProducts();
        shoppingList.put("suggestedByCategory", suggestedByCategory);

        return shoppingList;
    }

    private List<String> findUnderconsumedVegetables(List<Product> products) {
        Set<String> consumedVegetables = products.stream()
            .filter(p -> "Fruits & Légumes".equals(p.getCategory()))
            .map(p -> p.getName().toLowerCase())
            .collect(Collectors.toSet());

        List<String> recommendedVegetables = Arrays.asList(
            "Épinards", "Brocoli", "Patate douce", "Courgette", "Aubergine",
            "Poivron", "Chou", "Carotte", "Tomate", "Salade",
            "Concombre", "Haricots verts", "Pois", "Avocat"
        );

        return recommendedVegetables.stream()
            .filter(v -> !consumedVegetables.contains(v.toLowerCase()))
            .limit(5)
            .collect(Collectors.toList());
    }

    private Map<String, Object> analyzeSugarConsumption(List<Product> products) {
        Map<String, Object> analysis = new HashMap<>();

        // Identifier les produits sucrés
        List<String> sugarCategories = Arrays.asList("Confiserie", "Biscuiterie");
        List<String> highSugarProducts = products.stream()
            .filter(p -> sugarCategories.contains(p.getCategory()))
            .map(Product::getName)
            .distinct()
            .collect(Collectors.toList());

        long sugarProductCount = products.stream()
            .filter(p -> sugarCategories.contains(p.getCategory()))
            .count();

        analysis.put("highSugarProducts", highSugarProducts);
        analysis.put("sugarProductCount", sugarProductCount);
        analysis.put("level", sugarProductCount > 5 ? "HIGH" : sugarProductCount > 2 ? "MEDIUM" : "LOW");

        return analysis;
    }

    private Map<String, Object> analyzeDairyConsumption(List<Product> products) {
        Map<String, Object> analysis = new HashMap<>();

        List<String> dairyProducts = products.stream()
            .filter(p -> "Laitier".equals(p.getCategory()))
            .map(Product::getName)
            .distinct()
            .collect(Collectors.toList());

        long dairyCount = products.stream()
            .filter(p -> "Laitier".equals(p.getCategory()))
            .count();

        boolean adequate = dairyCount >= 8; // Au moins 2 par semaine sur un mois

        analysis.put("dairyProducts", dairyProducts);
        analysis.put("dairyCount", dairyCount);
        analysis.put("adequate", adequate);
        analysis.put("recommendation", adequate ?
            "Votre consommation de produits laitiers est adéquate" :
            "Augmentez votre consommation de produits laitiers pour l'apport en calcium");

        return analysis;
    }

    private int calculateHealthScore(Map<String, Long> categoryCount) {
        int score = 50; // Score de base

        // Bonus pour les fruits & légumes
        score += Math.min(30, categoryCount.getOrDefault("Fruits & Légumes", 0L).intValue());

        // Bonus pour les produits laitiers
        score += Math.min(10, categoryCount.getOrDefault("Laitier", 0L).intValue() / 2);

        // Malus pour les produits sucrés
        long sugarProducts = categoryCount.getOrDefault("Confiserie", 0L) +
                            categoryCount.getOrDefault("Biscuiterie", 0L);
        score -= (int) Math.min(20, sugarProducts * 2);

        return Math.max(0, Math.min(100, score));
    }

    private List<String> generateRecommendations(Map<String, Long> categoryCount,
                                                 Map<String, Object> sugarAnalysis,
                                                 Map<String, Object> dairyAnalysis) {
        List<String> recommendations = new ArrayList<>();

        // Recommandation légumes
        long vegetables = categoryCount.getOrDefault("Fruits & Légumes", 0L);
        if (vegetables < 20) {
            recommendations.add("Augmentez votre consommation de fruits et légumes (recommandation : 5 portions/jour)");
        }

        // Recommandation sucre
        String sugarLevel = (String) sugarAnalysis.get("level");
        if ("HIGH".equals(sugarLevel)) {
            recommendations.add("Réduisez votre consommation de produits sucrés et privilégiez les fruits frais");
        }

        // Recommandation laitier
        boolean dairyAdequate = (Boolean) dairyAnalysis.get("adequate");
        if (!dairyAdequate) {
            recommendations.add("Augmentez votre consommation de produits laitiers pour l'apport en calcium");
        }

        // Recommandation poisson
        long fish = categoryCount.getOrDefault("Poisson", 0L);
        if (fish < 2) {
            recommendations.add("Consommez du poisson au moins 2 fois par semaine pour les oméga-3");
        }

        return recommendations;
    }

    private List<Map<String, String>> generateHealthyAlternatives() {
        List<Map<String, String>> alternatives = new ArrayList<>();

        alternatives.add(Map.of(
            "instead", "Bonbons et chocolat",
            "use", "Fruits frais (pommes, bananes, oranges)",
            "benefit", "Vitamines naturelles et fibres"
        ));

        alternatives.add(Map.of(
            "instead", "Soda",
            "use", "Eau pétillante avec citron",
            "benefit", "Hydratation sans sucre ajouté"
        ));

        alternatives.add(Map.of(
            "instead", "Biscuits industriels",
            "use", "Yaourt nature avec fruits",
            "benefit", "Protéines et probiotiques"
        ));

        alternatives.add(Map.of(
            "instead", "Sucre blanc",
            "use", "Miel ou sirop d'érable",
            "benefit", "Sucres naturels avec nutriments"
        ));

        return alternatives;
    }

    private Map<String, Object> calculateNutritionalBalance(Map<String, Object> analysis) {
        @SuppressWarnings("unchecked")
        Map<String, Long> distribution = (Map<String, Long>) analysis.get("categoryDistribution");

        Map<String, Object> balance = new HashMap<>();

        for (Map.Entry<String, Integer> entry : RECOMMENDED_WEEKLY_SERVINGS.entrySet()) {
            String category = entry.getKey();
            int recommended = entry.getValue();
            long actual = distribution.getOrDefault(category, 0L);

            double percentage = (actual * 100.0) / recommended;
            String status = percentage >= 80 ? "GOOD" : percentage >= 50 ? "MEDIUM" : "LOW";

            Map<String, Object> categoryBalance = new HashMap<>();
            categoryBalance.put("actual", actual);
            categoryBalance.put("recommended", recommended);
            categoryBalance.put("percentage", Math.round(percentage));
            categoryBalance.put("status", status);

            balance.put(category, categoryBalance);
        }

        return balance;
    }

    private Map<String, List<String>> generateSuggestedProducts() {
        Map<String, List<String>> suggestions = new HashMap<>();

        suggestions.put("Fruits & Légumes", Arrays.asList(
            "Épinards frais",
            "Brocoli",
            "Patate douce",
            "Avocat",
            "Tomates cerises",
            "Carottes",
            "Poivrons",
            "Bananes",
            "Pommes",
            "Oranges"
        ));

        suggestions.put("Laitier", Arrays.asList(
            "Lait demi-écrémé",
            "Yaourt nature",
            "Fromage blanc 0%",
            "Lait d'amande enrichi en calcium",
            "Fromage à pâte dure (pour calcium)"
        ));

        suggestions.put("Protéines", Arrays.asList(
            "Poulet fermier",
            "Saumon frais",
            "Œufs bio",
            "Tofu",
            "Lentilles",
            "Pois chiches"
        ));

        suggestions.put("Féculents", Arrays.asList(
            "Pain complet",
            "Riz brun",
            "Pâtes complètes",
            "Quinoa",
            "Flocons d'avoine"
        ));

        suggestions.put("Alternatives Saines", Arrays.asList(
            "Miel naturel",
            "Fruits secs",
            "Noix et amandes",
            "Chocolat noir 70%",
            "Compote sans sucre ajouté"
        ));

        return suggestions;
    }
}

