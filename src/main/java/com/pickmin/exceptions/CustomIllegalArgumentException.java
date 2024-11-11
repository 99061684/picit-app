package com.pickmin.exceptions;

import com.pickmin.translation.TranslationHelper;

public class CustomIllegalArgumentException extends IllegalArgumentException {

    public CustomIllegalArgumentException(String function) {
        super(TranslationHelper.get("error.function.CustomIllegalArgumentException", function));
    }
    
}
