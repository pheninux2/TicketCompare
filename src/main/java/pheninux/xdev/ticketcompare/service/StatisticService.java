package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pheninux.xdev.ticketcompare.dto.PriceStatisticDTO;
import pheninux.xdev.ticketcompare.entity.PriceHistory;
import pheninux.xdev.ticketcompare.entity.Product;
import pheninux.xdev.ticketcompare.repository.PriceHistoryRepository;
import pheninux.xdev.ticketcompare.repository.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticService {
    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @Transactional(readOnly = true)
    public PriceStatisticDTO getCategoryStatistics(String category) {
        List<Product> products = productRepository.findByCategory(category, org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
            .getContent();

        if (products.isEmpty()) {
            return null;
        }

        BigDecimal minPrice = products.stream()
            .map(Product::getPrice)
            .min(Comparator.naturalOrder())
            .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = products.stream()
            .map(Product::getPrice)
            .max(Comparator.naturalOrder())
            .orElse(BigDecimal.ZERO);

        BigDecimal avgPrice = products.stream()
            .map(Product::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(products.size()), 2, RoundingMode.HALF_UP);

        return PriceStatisticDTO.builder()
            .category(category)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .averagePrice(avgPrice)
            .productCount((long) products.size())
            .build();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCategoryProducts(String category) {
        List<Product> products = productRepository.findByCategory(category, org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
            .getContent();

        // Grouper les produits par nom et calculer le prix moyen
        Map<String, List<Product>> groupedByName = products.stream()
            .collect(Collectors.groupingBy(Product::getName));

        return groupedByName.entrySet().stream()
            .map(entry -> {
                String productName = entry.getKey();
                List<Product> productList = entry.getValue();

                BigDecimal avgPrice = productList.stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(productList.size()), 2, RoundingMode.HALF_UP);

                BigDecimal minPrice = productList.stream()
                    .map(Product::getPrice)
                    .min(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);

                BigDecimal maxPrice = productList.stream()
                    .map(Product::getPrice)
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);

                // Trouver les dates de premier et dernier achat
                LocalDate firstPurchase = productList.stream()
                    .map(p -> p.getTicket().getDate())
                    .min(Comparator.naturalOrder())
                    .orElse(null);

                LocalDate lastPurchase = productList.stream()
                    .map(p -> p.getTicket().getDate())
                    .max(Comparator.naturalOrder())
                    .orElse(null);

                Map<String, Object> map = new HashMap<>();
                map.put("name", productName);
                map.put("avgPrice", avgPrice);
                map.put("minPrice", minPrice);
                map.put("maxPrice", maxPrice);
                map.put("count", productList.size());
                map.put("firstPurchase", firstPurchase);
                map.put("lastPurchase", lastPurchase);
                return map;
            })
            .sorted((m1, m2) -> ((BigDecimal) m2.get("avgPrice")).compareTo((BigDecimal) m1.get("avgPrice")))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PriceStatisticDTO> getAllCategoriesStatistics() {
        List<String> categories = productRepository.findDistinctCategories();
        return categories.stream()
            .map(this::getCategoryStatistics)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPriceComparison(String productName) {
        List<PriceHistory> history = priceHistoryRepository.findByProductNameOrderByPriceDateDesc(productName);

        if (history.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> comparison = new HashMap<>();
        comparison.put("productName", productName);
        comparison.put("currentPrice", history.get(0).getPrice());
        comparison.put("minPrice", history.stream()
            .map(PriceHistory::getPrice)
            .min(Comparator.naturalOrder())
            .orElse(BigDecimal.ZERO));
        comparison.put("maxPrice", history.stream()
            .map(PriceHistory::getPrice)
            .max(Comparator.naturalOrder())
            .orElse(BigDecimal.ZERO));
        comparison.put("averagePrice", history.stream()
            .map(PriceHistory::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(history.size()), 2, RoundingMode.HALF_UP));
        comparison.put("observations", history.size());

        return comparison;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMostExpensiveProducts(int limit) {
        List<Product> products = productRepository.findAll();
        return products.stream()
            .sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()))
            .limit(limit)
            .map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("name", p.getName());
                map.put("price", p.getPrice());
                map.put("category", p.getCategory());
                map.put("store", p.getTicket().getStore());
                map.put("date", p.getTicket().getDate());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCheapestProducts(int limit) {
        List<Product> products = productRepository.findAll();
        return products.stream()
            .sorted(Comparator.comparing(Product::getPrice))
            .limit(limit)
            .map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("name", p.getName());
                map.put("price", p.getPrice());
                map.put("category", p.getCategory());
                map.put("store", p.getTicket().getStore());
                map.put("date", p.getTicket().getDate());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageTicketPrice(LocalDate startDate, LocalDate endDate) {
        List<Product> products = productRepository.findAll().stream()
            .filter(p -> p.getTicket().getDate().isAfter(startDate.minusDays(1)) &&
                        p.getTicket().getDate().isBefore(endDate.plusDays(1)))
            .collect(Collectors.toList());

        if (products.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return products.stream()
            .map(Product::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(products.size()), 2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPriceTrends(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        Map<String, Object> result = new HashMap<>();

        // Récupérer tous les produits dans la période
        List<Product> products = productRepository.findAll().stream()
            .filter(p -> p.getTicket() != null &&
                        p.getTicket().getDate() != null &&
                        p.getTicket().getDate().isAfter(startDate.minusDays(1)) &&
                        p.getTicket().getDate().isBefore(endDate.plusDays(1)))
            .collect(Collectors.toList());

        if (products.isEmpty()) {
            result.put("empty", true);
            return result;
        }

        // Calculer le prix moyen global
        BigDecimal avgPrice = products.stream()
            .map(Product::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(products.size()), 2, RoundingMode.HALF_UP);

        result.put("avgPrice", avgPrice);
        result.put("productCount", products.stream().map(Product::getName).distinct().count());
        result.put("observations", products.size());

        // Grouper par date pour le graphique
        Map<LocalDate, BigDecimal> pricesByDate = products.stream()
            .collect(Collectors.groupingBy(
                p -> p.getTicket().getDate(),
                Collectors.collectingAndThen(
                    Collectors.averagingDouble(p -> p.getPrice().doubleValue()),
                    avg -> BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP)
                )
            ));

        List<String> dates = pricesByDate.keySet().stream()
            .sorted()
            .map(LocalDate::toString)
            .collect(Collectors.toList());

        List<BigDecimal> prices = pricesByDate.keySet().stream()
            .sorted()
            .map(pricesByDate::get)
            .collect(Collectors.toList());

        result.put("dates", dates);
        result.put("prices", prices);

        // Calculer la tendance (comparer dernier vs premier)
        if (prices.size() >= 2) {
            BigDecimal first = prices.get(0);
            BigDecimal last = prices.get(prices.size() - 1);
            BigDecimal diff = last.subtract(first);

            if (first.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentChange = diff.divide(first, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);

                result.put("trend", diff.compareTo(BigDecimal.ZERO) >= 0 ? "up" : "down");
                result.put("trendPercent", percentChange.abs());
            }
        }

        // Top baisses et hausses (comparer même produit entre deux achats)
        Map<String, List<Product>> productsByName = products.stream()
            .collect(Collectors.groupingBy(Product::getName));

        List<Map<String, Object>> drops = new ArrayList<>();
        List<Map<String, Object>> rises = new ArrayList<>();

        productsByName.forEach((name, prods) -> {
            if (prods.size() >= 2) {
                prods.sort(Comparator.comparing(p -> p.getTicket().getDate()));
                Product oldest = prods.get(0);
                Product newest = prods.get(prods.size() - 1);

                BigDecimal oldPrice = oldest.getPrice();
                BigDecimal newPrice = newest.getPrice();
                BigDecimal diff = newPrice.subtract(oldPrice);

                if (oldPrice.compareTo(BigDecimal.ZERO) > 0 && diff.abs().compareTo(BigDecimal.valueOf(0.01)) > 0) {
                    BigDecimal percentChange = diff.divide(oldPrice, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(1, RoundingMode.HALF_UP);

                    Map<String, Object> change = new HashMap<>();
                    change.put("name", name);
                    change.put("oldPrice", oldPrice);
                    change.put("newPrice", newPrice);
                    change.put("change", percentChange);

                    if (diff.compareTo(BigDecimal.ZERO) < 0) {
                        drops.add(change);
                    } else {
                        rises.add(change);
                    }
                }
            }
        });

        // Trier et limiter à top 5
        drops.sort((a, b) -> ((BigDecimal)a.get("change")).compareTo((BigDecimal)b.get("change")));
        rises.sort((a, b) -> ((BigDecimal)b.get("change")).compareTo((BigDecimal)a.get("change")));

        result.put("drops", drops.stream().limit(5).collect(Collectors.toList()));
        result.put("rises", rises.stream().limit(5).collect(Collectors.toList()));

        return result;
    }
}

