package com.pickmin.logic;

import java.security.SecureRandom;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pickmin.config.GlobalConfig;
import com.pickmin.exceptions.ExistingProductException;
import com.pickmin.exceptions.InvalidInputException;
import com.pickmin.exceptions.MissingFieldException;

public class Validation {
    private static final String passwordRegex;
    private static final int bcryptStrength;

    static {
        // --- Regex voor een wachtwoord met de volgende eisen: ---
        // Minimaal één kleine letter (a-z).
        // Minimaal één hoofdletter (A-Z).
        // Minimaal één cijfer (0-9).
        // Minimaal één speciaal teken uit de set [@$!%*?&].
        // De string moet minimaal 8 tekens lang zijn.
        // Alleen de toegestane tekens (letters, cijfers, en speciaal tekens) mogen
        // voorkomen.
        passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        bcryptStrength = GlobalConfig.BCRYPT_STRENGTH;
    }

    // Controleer of een veld niet leeg is
    public static void validateNotEmpty(String input, FieldKey fieldKey) throws MissingFieldException {
        if (input == null || input.trim().isEmpty()) {
            throw new MissingFieldException(fieldKey);
        }
    }

    // Validatie voor zoeken (zoekveld mag niet leeg zijn)
    // public static void validateSearchQuery(String input) throws InvalidInputException {
    //     if (input == null || input.trim().isEmpty()) {
    //         throw new InvalidInputException("validation.invalidSearchQuery");
    //     }
    // }

    // Validatie voor gebruikersnaam en wachtwoord bij inloggen
    public static void validateProduct(String name, boolean isAvailable, String ripeningDate, String season, Integer stock, Double price) throws MissingFieldException, InvalidInputException, ExistingProductException {
        validateNotEmpty(name, FieldKey.PRODUCT_NAME);
        validateNotEmpty(ripeningDate, FieldKey.RIPENING_DATE);
        validateNotEmpty(season, FieldKey.SEASON);
        validateStock(stock);
        validatePrice(price);
        if (Inventory.findProductByName(name) != null) {
            throw new ExistingProductException(name);
        }
    }

    public static void validateStock(Integer stock) throws MissingFieldException, InvalidInputException {
        if (stock == null) {
            throw new MissingFieldException(FieldKey.STOCK);
        }
        if (stock < 0) {
            throw new InvalidInputException(FieldKey.STOCK);
        }
    }

    public static void validatePrice(Double price) throws MissingFieldException, InvalidInputException {
        if (price == null) {
            throw new MissingFieldException(FieldKey.PRODUCT_PRICE);
        }
        if (price < 0 || Double.isNaN(price) || countDecimalPlaces(price) > 2) {
            throw new InvalidInputException(FieldKey.PRODUCT_PRICE);
        }
    }

    public static int countDecimalPlaces(double value) {
        if (Math.round(value) == value) {
            return 0;
        }
        final String s = Double.toString(value);
        final int index = s.indexOf(',');
        if (index < 0) {
            return 0;
        }
        return s.length() - 1 - index;
    }

    // Validatie voor gebruikersnaam en wachtwoord bij inloggen
    public static void validateLogin(String username, String password)
            throws MissingFieldException, InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new MissingFieldException(FieldKey.USERNAME);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new MissingFieldException(FieldKey.PASSWORD);
        }
        if (GlobalConfig.PASSWORD_CREATE_PATTERN) {
            if (password.matches(passwordRegex) == false) {
                throw new InvalidInputException(FieldKey.PASSWORD);
            }
        }
    }

    // Validatie voor account aanmaken
    public static void validateAccountCreation(String username, String password)
            throws MissingFieldException, InvalidInputException {
        validateLogin(username, password); // Hergebruik validatie voor login
    }

    public static String encodePassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(bcryptStrength, new SecureRandom());
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        return encodedPassword;
    }

    public static boolean checkEncodedPassword(String password, String encodedPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(bcryptStrength, new SecureRandom());
        return bCryptPasswordEncoder.matches(password, encodedPassword);
    }
}
