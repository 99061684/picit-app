package com.pickmin.exceptions;

public class ExistingUserException extends Exception {
    public ExistingUserException(String username) {
        super("Gebruiker '" + username + "' bestaat al.");
    }
}