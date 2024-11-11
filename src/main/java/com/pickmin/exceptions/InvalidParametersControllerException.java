package com.pickmin.exceptions;

public class InvalidParametersControllerException extends Exception {
    public InvalidParametersControllerException(String fxml) {
        super("The specified controller does not support parameters: " + fxml);
    }
}