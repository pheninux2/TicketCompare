package pheninux.xdev.ticketcompare.entity;

import jakarta.persistence.*;
import lombok.*;
import pheninux.xdev.ticketcompare.enums.PlanType;
import pheninux.xdev.ticketcompare.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanType planType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate nextBillingDate;

    @Column
    private LocalDate endDate;

    @Column(nullable = false)
    private Boolean autoRenew = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String paymentMethod; // "STRIPE", "PAYPAL", "MANUAL", etc.

    @Column(length = 200)
    private String paymentReference; // ID de transaction externe

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = SubscriptionStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si l'abonnement est actif
     */
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE &&
               (endDate == null || !LocalDate.now().isAfter(endDate));
    }

    /**
     * Vérifie si l'abonnement nécessite un renouvellement
     */
    public boolean needsRenewal() {
        return nextBillingDate != null &&
               LocalDate.now().isAfter(nextBillingDate) &&
               autoRenew;
    }

    /**
     * Renouvelle l'abonnement
     */
    public void renew() {
        if (planType.isRecurring()) {
            this.startDate = LocalDate.now();
            this.nextBillingDate = startDate.plusDays(planType.getValidityDays());
            this.status = SubscriptionStatus.ACTIVE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Annule l'abonnement
     */
    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.autoRenew = false;
        this.endDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();
    }
}

