package com.pickmin.exceptions;

public class InvalidStockPriceException extends Exception {
    private static String errorMessage = "Ongeldige invoer voor voorraad of prijs.";
    
    public InvalidStockPriceException() {
        super(errorMessage);
    }

    public static String getErrorMessage() {
        return errorMessage;
    }    
}