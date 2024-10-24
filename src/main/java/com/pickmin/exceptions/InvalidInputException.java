package com.pickmin.exceptions;

import com.pickmin.logic.validation.FieldKey;
import com.pickmin.translation.TranslationHelper;

public class InvalidInputException extends InputException {
    public InvalidInputException(FieldKey fieldKey) {
        super(TranslationHelper.get("validation.invalid." + fieldKey), fieldKey);
    }
}
