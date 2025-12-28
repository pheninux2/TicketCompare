package pheninux.xdev.ticketcompare.enums;

import java.math.BigDecimal;

/**
 * Plans tarifaires disponibles
 */
public enum PlanType {
    TRIAL("Essai Gratuit", BigDecimal.ZERO, 30, "Essayez gratuitement pendant 1 mois"),
    MONTHLY("Mensuel", new BigDecimal("4.99"), 30, "Abonnement mensuel renouvelable"),
    YEARLY("Annuel", new BigDecimal("49.99"), 365, "Abonnement annuel - Économisez 17%"),
    LIFETIME("À Vie", new BigDecimal("149.99"), -1, "Paiement unique - Accès illimité");

    private final String displayName;
    private final BigDecimal price;
    private final int validityDays;
    private final String description;

    PlanType(String displayName, BigDecimal price, int validityDays, String description) {
        this.displayName = displayName;
        this.price = price;
        this.validityDays = validityDays;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFree() {
        return price.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isRecurring() {
        return this == MONTHLY || this == YEARLY;
    }

    public LicenseType toLicenseType() {
        switch (this) {
            case TRIAL: return LicenseType.TRIAL;
            case MONTHLY: return LicenseType.MONTHLY;
            case YEARLY: return LicenseType.YEARLY;
            case LIFETIME: return LicenseType.LIFETIME;
            default: throw new IllegalStateException("Unknown plan type: " + this);
        }
    }
}

