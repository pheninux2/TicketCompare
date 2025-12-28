package pheninux.xdev.ticketcompare.enums;

/**
 * Statut de l'abonnement
 */
public enum SubscriptionStatus {
    ACTIVE("Actif"),
    EXPIRED("Expiré"),
    CANCELLED("Annulé"),
    PENDING("En attente"),
    SUSPENDED("Suspendu");

    private final String displayName;

    SubscriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

