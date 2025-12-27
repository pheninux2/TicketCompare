package pheninux.xdev.ticketcompare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "consumption_statistics", indexes = {
    @Index(name = "idx_product_week", columnList = "product_name, week_start")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String productName;

    @Column(nullable = false)
    private LocalDate weekStart;

    @Column(nullable = false)
    private BigDecimal totalQuantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(nullable = false)
    private Long purchaseCount;

    @Column(length = 100)
    private String category;

    @Column(length = 50)
    private String unit;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}

