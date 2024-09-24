package com.pickmin.exceptions;

public class InvalidParametersControllerException extends Exception {
    public InvalidParametersControllerException() {
        super("De controller parameters zijn niet valide");
    }
}