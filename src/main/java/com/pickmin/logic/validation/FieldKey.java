package com.pickmin.logic.validation;

public enum FieldKey {
    PRODUCT_NAME("productName"),
    PRODUCT_CATEGORIE("categorie"),
    PRODUCT_DESCRIPTION("productDescription"),
    PRODUCT_ORIGIN("origin"),
    PRODUCT_RIPENING_DATE("ripeningDate"),
    PRODUCT_SEASONS("season"),
    PRODUCT_STOCK_NL("stockNL"),
    PRODUCT_PRICE("price"),

    BRANCH_PRODUCT_STOCK("stock"),

    USERNAME("username"),
    PASSWORD("password");

    private final String fieldName;

    FieldKey(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return this.fieldName;
    }

    public static FieldKey getFieldKeyFromType(String fieldName) {
        for (FieldKey fieldKey : FieldKey.values()) {
            if (fieldKey.getFieldName().equalsIgnoreCase(fieldName)) {
                return fieldKey;
            }
        }
        return null;
    }
}