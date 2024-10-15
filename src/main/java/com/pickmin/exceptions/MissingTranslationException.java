package com.pickmin.exceptions;

import java.util.MissingResourceException;

import com.pickmin.translation.TranslationHelper;

import javafx.fxml.LoadException;

public class MissingTranslationException extends Exception {

    public MissingTranslationException(String missingResource) {
        super(TranslationHelper.get("warning.MissingTranslationException", missingResource));
    }

    public MissingTranslationException(LoadException loadException) {
        super(TranslationHelper.get("warning.MissingTranslationException", extractMissingResource(loadException)));
    }

    public MissingTranslationException(MissingResourceException missingResourceException) {
        super(TranslationHelper.get("warning.MissingTranslationException", missingResourceException.getKey()));
    }

    // Hulpmethode om de ontbrekende resource uit de foutmelding te halen
    public static String extractMissingResource(LoadException loadException) {
        String errorMessage = loadException.getMessage();

        if (errorMessage.contains("Resource") && errorMessage.contains("not found")) {
            int startIndex = errorMessage.indexOf('"') + 1;
            int endIndex = errorMessage.lastIndexOf('"');

            return errorMessage.substring(startIndex, endIndex).trim();
        }
        return null;
    }
}
