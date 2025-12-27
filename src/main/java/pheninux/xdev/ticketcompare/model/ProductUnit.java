package pheninux.xdev.ticketcompare.model;

/**
 * Énumération des unités de mesure
 */
public enum ProductUnit {
    KG("kg", "Kilogramme"),
    G("g", "Gramme"),
    L("L", "Litre"),
    ML("mL", "Millilitre"),
    PIECE("pièce", "Pièce"),
    PAQUET("paquet", "Paquet"),
    BOITE("boîte", "Boîte"),
    BOUTEILLE("bouteille", "Bouteille"),
    SACHET("sachet", "Sachet"),
    LOT("lot", "Lot"),
    BOTTE("botte", "Botte"),
    UNITE("unité", "Unité");

    private final String symbol;
    private final String displayName;

    ProductUnit(String symbol, String displayName) {
        this.symbol = symbol;
        this.displayName = displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return symbol;
    }
}

