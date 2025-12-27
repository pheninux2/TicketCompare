package pheninux.xdev.ticketcompare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "statistic_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maxPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal averagePrice;

    @Column(nullable = false)
    private Long productCount;

    @Column(nullable = false)
    private LocalDate snapshotDate;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        if (snapshotDate == null) {
            snapshotDate = LocalDate.now();
        }
    }
}

