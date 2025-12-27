package pheninux.xdev.ticketcompare.model;

/**
 * Énumération des catégories de produits
 */
public enum ProductCategory {
    FRUITS_LEGUMES("Fruits & Légumes"),
    VIANDE("Viande"),
    POISSON("Poisson"),
    LAITIER("Laitier"),
    FROMAGERIE("Fromagerie"),
    BOULANGERIE("Boulangerie"),
    PATISSERIE("Pâtisserie"),
    EPICERIE("Épicerie"),
    BOISSONS("Boissons"),
    SURGELES("Surgelés"),
    HYGIENE("Hygiène"),
    ENTRETIEN("Entretien"),
    BEBE("Bébé"),
    ANIMAUX("Animaux"),
    AUTRE("Autre");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

