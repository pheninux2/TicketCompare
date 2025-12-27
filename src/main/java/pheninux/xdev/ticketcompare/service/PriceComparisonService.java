package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pheninux.xdev.ticketcompare.entity.Product;
import pheninux.xdev.ticketcompare.repository.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PriceComparisonService {
    private final ProductRepository productRepository;

    /**
     * Compare les prix d'un produit entre différents magasins
     */
    @Transactional(readOnly = true)
    public Map<String, Object> compareProductPricesByStore(String productName) {
        List<Product> products = productRepository.findAll().stream()
            .filter(p -> p.getName().toLowerCase().contains(productName.toLowerCase()))
            .collect(Collectors.toList());

        if (products.isEmpty()) {
            return Map.of("error", "Produit non trouvé", "productName", productName);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("productName", productName);
        result.put("totalObservations", products.size());

        // Grouper par magasin
        Map<String, List<Product>> byStore = products.stream()
            .collect(Collectors.groupingBy(p -> p.getTicket().getStore()));

        // Calculer les statistiques par magasin
        List<Map<String, Object>> storeComparison = new ArrayList<>();
        for (Map.Entry<String, List<Product>> entry : byStore.entrySet()) {
            String store = entry.getKey();
            List<Product> storeProducts = entry.getValue();

            BigDecimal avgPrice = storeProducts.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(storeProducts.size()), 2, RoundingMode.HALF_UP);

            BigDecimal minPrice = storeProducts.stream()
                .map(Product::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

            BigDecimal maxPrice = storeProducts.stream()
                .map(Product::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

            LocalDate lastPurchase = storeProducts.stream()
                .map(p -> p.getTicket().getDate())
                .max(Comparator.naturalOrder())
                .orElse(null);

            Map<String, Object> storeData = new HashMap<>();
            storeData.put("store", store);
            storeData.put("avgPrice", avgPrice);
            storeData.put("minPrice", minPrice);
            storeData.put("maxPrice", maxPrice);
            storeData.put("purchaseCount", storeProducts.size());
            storeData.put("lastPurchase", lastPurchase);

            storeComparison.add(storeData);
        }

        // Trier par prix moyen croissant
        storeComparison.sort((a, b) ->
            ((BigDecimal) a.get("avgPrice")).compareTo((BigDecimal) b.get("avgPrice"))
        );

        result.put("storeComparison", storeComparison);

        // Identifier le meilleur magasin
        if (!storeComparison.isEmpty()) {
            Map<String, Object> bestStore = storeComparison.get(0);
            result.put("bestStore", bestStore.get("store"));
            result.put("bestPrice", bestStore.get("avgPrice"));

            // Calculer l'économie potentielle
            if (storeComparison.size() > 1) {
                BigDecimal worstPrice = ((BigDecimal) storeComparison.get(storeComparison.size() - 1).get("avgPrice"));
                BigDecimal savings = worstPrice.subtract((BigDecimal) bestStore.get("avgPrice"));
                result.put("potentialSavings", savings);
            }
        }

        return result;
    }

    /**
     * Obtient tous les produits disponibles pour comparaison
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllProductsForComparison() {
        List<Product> products = productRepository.findAll();

        // Grouper par nom de produit
        Map<String, List<Product>> groupedByName = products.stream()
            .collect(Collectors.groupingBy(Product::getName));

        List<Map<String, Object>> productList = new ArrayList<>();
        for (Map.Entry<String, List<Product>> entry : groupedByName.entrySet()) {
            String name = entry.getKey();
            List<Product> productGroup = entry.getValue();

            // Compter le nombre de magasins différents
            long storeCount = productGroup.stream()
                .map(p -> p.getTicket().getStore())
                .distinct()
                .count();

            // Ne garder que les produits achetés dans au moins 2 magasins
            if (storeCount >= 2) {
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("name", name);
                productInfo.put("storeCount", storeCount);
                productInfo.put("totalPurchases", productGroup.size());
                productInfo.put("category", productGroup.get(0).getCategory());

                productList.add(productInfo);
            }
        }

        // Trier par nombre de magasins décroissant
        productList.sort((a, b) ->
            Long.compare((Long) b.get("storeCount"), (Long) a.get("storeCount"))
        );

        return productList;
    }

    /**
     * Évolution des prix d'un produit dans un magasin
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPriceEvolution(String productName, String store) {
        List<Product> products = productRepository.findAll().stream()
            .filter(p -> p.getName().toLowerCase().contains(productName.toLowerCase()))
            .filter(p -> p.getTicket().getStore().equals(store))
            .sorted(Comparator.comparing(p -> p.getTicket().getDate()))
            .collect(Collectors.toList());

        Map<String, Object> evolution = new HashMap<>();
        evolution.put("productName", productName);
        evolution.put("store", store);

        if (products.isEmpty()) {
            evolution.put("error", "Aucune donnée trouvée");
            return evolution;
        }

        // Créer la timeline des prix
        List<Map<String, Object>> timeline = products.stream()
            .map(p -> {
                Map<String, Object> point = new HashMap<>();
                point.put("date", p.getTicket().getDate());
                point.put("price", p.getPrice());
                return point;
            })
            .collect(Collectors.toList());

        evolution.put("timeline", timeline);

        // Calculer la variation
        BigDecimal firstPrice = products.get(0).getPrice();
        BigDecimal lastPrice = products.get(products.size() - 1).getPrice();
        BigDecimal variation = lastPrice.subtract(firstPrice);
        BigDecimal percentVariation = firstPrice.compareTo(BigDecimal.ZERO) > 0 ?
            variation.divide(firstPrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
            BigDecimal.ZERO;

        evolution.put("firstPrice", firstPrice);
        evolution.put("lastPrice", lastPrice);
        evolution.put("variation", variation);
        evolution.put("percentVariation", percentVariation);
        evolution.put("trend", variation.compareTo(BigDecimal.ZERO) > 0 ? "UP" : "DOWN");

        return evolution;
    }

    /**
     * Tableau de comparaison global pour tous les produits réguliers
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getGlobalPriceComparison() {
        try {
            List<Product> products = productRepository.findAll();

            // Identifier les produits achetés dans plusieurs magasins
            Map<String, List<Product>> byProduct = products.stream()
                .filter(p -> p.getTicket() != null && p.getTicket().getStore() != null)
                .collect(Collectors.groupingBy(Product::getName));

            List<Map<String, Object>> comparisons = new ArrayList<>();

            for (Map.Entry<String, List<Product>> entry : byProduct.entrySet()) {
                String productName = entry.getKey();
                List<Product> productList = entry.getValue();

                // Grouper par magasin
                Map<String, List<Product>> byStore = productList.stream()
                    .filter(p -> p.getTicket() != null && p.getTicket().getStore() != null)
                    .collect(Collectors.groupingBy(p -> p.getTicket().getStore()));

                // Ne garder que les produits dans au moins 2 magasins
                if (byStore.size() >= 2) {
                    Map<String, Object> comparison = new HashMap<>();
                    comparison.put("productName", productName);
                    comparison.put("category", productList.get(0).getCategory() != null ?
                        productList.get(0).getCategory() : "Autre");

                    Map<String, BigDecimal> storePrices = new HashMap<>();
                    String cheapestStore = null;
                    BigDecimal cheapestPrice = null;

                    for (Map.Entry<String, List<Product>> storeEntry : byStore.entrySet()) {
                        String store = storeEntry.getKey();
                        List<Product> storeProducts = storeEntry.getValue();

                        if (storeProducts.isEmpty()) continue;

                        BigDecimal avgPrice = storeProducts.stream()
                            .map(Product::getPrice)
                            .filter(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(storeProducts.size()), 2, RoundingMode.HALF_UP);

                        if (avgPrice.compareTo(BigDecimal.ZERO) > 0) {
                            storePrices.put(store, avgPrice);

                            if (cheapestPrice == null || avgPrice.compareTo(cheapestPrice) < 0) {
                                cheapestPrice = avgPrice;
                                cheapestStore = store;
                            }
                        }
                    }

                    if (!storePrices.isEmpty() && cheapestStore != null) {
                        comparison.put("storePrices", storePrices);
                        comparison.put("cheapestStore", cheapestStore);
                        comparison.put("cheapestPrice", cheapestPrice);
                        comparisons.add(comparison);
                    }
                }
            }

            // Calculer le total des économies potentielles
            BigDecimal totalSavings = BigDecimal.ZERO;
            for (Map<String, Object> comp : comparisons) {
                @SuppressWarnings("unchecked")
                Map<String, BigDecimal> storePrices = (Map<String, BigDecimal>) comp.get("storePrices");
                BigDecimal cheapest = (BigDecimal) comp.get("cheapestPrice");
                BigDecimal mostExpensive = storePrices.values().stream()
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);
                if (mostExpensive.compareTo(cheapest) > 0) {
                    totalSavings = totalSavings.add(mostExpensive.subtract(cheapest));
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("comparisons", comparisons);
            result.put("productCount", comparisons.size());
            result.put("totalPotentialSavings", totalSavings);

            // Statistiques par magasin
            Map<String, Long> storeWinCount = new HashMap<>();
            for (Map<String, Object> comp : comparisons) {
                String cheapestStore = (String) comp.get("cheapestStore");
                if (cheapestStore != null) {
                    storeWinCount.put(cheapestStore, storeWinCount.getOrDefault(cheapestStore, 0L) + 1);
                }
            }
            result.put("storeWinCount", storeWinCount);

            return result;
        } catch (Exception e) {
            // En cas d'erreur, retourner des valeurs par défaut
            Map<String, Object> result = new HashMap<>();
            result.put("comparisons", new ArrayList<>());
            result.put("productCount", 0);
            result.put("totalPotentialSavings", BigDecimal.ZERO);
            result.put("storeWinCount", new HashMap<>());
            return result;
        }
    }

    /**
     * Détecte les baisses de prix significatives (>10%)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> detectPriceDrops() {
        LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

        List<Product> recentProducts = productRepository.findAll().stream()
            .filter(p -> p.getTicket().getDate().isAfter(oneWeekAgo))
            .collect(Collectors.toList());

        List<Product> oldProducts = productRepository.findAll().stream()
            .filter(p -> p.getTicket().getDate().isBefore(oneWeekAgo))
            .filter(p -> p.getTicket().getDate().isAfter(oneMonthAgo))
            .collect(Collectors.toList());

        List<Map<String, Object>> priceDrops = new ArrayList<>();

        // Grouper les produits récents par nom et magasin
        Map<String, Map<String, List<Product>>> recentByProductAndStore = recentProducts.stream()
            .collect(Collectors.groupingBy(
                Product::getName,
                Collectors.groupingBy(p -> p.getTicket().getStore())
            ));

        // Grouper les anciens produits
        Map<String, Map<String, List<Product>>> oldByProductAndStore = oldProducts.stream()
            .collect(Collectors.groupingBy(
                Product::getName,
                Collectors.groupingBy(p -> p.getTicket().getStore())
            ));

        // Comparer les prix
        for (String productName : recentByProductAndStore.keySet()) {
            if (oldByProductAndStore.containsKey(productName)) {
                Map<String, List<Product>> recentStores = recentByProductAndStore.get(productName);
                Map<String, List<Product>> oldStores = oldByProductAndStore.get(productName);

                for (String store : recentStores.keySet()) {
                    if (oldStores.containsKey(store)) {
                        BigDecimal recentAvg = recentStores.get(store).stream()
                            .map(Product::getPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(recentStores.get(store).size()), 2, RoundingMode.HALF_UP);

                        BigDecimal oldAvg = oldStores.get(store).stream()
                            .map(Product::getPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(oldStores.get(store).size()), 2, RoundingMode.HALF_UP);

                        BigDecimal drop = oldAvg.subtract(recentAvg);
                        BigDecimal percentDrop = oldAvg.compareTo(BigDecimal.ZERO) > 0 ?
                            drop.divide(oldAvg, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                            BigDecimal.ZERO;

                        // Baisse significative (>10%)
                        if (percentDrop.compareTo(BigDecimal.valueOf(10)) > 0) {
                            Map<String, Object> priceDrop = new HashMap<>();
                            priceDrop.put("productName", productName);
                            priceDrop.put("store", store);
                            priceDrop.put("oldPrice", oldAvg);
                            priceDrop.put("newPrice", recentAvg);
                            priceDrop.put("drop", drop);
                            priceDrop.put("percentDrop", percentDrop);

                            priceDrops.add(priceDrop);
                        }
                    }
                }
            }
        }

        // Trier par pourcentage de baisse décroissant
        priceDrops.sort((a, b) ->
            ((BigDecimal) b.get("percentDrop")).compareTo((BigDecimal) a.get("percentDrop"))
        );

        return priceDrops;
    }
}

