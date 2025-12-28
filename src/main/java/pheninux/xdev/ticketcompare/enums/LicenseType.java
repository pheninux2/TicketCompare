package pheninux.xdev.ticketcompare.enums;

/**
 * Type de licence utilisateur
 */
public enum LicenseType {
    TRIAL("Essai gratuit", 30, true),
    MONTHLY("Abonnement mensuel", 30, true),
    YEARLY("Abonnement annuel", 365, true),
    LIFETIME("Licence à vie", -1, false);

    private final String displayName;
    private final int validityDays; // -1 pour illimité
    private final boolean needsRenewal;

    LicenseType(String displayName, int validityDays, boolean needsRenewal) {
        this.displayName = displayName;
        this.validityDays = validityDays;
        this.needsRenewal = needsRenewal;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public boolean isNeedsRenewal() {
        return needsRenewal;
    }

    public boolean isUnlimited() {
        return validityDays == -1;
    }
}

