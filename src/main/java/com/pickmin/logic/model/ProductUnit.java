package com.pickmin.logic.model;

public enum ProductUnit {
    PER_PIECE("Per stuk"),
    PER_KG("Per kg");

    private final String description;

    ProductUnit(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ProductUnit getByDescription(String description) {
        for (ProductUnit productUnit : values()) {
            if (productUnit.description.equals(description)) {
                return productUnit;
            }
        }
        return null;
    }

    public static ProductUnit getDefault() {
        return ProductUnit.PER_KG;
    }

    @Override
    public String toString() {
        return description;
    }
}
