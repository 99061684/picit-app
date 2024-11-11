package com.pickmin.exceptions;

import com.pickmin.logic.validation.FieldKey;
import com.pickmin.translation.TranslationHelper;

public class MissingFieldException extends InputException {
    public MissingFieldException(FieldKey fieldKey) {
        super(TranslationHelper.get("validation.missingField", fieldKey), fieldKey);
    }
}
