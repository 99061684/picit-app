package com.pickmin.controllers;

import java.io.IOException;

import com.pickmin.App;
import com.pickmin.config.GlobalConfig;
import com.pickmin.exceptions.ExistingUserException;
import com.pickmin.exceptions.InvalidInputException;
import com.pickmin.exceptions.MissingFieldException;
import com.pickmin.logic.model.User;
import com.pickmin.logic.model.UserManagement;
import com.pickmin.logic.model.UserType;
import com.pickmin.translation.Language;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<Language> languageComboBox;
    @FXML
    private Label errorMessage;

    private UserManagement userManagement = new UserManagement();

    @FXML
    private void initialize() {
        // Zet de prompt tekst dynamisch op basis van de taal
        languageComboBox.getItems().setAll(Language.values());
        languageComboBox.getSelectionModel().select(GlobalConfig.DEFAULT_LANGUAGE);
    }

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Language preferredLanguage = languageComboBox.getSelectionModel().getSelectedItem();

        User user = userManagement.loginUser(username, password, preferredLanguage);
        if (user != null) {
            App.goToPage("ProductOverview");
            System.out.println("Succesvol ingelogd als " + user.getUsername());
        } else {
            errorMessage.setText("Ongeldige inloggegevens");
        }
    }

    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Language selectedLanguage = languageComboBox.getSelectionModel().getSelectedItem();
        
        try {
            userManagement.createUser(username, password, UserType.CUSTOMER, selectedLanguage);
            errorMessage.setText("Account succesvol aangemaakt!");
        } catch (ExistingUserException e) {
            errorMessage.setText(e.getMessage());
        } catch (MissingFieldException e) {
            errorMessage.setText(e.getMessage());
        } catch (InvalidInputException e) {
            errorMessage.setText(e.getMessage());
        }
    }
}