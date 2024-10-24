package com.pickmin.exceptions;

import com.pickmin.translation.TranslationHelper;

public class JSONMissingObjectException extends Exception {
    public JSONMissingObjectException(String objectName, String fieldName, String fieldValue) {
        super(TranslationHelper.get("error.missingObject", objectName, fieldName, fieldValue));
    }

    public JSONMissingObjectException(String objectName, String query) {
        super(TranslationHelper.get("error.missingObjectFromQuery", objectName, query));
    }
}