package com.pickmin.translation;

public enum Language {
    DUTCH("nl", "Nederlands"),
    ENGLISH("en", "English");

    private final String code;
    private final String displayName;

    Language(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // Methode om een Language te verkrijgen op basis van de code
    public static Language getLanguageFromCode(String code) {
        for (Language lang : Language.values()) {
            if (lang.getCode().equalsIgnoreCase(code)) {
                return lang;
            }
        }
        return null; // Kan eventueel een standaard taal teruggeven
    }
}