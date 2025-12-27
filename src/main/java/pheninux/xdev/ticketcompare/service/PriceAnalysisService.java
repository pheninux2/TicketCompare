package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pheninux.xdev.ticketcompare.dto.PriceForecastDTO;
import pheninux.xdev.ticketcompare.entity.PriceHistory;
import pheninux.xdev.ticketcompare.repository.PriceHistoryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PriceAnalysisService {
    private final PriceHistoryRepository priceHistoryRepository;

    @Transactional(readOnly = true)
    public PriceForecastDTO forecastPrice(String productName, int daysAhead) {
        List<PriceHistory> history = priceHistoryRepository.findByProductNameOrderByPriceDateDesc(productName);

        if (history.size() < 2) {
            return PriceForecastDTO.builder()
                .productName(productName)
                .confidence("LOW")
                .trendDirection("UNKNOWN")
                .build();
        }

        history.sort(Comparator.comparing(PriceHistory::getPriceDate));

        BigDecimal[] prediction = linearRegression(history, daysAhead);
        BigDecimal predictedPrice = prediction[0];
        BigDecimal currentPrice = history.get(history.size() - 1).getPrice();

        String trend = predictedPrice.compareTo(currentPrice) > 0 ? "UP" : "DOWN";
        BigDecimal percentChange = currentPrice.compareTo(BigDecimal.ZERO) > 0 ?
            predictedPrice.subtract(currentPrice)
                .divide(currentPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)) :
            BigDecimal.ZERO;

        String confidence = getConfidence(history, prediction[1]);

        return PriceForecastDTO.builder()
            .productName(productName)
            .currentPrice(currentPrice)
            .predictedPrice(predictedPrice.setScale(2, RoundingMode.HALF_UP))
            .trendDirection(trend)
            .percentChange(percentChange.abs().setScale(2, RoundingMode.HALF_UP))
            .daysToForecast((long) daysAhead)
            .confidence(confidence)
            .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPriceTrend(String productName, int months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);

        List<PriceHistory> history = priceHistoryRepository.getPriceHistoryByRange(productName, startDate, endDate);

        if (history.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> trend = new HashMap<>();
        trend.put("productName", productName);
        trend.put("startDate", startDate);
        trend.put("endDate", endDate);
        trend.put("startPrice", history.get(0).getPrice());
        trend.put("endPrice", history.get(history.size() - 1).getPrice());

        BigDecimal percentChange = history.get(history.size() - 1).getPrice()
            .subtract(history.get(0).getPrice())
            .divide(history.get(0).getPrice(), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));

        trend.put("percentChange", percentChange.setScale(2, RoundingMode.HALF_UP));
        trend.put("direction", percentChange.compareTo(BigDecimal.ZERO) > 0 ? "UP" : "DOWN");
        trend.put("dataPoints", history.size());

        return trend;
    }

    private BigDecimal[] linearRegression(List<PriceHistory> history, int daysAhead) {
        int n = history.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i;
            double y = history.get(i).getPrice().doubleValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        double predictedValue = intercept + slope * (n - 1 + daysAhead);

        double rSquared = calculateRSquared(history, slope, intercept);

        return new BigDecimal[]{
            BigDecimal.valueOf(Math.max(0, predictedValue)),
            BigDecimal.valueOf(rSquared)
        };
    }

    private double calculateRSquared(List<PriceHistory> history, double slope, double intercept) {
        int n = history.size();
        double meanY = history.stream()
            .mapToDouble(ph -> ph.getPrice().doubleValue())
            .average()
            .orElse(0);

        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < n; i++) {
            double y = history.get(i).getPrice().doubleValue();
            double yPred = intercept + slope * i;
            ssRes += Math.pow(y - yPred, 2);
            ssTot += Math.pow(y - meanY, 2);
        }

        return ssTot > 0 ? 1 - (ssRes / ssTot) : 0;
    }

    private String getConfidence(List<PriceHistory> history, BigDecimal rSquared) {
        double rSq = rSquared.doubleValue();

        if (history.size() < 5) {
            return "LOW";
        } else if (rSq > 0.8) {
            return "HIGH";
        } else if (rSq > 0.5) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    @Transactional(readOnly = true)
    public List<PriceForecastDTO> forecastMultipleProducts(List<String> productNames, int daysAhead) {
        return productNames.stream()
            .map(name -> forecastPrice(name, daysAhead))
            .collect(Collectors.toList());
    }

    /**
     * Prédiction de consommation basée sur l'historique d'achats
     */
    @Transactional(readOnly = true)
    public Map<String, Object> forecastConsumption(String productName, int daysAhead) {
        List<PriceHistory> history = priceHistoryRepository.findByProductNameOrderByPriceDateDesc(productName);

        if (history.size() < 2) {
            Map<String, Object> result = new HashMap<>();
            result.put("productName", productName);
            result.put("confidence", "LOW");
            result.put("message", "Pas assez de données pour prédire la consommation");
            return result;
        }

        // Trier par date
        history.sort(Comparator.comparing(PriceHistory::getPriceDate));

        // Calculer la fréquence moyenne d'achat (en jours)
        LocalDate firstPurchase = history.get(0).getPriceDate();
        LocalDate lastPurchase = history.get(history.size() - 1).getPriceDate();
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(firstPurchase, lastPurchase);
        int purchaseCount = history.size();

        double averageFrequency = purchaseCount > 1 ? (double) totalDays / (purchaseCount - 1) : 0;

        // Prédire le nombre d'achats futurs
        int predictedPurchases = averageFrequency > 0 ? (int) Math.ceil(daysAhead / averageFrequency) : 0;

        // Calculer la date probable du prochain achat
        LocalDate nextPurchaseDate = averageFrequency > 0 ?
            lastPurchase.plusDays((long) averageFrequency) : null;

        // Niveau de confiance
        String confidence = purchaseCount >= 5 ? "HIGH" : (purchaseCount >= 3 ? "MEDIUM" : "LOW");

        Map<String, Object> result = new HashMap<>();
        result.put("productName", productName);
        result.put("totalPurchases", purchaseCount);
        result.put("averageFrequencyDays", Math.round(averageFrequency * 10.0) / 10.0);
        result.put("predictedPurchases", predictedPurchases);
        result.put("nextPurchaseDate", nextPurchaseDate);
        result.put("daysToForecast", daysAhead);
        result.put("confidence", confidence);
        result.put("firstPurchase", firstPurchase);
        result.put("lastPurchase", lastPurchase);

        return result;
    }
}


