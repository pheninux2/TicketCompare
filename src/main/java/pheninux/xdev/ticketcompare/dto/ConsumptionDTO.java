package pheninux.xdev.ticketcompare.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionDTO {
    private String productName;
    private LocalDate weekStart;
    private BigDecimal totalQuantity;
    private BigDecimal totalCost;
    private Long purchaseCount;
    private String category;
    private String unit;
    private BigDecimal averagePricePerUnit;

    // Détails des achats individuels
    @Builder.Default
    private List<PurchaseDetail> purchaseDetails = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseDetail {
        private LocalDate date;
        private String store;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal totalPrice;
    }
}

