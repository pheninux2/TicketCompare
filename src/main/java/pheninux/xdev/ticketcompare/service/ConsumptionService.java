package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pheninux.xdev.ticketcompare.dto.ConsumptionDTO;
import pheninux.xdev.ticketcompare.entity.ConsumptionStatistic;
import pheninux.xdev.ticketcompare.entity.Product;
import pheninux.xdev.ticketcompare.repository.ConsumptionStatisticRepository;
import pheninux.xdev.ticketcompare.repository.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsumptionService {
    private final ConsumptionStatisticRepository consumptionRepository;
    private final ProductRepository productRepository;

    public void calculateWeeklyConsumption(LocalDate startDate, LocalDate endDate) {
        List<Product> products = productRepository.findAll();

        Map<String, List<Product>> groupedByProductName = products.stream()
            .filter(p -> p.getTicket().getDate().isAfter(startDate.minusDays(1)) &&
                         p.getTicket().getDate().isBefore(endDate.plusDays(1)))
            .collect(Collectors.groupingBy(Product::getName));

        for (Map.Entry<String, List<Product>> entry : groupedByProductName.entrySet()) {
            String productName = entry.getKey();
            List<Product> productList = entry.getValue();

            // Group by week
            Map<LocalDate, List<Product>> groupedByWeek = productList.stream()
                .collect(Collectors.groupingBy(p -> getWeekStart(p.getTicket().getDate())));

            for (Map.Entry<LocalDate, List<Product>> weekEntry : groupedByWeek.entrySet()) {
                LocalDate weekStart = weekEntry.getKey();
                List<Product> weekProducts = weekEntry.getValue();

                BigDecimal totalQuantity = weekProducts.stream()
                    .map(Product::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalCost = weekProducts.stream()
                    .map(Product::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                ConsumptionStatistic existing = consumptionRepository.findAll().stream()
                    .filter(cs -> cs.getProductName().equals(productName) && cs.getWeekStart().equals(weekStart))
                    .findFirst()
                    .orElse(null);

                if (existing == null) {
                    ConsumptionStatistic statistic = ConsumptionStatistic.builder()
                        .productName(productName)
                        .weekStart(weekStart)
                        .totalQuantity(totalQuantity)
                        .totalCost(totalCost)
                        .purchaseCount((long) weekProducts.size())
                        .category(weekProducts.get(0).getCategory())
                        .unit(weekProducts.get(0).getUnit())
                        .build();
                    consumptionRepository.save(statistic);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ConsumptionDTO> getWeeklyConsumption(LocalDate weekStart) {
        return consumptionRepository.getTopProductsByWeek(weekStart).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsumptionDTO> getTopProductsByWeek(LocalDate weekStart, int limit) {
        List<ConsumptionStatistic> stats = consumptionRepository.getTopProductsByWeek(weekStart);
        return stats.stream()
            .limit(limit)
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsumptionDTO> getConsumptionTrend(String productName, int weeks) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(weeks);

        return consumptionRepository.getConsumptionByWeekRange(startDate, endDate).stream()
            .filter(cs -> cs.getProductName().equals(productName))
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère la consommation de produits directement depuis les tickets
     * avec filtres et tri
     */
    @Transactional(readOnly = true)
    public List<ConsumptionDTO> getProductConsumption(LocalDate startDate, LocalDate endDate,
                                                      String search, String sortBy, String sortOrder) {
        // Récupérer tous les produits de la période
        List<Product> products = productRepository.findAll().stream()
            .filter(p -> {
                LocalDate ticketDate = p.getTicket().getDate();
                return !ticketDate.isBefore(startDate) && !ticketDate.isAfter(endDate);
            })
            .collect(Collectors.toList());

        // Filtrer par recherche si nécessaire
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            products = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchLower) ||
                           (p.getCategory() != null && p.getCategory().toLowerCase().contains(searchLower)))
                .collect(Collectors.toList());
        }

        // Grouper par nom de produit
        Map<String, List<Product>> groupedByProduct = products.stream()
            .collect(Collectors.groupingBy(Product::getName));

        // Créer les DTOs
        List<ConsumptionDTO> result = groupedByProduct.entrySet().stream()
            .map(entry -> {
                String productName = entry.getKey();
                List<Product> productList = entry.getValue();

                BigDecimal totalQuantity = productList.stream()
                    .map(Product::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalCost = productList.stream()
                    .map(p -> p.getTotalPrice() != null ? p.getTotalPrice() :
                             (p.getPrice() != null && p.getQuantity() != null ?
                              p.getPrice().multiply(p.getQuantity()) : BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal avgPricePerUnit = BigDecimal.ZERO;
                if (totalQuantity.compareTo(BigDecimal.ZERO) > 0) {
                    avgPricePerUnit = totalCost.divide(totalQuantity, 2, RoundingMode.HALF_UP);
                }

                // Créer les détails de chaque achat
                List<ConsumptionDTO.PurchaseDetail> details = productList.stream()
                    .map(p -> ConsumptionDTO.PurchaseDetail.builder()
                        .date(p.getTicket().getDate())
                        .store(p.getTicket().getStore())
                        .quantity(p.getQuantity())
                        .price(p.getPrice())
                        .totalPrice(p.getTotalPrice() != null ? p.getTotalPrice() :
                                   (p.getPrice() != null && p.getQuantity() != null ?
                                    p.getPrice().multiply(p.getQuantity()) : BigDecimal.ZERO))
                        .build())
                    .sorted(Comparator.comparing(ConsumptionDTO.PurchaseDetail::getDate).reversed())
                    .collect(Collectors.toList());

                return ConsumptionDTO.builder()
                    .productName(productName)
                    .category(productList.get(0).getCategory())
                    .unit(productList.get(0).getUnit())
                    .totalQuantity(totalQuantity)
                    .totalCost(totalCost)
                    .purchaseCount((long) productList.size())
                    .averagePricePerUnit(avgPricePerUnit)
                    .weekStart(startDate) // Pour compatibilité
                    .purchaseDetails(details)
                    .build();
            })
            .collect(Collectors.toList());

        // Trier selon les critères
        Comparator<ConsumptionDTO> comparator = getComparator(sortBy);
        if ("desc".equals(sortOrder)) {
            comparator = comparator.reversed();
        }
        result.sort(comparator);

        return result;
    }

    private Comparator<ConsumptionDTO> getComparator(String sortBy) {
        switch (sortBy) {
            case "productName":
                return Comparator.comparing(ConsumptionDTO::getProductName);
            case "category":
                return Comparator.comparing(dto -> dto.getCategory() != null ? dto.getCategory() : "");
            case "totalQuantity":
                return Comparator.comparing(ConsumptionDTO::getTotalQuantity);
            case "purchaseCount":
                return Comparator.comparing(ConsumptionDTO::getPurchaseCount);
            case "averagePricePerUnit":
                return Comparator.comparing(ConsumptionDTO::getAveragePricePerUnit);
            case "totalCost":
            default:
                return Comparator.comparing(ConsumptionDTO::getTotalCost);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, ConsumptionDTO> getTopConsumptionByCategory(LocalDate weekStart) {
        List<ConsumptionStatistic> stats = consumptionRepository.getTopProductsByWeek(weekStart);

        return stats.stream()
            .collect(Collectors.toMap(
                ConsumptionStatistic::getCategory,
                this::mapToDTO,
                (existing, replacement) -> existing
            ));
    }

    private LocalDate getWeekStart(LocalDate date) {
        return date.with(WeekFields.ISO.dayOfWeek(), 1);
    }

    private ConsumptionDTO mapToDTO(ConsumptionStatistic statistic) {
        BigDecimal avgPrice = BigDecimal.ZERO;
        if (statistic.getTotalQuantity().compareTo(BigDecimal.ZERO) > 0) {
            avgPrice = statistic.getTotalCost()
                .divide(statistic.getTotalQuantity(), 2, RoundingMode.HALF_UP);
        }

        return ConsumptionDTO.builder()
            .productName(statistic.getProductName())
            .weekStart(statistic.getWeekStart())
            .totalQuantity(statistic.getTotalQuantity())
            .totalCost(statistic.getTotalCost())
            .purchaseCount(statistic.getPurchaseCount())
            .category(statistic.getCategory())
            .unit(statistic.getUnit())
            .averagePricePerUnit(avgPrice)
            .build();
    }
}

