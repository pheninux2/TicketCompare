package pheninux.xdev.ticketcompare.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceStatisticDTO {
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal averagePrice;
    private Long productCount;
    private String trend; // "UP", "DOWN", "STABLE"
}

