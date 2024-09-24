package com.pickmin.exceptions;

public class FoundProductException extends Exception {
    public FoundProductException() {
        super("Product niet gevonden.");
    }
}