package pheninux.xdev.ticketcompare.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceForecastDTO {
    private String productName;
    private BigDecimal currentPrice;
    private BigDecimal predictedPrice;
    private String trendDirection; // "UP", "DOWN"
    private BigDecimal percentChange;
    private Long daysToForecast;
    private String confidence; // "HIGH", "MEDIUM", "LOW"
}

