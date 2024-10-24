package com.pickmin.logic.validation;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pickmin.config.GlobalConfig;
import com.pickmin.exceptions.ExistingProductException;
import com.pickmin.exceptions.InvalidInputException;
import com.pickmin.exceptions.MissingFieldException;
import com.pickmin.logic.general.UtilityFunctions;
import com.pickmin.logic.json.JsonHandler;
import com.pickmin.logic.model.ProductCategorie;
import com.pickmin.logic.model.ProductUnit;

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
    public static void validateNotEmpty(Object input, FieldKey fieldKey) throws MissingFieldException {
        if (input == null) {
            throw new MissingFieldException(fieldKey);
        }
    }

    public static void validateNotEmpty(String input, FieldKey fieldKey) throws MissingFieldException {
        if (input == null || input.trim().isEmpty()) {
            throw new MissingFieldException(fieldKey);
        }
    }

    public static void validateNotEmpty(ArrayList<String> input, FieldKey fieldKey) throws MissingFieldException {
        if (input == null || input.isEmpty()) {
            throw new MissingFieldException(fieldKey);
        }

        for (String item : input) {
            validateNotEmpty(item, fieldKey);
        }
    }

    public static void validateProduct(String id, String name, String description, String origin, LocalDate ripeningDate, ArrayList<String> seasons, int stockNL, double price, ProductCategorie categorie) throws MissingFieldException, InvalidInputException, ExistingProductException {
        validateNotEmpty(name, FieldKey.PRODUCT_NAME);
        checkExistingProduct(name, id, FieldKey.PRODUCT_NAME);
        validateNotEmpty(description, FieldKey.PRODUCT_DESCRIPTION);
        validateNotEmpty(origin, FieldKey.PRODUCT_NAME);
        validateNotEmpty(ripeningDate, FieldKey.PRODUCT_RIPENING_DATE);
        validateNotEmpty(seasons, FieldKey.PRODUCT_SEASONS);
        validateStock(stockNL, FieldKey.PRODUCT_STOCK_NL);
        validatePrice(price, FieldKey.PRODUCT_PRICE);
        validateNotEmpty(categorie, FieldKey.PRODUCT_CATEGORIE);
    }

    public static void checkExistingProduct(String name, String idToExclude, FieldKey fieldKey) throws ExistingProductException {
        HashMap<String, String> productNames = JsonHandler.getProductNames();
        if (idToExclude != null && UtilityFunctions.hasProductName(productNames, name, idToExclude)) {
            throw new ExistingProductException(name);
        } else if (idToExclude == null && UtilityFunctions.hasProductName(productNames, name)) {
            throw new ExistingProductException(name);
        }        
    }

    public static void validateStock(Integer stock, FieldKey fieldKey) throws MissingFieldException, InvalidInputException {
        validateNotEmpty(stock, fieldKey);
        if (stock < 0) {
            throw new InvalidInputException(fieldKey);
        }
    }

    public static void validatePrice(Double price, FieldKey fieldKey) throws MissingFieldException, InvalidInputException {
        validateNotEmpty(price, fieldKey);
        if (price < 0 || Double.isNaN(price) || UtilityFunctions.countDecimalPlaces(price) > 2) {
            throw new InvalidInputException(fieldKey);
        }
    }

    public static void validateLogin(String username, String password) throws MissingFieldException, InvalidInputException {
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

    public static void validateAccountCreation(String username, String password)
            throws MissingFieldException, InvalidInputException {
        validateLogin(username, password);
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

    public static boolean isValidAddressFormat(String address) {
        // Regex voor adrescontrole: straat huisnummer, postcode en stad
        String addressRegex = "^[A-Za-z\\s]+\\d+[,\\s]+\\d{4}\\s?[A-Z]{2}\\s[A-Za-z\\s]+$";

        return address.matches(addressRegex);
    }
}
