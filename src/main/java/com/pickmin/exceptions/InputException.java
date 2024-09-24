package com.pickmin.exceptions;

import com.pickmin.logic.FieldKey;

public abstract class InputException extends Exception {
    private final FieldKey fieldKey;
    
    public InputException(String message, FieldKey fieldKey) {
        super(message);
        this.fieldKey = fieldKey;
    }

    public FieldKey getFieldKey() {
        return fieldKey;
    }
}
