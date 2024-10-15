package com.pickmin.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import com.pickmin.translation.Language;
import com.pickmin.translation.TranslationHelper;

public class GlobalConfig {
    private static final String SECRET_CONFIGURATION_FILE = "secret.properties";
    private static final Properties properties;

    // Bestandslocaties
    public static String OUTSIDE_DATA_FILE_PATH;
    public static String DATA_FILE_PATH;
    public static String USERS_FILE_PATH;
    public static String PRODUCTS_FILE_PATH;
    public static String EXTERN_PRODUCTS_FILE_PATH;
    public static String EXTERN_BRANCHES_FILE_PATH;
    public static String TEST_PRODUCTS_FILE_PATH;
    public static String TEST_BRANCHES_FILE_PATH;

    // functionaliteiten
    public static final Boolean SAVE_PRODUCTS_AFTER_CREATE = true;
    public static final Boolean SAVE_PRODUCTS_AFTER_CHANGE = true;
    public static final Boolean SAVE_PRODUCTS_AFTER_DELETE = false;
    public static final Boolean SAVE_SHOPPINGLIST = false;
    public static final Boolean LOAD_SHOPPINGLIST = false;
    public static final Boolean SAVE_BRANCHES_AFTER_CREATE = true;
    public static final Boolean SAVE_BRANCHES_AFTER_CHANGE = true;
    public static final Boolean SAVE_BRANCHES_AFTER_DELETE = false;
    public static final Boolean SAVE_BRANCH_PRODUCTS_AFTER_CREATE = true;
    public static final Boolean SAVE_BRANCH_PRODUCTS_AFTER_CHANGE = true;
    public static final Boolean SAVE_BRANCH_PRODUCTS_AFTER_DELETE = false;

    // console output
    public static final Boolean EMPTY_FILE_ERROR_CONSOLE = true;
    public static final Boolean EMPTY_FILE_ERROR_PATH_CONSOLE = true;
    public static final Boolean NO_TRANSLATION_KEY_ERROR_CONSOLE = false;

    // Schermconfiguratie
    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;

    // Validation
    public static final int BCRYPT_STRENGTH = 10;
    public static final Boolean PASSWORD_CREATE_PATTERN = true;

    // Standaard taal van de applicatie
    public static final Language DEFAULT_LANGUAGE = Language.DUTCH;

    // Huidige taal (kan tijdens runtime veranderen)
    public static Language currentLanguage = DEFAULT_LANGUAGE;

    // Methode om de huidige taal te wijzigen
    public static void setLanguage(Language language) {
        currentLanguage = language;
        TranslationHelper.setLanguage(language);
    }

    static {
        properties = new Properties();
        loadSecretConfigurationFile();
        configureVariables();
    }

    public static void configureVariables() {
        OUTSIDE_DATA_FILE_PATH = getConfigurationValue("GlobalConfig.OUTSIDE_DATA_FILE_PATH");
        DATA_FILE_PATH = getConfigurationValue("GlobalConfig.DATA_FILE_PATH");
        USERS_FILE_PATH = DATA_FILE_PATH + "users.json";
        PRODUCTS_FILE_PATH = DATA_FILE_PATH + "products.json";
        EXTERN_PRODUCTS_FILE_PATH = OUTSIDE_DATA_FILE_PATH + "vruchtenlijst.json";
        EXTERN_BRANCHES_FILE_PATH = OUTSIDE_DATA_FILE_PATH + "vestigingen.json";
        TEST_PRODUCTS_FILE_PATH = DATA_FILE_PATH + "test-products.json";
        TEST_BRANCHES_FILE_PATH = DATA_FILE_PATH + "test-vestigingen.json";
    }

    public static void loadSecretConfigurationFile() {
        try (InputStream inputStream = GlobalConfig.class.getResourceAsStream(SECRET_CONFIGURATION_FILE)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file " + SECRET_CONFIGURATION_FILE, e);
        }
    }

    public static HashMap<String, Object> getConfiguration() {
        HashMap<String, Object> map = new HashMap<>();

        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }

        return map;
    }

    public static String getConfigurationValue(String key) {
        return properties.getProperty(key);
    }
}