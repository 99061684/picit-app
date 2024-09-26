package com.pickmin.controllers;

import com.pickmin.customJavaFX.ActionButtonTableCellFactory2;
import com.pickmin.exceptions.NoAccesException;
import com.pickmin.logic.Customer;
import com.pickmin.logic.FormattingHelper;
import com.pickmin.logic.ShoppingListProduct;
import com.pickmin.logic.User;
import com.pickmin.logic.UserManagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ShoppingListController {

    @FXML
    private TableView<ShoppingListProduct> productTable;
    @FXML
    private TableColumn<ShoppingListProduct, String> productNameColumn;
    @FXML
    private TableColumn<ShoppingListProduct, Boolean> availabilityColumn;
    @FXML
    private TableColumn<ShoppingListProduct, Integer> amountInShoppingListColumn;
    @FXML
    private TableColumn<ShoppingListProduct, Double> priceColumn;
    @FXML
    private TableColumn<ShoppingListProduct, Double> totalPriceColumn;
    @FXML
    private TableColumn<ShoppingListProduct, Void> actionColumn;

    @FXML
    private Label totalLabel;

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
        amountInShoppingListColumn.setCellValueFactory(cellData -> cellData.getValue().getAmountInShoppingListProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().getPriceProperty().asObject());
        totalPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalPriceProperty().asObject());

        actionColumn.setCellFactory(new ActionButtonTableCellFactory2(this));

        // Gebruik de FormattingHelper voor de cell factories
        availabilityColumn.setCellFactory(FormattingHelper.availabilityCellFactory());
        priceColumn.setCellFactory(FormattingHelper.priceCellFactory());
        totalPriceColumn.setCellFactory(FormattingHelper.priceCellFactory());

        setTotalLabel();

        fillShoppingListProductTable();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // vult de productTable met data. Kan gebruikt worden om de data in de productTable te herladen na een aanpassing bij de data.
    public void fillShoppingListProductTable() {
        productTable.setItems(this.customer.getShoppingList().getShoppingItemsObservableList());
        productTable.refresh();
        setTotalLabel();
    }

    private void setTotalLabel() {
        totalLabel.setText(FormattingHelper.formatPrice(this.customer.getShoppingList().calculateTotalPrice()));
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
