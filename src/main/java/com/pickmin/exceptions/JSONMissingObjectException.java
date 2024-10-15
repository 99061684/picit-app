package com.pickmin.exceptions;

import com.pickmin.translation.TranslationHelper;

public class JSONMissingObjectException extends Exception {
    public JSONMissingObjectException(String objectName, String fieldname, String id) {
        super(TranslationHelper.get("error.missingObject", objectName, id));        
    }
}