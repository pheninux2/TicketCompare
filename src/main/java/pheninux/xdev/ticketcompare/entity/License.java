package pheninux.xdev.ticketcompare.entity;

import jakarta.persistence.*;
import lombok.*;
import pheninux.xdev.ticketcompare.enums.LicenseType;
import pheninux.xdev.ticketcompare.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "licenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LicenseType licenseType;

    @Column(nullable = false, unique = true, length = 100)
    private String licenseKey;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate expiryDate; // null pour LIFETIME

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private Boolean autoRenew = false;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = SubscriptionStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si la licence est active
     */
    public boolean isActive() {
        if (status != SubscriptionStatus.ACTIVE) {
            return false;
        }

        // LIFETIME n'expire jamais
        if (licenseType == LicenseType.LIFETIME) {
            return true;
        }

        // Vérifier la date d'expiration
        return expiryDate != null && !LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Vérifie si la licence est expirée
     */
    public boolean isExpired() {
        if (licenseType == LicenseType.LIFETIME) {
            return false;
        }
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Obtient le nombre de jours restants
     */
    public long getDaysRemaining() {
        if (licenseType == LicenseType.LIFETIME) {
            return -1; // Illimité
        }

        if (expiryDate == null) {
            return 0;
        }

        long days = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        return Math.max(0, days);
    }

    /**
     * Vérifie si la licence expire bientôt (moins de 7 jours)
     */
    public boolean isExpiringSoon() {
        if (licenseType == LicenseType.LIFETIME) {
            return false;
        }
        long daysRemaining = getDaysRemaining();
        return daysRemaining > 0 && daysRemaining <= 7;
    }

    /**
     * Renouvelle la licence
     */
    public void renew() {
        if (licenseType == LicenseType.LIFETIME) {
            return; // Pas de renouvellement nécessaire
        }

        LocalDate newExpiryDate;
        if (isExpired()) {
            // Si déjà expiré, partir d'aujourd'hui
            newExpiryDate = LocalDate.now().plusDays(licenseType.getValidityDays());
        } else {
            // Sinon, étendre à partir de la date d'expiration actuelle
            newExpiryDate = expiryDate.plusDays(licenseType.getValidityDays());
        }

        this.expiryDate = newExpiryDate;
        this.status = SubscriptionStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Annule la licence
     */
    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.autoRenew = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Suspend la licence
     */
    public void suspend() {
        this.status = SubscriptionStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Réactive la licence
     */
    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
}

