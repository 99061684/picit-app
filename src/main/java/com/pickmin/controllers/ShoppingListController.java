package com.pickmin.controllers;

import com.pickmin.exceptions.NoAccesException;
import com.pickmin.logic.Customer;
import com.pickmin.logic.FormattingHelper;
import com.pickmin.logic.Product;
import com.pickmin.logic.User;
import com.pickmin.logic.UserManagement;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ShoppingListController {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, Boolean> availabilityColumn;
    @FXML
    private TableColumn<Product, String> seasonColumn;
    @FXML
    private TableColumn<Product, Integer> stockColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Void> actionColumn;

    private Customer customer;

    @FXML
    private void initialize() throws NoAccesException {
        User loggedInUser = UserManagement.getLoggedInUser();
        if (loggedInUser instanceof Customer) {
            this.customer = (Customer) loggedInUser;
        } else {
            throw new NoAccesException();
        }
        // Configuratie van de tabelkolommen en initialiseren van de productlijst
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        availabilityColumn.setCellValueFactory(cellData -> cellData.getValue().getAvailabilityProperty().asObject());
        seasonColumn.setCellValueFactory(cellData -> cellData.getValue().getSeasonProperty());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().getStockProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().getPriceProperty().asObject());

        // Gebruik de FormattingHelper voor de cell factories
        availabilityColumn.setCellFactory(FormattingHelper.availabilityCellFactory());
        priceColumn.setCellFactory(FormattingHelper.priceCellFactory());

        fillProductTable();
        productTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    // vult de productTable met data. Kan gebruikt worden om de data in de productTable te herladen na een aanpassing bij de data. 
    public void fillProductTable() {
        productTable.setItems(this.customer.getShoppingList().getShoppingItemsObservableList());
    }

    @FXML
    private void handleRemoveFromShoppingList() {
        // Verwijder geselecteerd item uit boodschappenlijst
    }

    @FXML
    private void handleCheckout() {
        // Implementatie voor afrekenen
    }
}
