package com.pickmin.exceptions;

public class ExistingProductException extends Exception {
    public ExistingProductException(String productName) {
        super("Product '" + productName + "' bestaat al.");
    }
}
