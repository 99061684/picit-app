package com.pickmin.controllers;

import com.pickmin.customJavaFX.ActionButtonTableCellFactory;
import com.pickmin.logic.FormattingHelper;
import com.pickmin.logic.Inventory;
import com.pickmin.logic.Product;
import com.pickmin.logic.User;
import com.pickmin.logic.UserManagement;
import com.pickmin.logic.UserType;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ProductOverviewController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, Boolean> availabilityColumn;
    @FXML
    private TableColumn<Product, Integer> timesViewedColumn;
    @FXML
    private TableColumn<Product, String> seasonColumn;
    @FXML
    private TableColumn<Product, Integer> stockColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Void> actionColumn;

    public ProductOverviewController() {
    }

    @FXML
    private void initialize() {
        // Configuratie van de tabelkolommen en initialiseren van de productlijst
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        availabilityColumn.setCellValueFactory(cellData -> cellData.getValue().getAvailabilityProperty().asObject());
        timesViewedColumn.setCellValueFactory(cellData -> cellData.getValue().getTimesViewedProperty().asObject());
        seasonColumn.setCellValueFactory(cellData -> cellData.getValue().getSeasonProperty());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().getStockProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().getPriceProperty().asObject());

        actionColumn.setCellFactory(new ActionButtonTableCellFactory(this));

        // Gebruik de FormattingHelper voor de cell factories
        availabilityColumn.setCellFactory(FormattingHelper.availabilityCellFactory());
        priceColumn.setCellFactory(FormattingHelper.priceCellFactory());

        User loggedInUser = UserManagement.getLoggedInUser();  
        // Bepaal wat niet zichtbaar is voor k
        if (loggedInUser.getUserType() == UserType.CUSTOMER) {
            availabilityColumn.setVisible(false);
            timesViewedColumn.setVisible(false);
            stockColumn.setVisible(false);
        }

        fillProductTable();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // vult de productTable met data. Kan gebruikt worden om de data in de
    // productTable te herladen na een aanpassing bij de data.
    public void fillProductTable() {
        productTable.setItems(Inventory.getProductsObservableList());
        productTable.refresh();
    }

    @FXML
    private void handleSearch() {
        // String query = searchField.getText();
        // Voeg zoeklogica toe voor producten
    }
}
