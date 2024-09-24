package com.pickmin.logic;

public enum FieldKey {
    PRODUCT_NAME("productName"),
    RIPENING_DATE("ripeningDate"),
    SEASON("season"),
    STOCK("stock"),
    PRODUCT_PRICE("price"),
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