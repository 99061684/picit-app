package com.pickmin.exceptions;

public class NoAccesException extends Exception {
    public NoAccesException() {
        super("Geen toegang tot deze functionaliteit.");
    }
}
