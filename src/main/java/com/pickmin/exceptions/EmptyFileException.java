package com.pickmin.exceptions;

public class EmptyFileException extends Exception {
    public EmptyFileException() {
        super("Bestand is leeg.");
    }

    public EmptyFileException(String file) {
        super("Het bestand: '"+file+"' is leeg.");
    }
}
