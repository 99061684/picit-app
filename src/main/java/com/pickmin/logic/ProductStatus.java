package com.pickmin.logic;

public enum ProductStatus {
    TEMPORARILY_UNAVAILABLE(1, "product.temporarily_unavailable", "Tijdelijk niet leverbaar", "Temporarily unavailable", false),
    OUT_OF_STOCK(2, "product.out_of_stock", "Geen voorraad", "Out of stock", false),
    NOT_AVAILABLE(3, "product.not_available", "Niet beschikbaar", "Not available", false),
    UNKNOWN(4, "product.unknown", "Onbekend", "Unknown", false),
    AVAILABLE(5, "product.available", "Beschikbaar", "Available", true);

    private final int id;
    private final String translationKey;
    private final String nameNl;
    private final String nameEn;
    private final boolean available;

    ProductStatus(int id, String translationKey, String nameNl, String nameEn, boolean available) {
        this.id = id;
        this.translationKey = translationKey;
        this.nameNl = nameNl;
        this.nameEn = nameEn;
        this.available = available;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public String getNameNl() {
        return nameNl;
    }

    public String getNameEn() {
        return nameEn;
    }

    public boolean isAvailable() {
        return available;
    }

    // Haal status op via ID
    public static ProductStatus getById(int id) {
        for (ProductStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        return UNKNOWN; // Fallback naar UNKNOWN als ID niet wordt herkend
    }
}