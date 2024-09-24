package com.pickmin.translation;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.pickmin.config.GlobalConfig;

public class TranslationHelper {
    private static ResourceBundle bundle;
    private static ResourceBundle defaultBundle;

    // Stel de taal in en laad de ResourceBundle (bestand met vertalingen van een bepaalde taal)
    public static void setLanguage(Language language) {
        Locale locale = Locale.forLanguageTag(language.getCode());
        bundle = ResourceBundle.getBundle("com.pickmin.translation.translation", locale);

        // Laad de standaardtaal (zoals ingesteld in GlobalConfig)
        Locale defaultLocale = Locale.forLanguageTag(GlobalConfig.DEFAULT_LANGUAGE.getCode());
        defaultBundle = ResourceBundle.getBundle("com.pickmin.translation.translation", defaultLocale);
    }

    // Haal een vertaling op, gebruik de standaardtaal als de sleutel niet bestaat
    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // Als de vertaling niet bestaat in de geselecteerde taal, gebruik de standaardtaal
            try {
                return defaultBundle.getString(key);
            } catch (MissingResourceException ex) {
                // Als de sleutel ook niet bestaat in de standaardtaal, geef de sleutel zelf terug
                return key;
            }
        }
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static ResourceBundle getDefaultBundle() {
        return defaultBundle;
    }

    // Standaard taal instellen bij de eerste keer laden
    static {
        setLanguage(GlobalConfig.DEFAULT_LANGUAGE);
    }
}
