package com.pickmin.logic;

import java.util.ArrayList;
import com.pickmin.translation.Language;
import com.pickmin.config.GlobalConfig;
import com.pickmin.exceptions.ExistingUserException;
import com.pickmin.exceptions.InvalidInputException;
import com.pickmin.exceptions.MissingFieldException;

public class UserManagement {
    private static ArrayList<User> users = new ArrayList<>();
    private static User loggedInUser;

    // Functie die wordt uitgevoerd de eerste keer dat deze class wordt ingeladen.
    static {
        // Users worden één keer geïmporteerd uit de JSON-bestanden
        users = JsonHandler.loadUsersFromFile();
    }

    // getters and setters
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        UserManagement.loggedInUser = loggedInUser;
    }

    public static void logout() {
        UserManagement.setLoggedInUser(null);
    }

    public static boolean userLoggedIn() {
        if (UserManagement.loggedInUser != null) {
            return true;
        }
        return false;
    }

    // class functions
    public void createUser(String username, String password, UserType userType, Language preferredLanguage)
            throws ExistingUserException, MissingFieldException, InvalidInputException {
        if (getUserByUsername(username) != null) {
            throw new ExistingUserException(username);
        }
        User user = null;

        // Validate the gegevens bij het aanmaken van een account
        Validation.validateAccountCreation(username, password);

        // Maak een beveiligd wachtwoord.
        String encryptedPassword = Validation.encodePassword(password);

        if (userType.equals(UserType.EMPLOYEE)) {
            user = new Employee(username, encryptedPassword, preferredLanguage);
        } else {
            user = new Customer(username, encryptedPassword, preferredLanguage);
        }
        users.add(user);
        JsonHandler.saveUsersToFile(users);

        System.out.println("Gebruiker succesvol aangemaakt!");
    }

    public User loginUser(String username, String password, Language preferredLanguage) {
        User user = getUserByUsername(username);

        if (user != null && user.comparePassword(password)) {
            // Pas de taal van de gebruiker aan, sla de user op en zet de taal van de
            // applicatie naar de taal van de user.
            user.setPreferredLanguage(preferredLanguage);
            JsonHandler.saveUsersToFile(users);
            GlobalConfig.setLanguage(user.getPreferredLanguage());

            setLoggedInUser(user);
            System.out.println("Succesvol ingelogd!");
            return user;
        } else {
            System.out.println("Ongeldige gebruikersnaam of wachtwoord.");
            return null;
        }
    }

    private User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
