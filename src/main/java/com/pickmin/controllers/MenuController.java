package com.pickmin.controllers;

import java.io.IOException;

import com.pickmin.App;
import com.pickmin.logic.model.Customer;
import com.pickmin.logic.model.Employee;
import com.pickmin.logic.model.User;
import com.pickmin.logic.model.UserManagement;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class MenuController {

    @FXML
    private MenuItem shoppingListMenuItem;
    @FXML
    private MenuItem addProductMenuItem;

    @FXML
    private void initialize() {
        // Controleer de rol van de ingelogde gebruiker
        User loggedInUser = UserManagement.getLoggedInUser();
        if (loggedInUser instanceof Employee) { //verberg voor medewerkers
            shoppingListMenuItem.setVisible(false);
        } else if (loggedInUser instanceof Customer) { //verberg voor klanten
            addProductMenuItem.setVisible(false);
        }
    }

    @FXML
    private void handleLogout() {
        // Logica voor uitloggen
        User temp = UserManagement.getLoggedInUser();
        UserManagement.logout();
        try {
            App.goToPage("Login");
        } catch (IOException e) {
            e.printStackTrace();
            UserManagement.setLoggedInUser(temp);
        }
    }

    // Functie om naar het productoverzicht te navigeren
    @FXML
    private void goToProductOverview() {
        try {
            App.goToPage("ProductOverview"); // Navigatie naar productoverzicht
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Functie om naar de boodschappenlijst te navigeren
    @FXML
    private void goToShoppingList() {
        try {
            App.goToPage("ShoppingListPage"); // Navigatie naar boodschappenlijstpagina
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Functie om naar de "product toevoegen pagina" te navigeren
    @FXML
    private void goToAddProduct() {
        try {
            App.goToPage("AddProduct"); // Navigatie naar "product toevoegen pagina"
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

