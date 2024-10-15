package com.pickmin.exceptions;

import com.pickmin.translation.TranslationHelper;

public class JSONMissingFieldException extends Exception {
    public JSONMissingFieldException(String filename, String arrayKey, String fieldName) {
        super(TranslationHelper.get("error.missingField", filename, arrayKey, fieldName));
    }
}
