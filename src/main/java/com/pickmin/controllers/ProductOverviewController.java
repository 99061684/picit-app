package com.pickmin.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.controlsfx.control.SearchableComboBox;

import com.pickmin.customJavaFX.ActionButtonTableCellFactory;
import com.pickmin.logic.general.UtilityFunctions;
import com.pickmin.logic.json.JsonHandler;
import com.pickmin.logic.model.BranchProduct;
import com.pickmin.logic.model.Inventory;
import com.pickmin.logic.model.InventoryManagement;
import com.pickmin.logic.model.ProductStatus;
import com.pickmin.logic.model.ShoppingList;
import com.pickmin.logic.model.ShoppingListProduct;
import com.pickmin.logic.model.User;
import com.pickmin.logic.model.UserManagement;
import com.pickmin.logic.model.UserType;
import com.pickmin.logic.model.filter.BranchFilter;
import com.pickmin.logic.validation.FormattingHelper;
import com.pickmin.translation.TranslationHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ProductOverviewController {

    @FXML
    private TextField searchField;
    @FXML
    private SearchableComboBox<String> cityComboBox;
    @FXML
    private TableView<BranchProduct> productTable;
    @FXML
    private TableColumn<BranchProduct, String> productNameColumn;
    @FXML
    private TableColumn<BranchProduct, ProductStatus> availabilityColumn;
    @FXML
    private TableColumn<BranchProduct, Integer> timesViewedColumn;
    @FXML
    private TableColumn<BranchProduct, String> seasonColumn;
    @FXML
    private TableColumn<BranchProduct, Integer> stockColumn;
    @FXML
    private TableColumn<BranchProduct, Double> priceColumn;
    @FXML
    private TableColumn<BranchProduct, Void> actionColumn;

    public ProductOverviewController() {
    }

    @FXML
    private void initialize() {
        // Configuratie van de tabelkolommen en initialiseren van de productlijst
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        // availabilityColumn.setCellValueFactory(cellData -> cellData.getValue().getAvailabilityProperty().asObject());
        availabilityColumn.setCellValueFactory(cellData -> cellData.getValue().getStatusProperty());
        timesViewedColumn.setCellValueFactory(cellData -> cellData.getValue().getTimesViewedProperty().asObject());
        seasonColumn.setCellValueFactory(cellData -> cellData.getValue().getSeasonProperty());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().getStockProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().getPriceProperty().asObject());

        actionColumn.setCellFactory(new ActionButtonTableCellFactory(this));

        // Gebruik de FormattingHelper voor de cell factories
        // availabilityColumn.setCellFactory(FormattingHelper.availabilityCellFactory());
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

        fillCityComboBox();
    }

    private void fillCityComboBox() {
        ObservableList<String> cities = FXCollections.observableArrayList();
        ArrayList<BranchFilter> branchFilters = JsonHandler.getBranchFilters();
        branchFilters.forEach(filter -> cities.add(filter.toString()));
        cityComboBox.setItems(cities);
        cityComboBox.valueProperty().addListener((obs, oldValue, newValue) -> filterProductsByCity(branchFilters, newValue));
        cityComboBox.getSelectionModel().selectFirst();
    }

    // vult de productTable met data. Kan gebruikt worden om de data in de
    // productTable te herladen na een aanpassing bij de data.
    public void fillProductTable() {
        productTable.setItems(InventoryManagement.getBranchProductsObservableList());
        productTable.refresh();
    }

    @FXML
    private void handleSearch() {
        // String query = searchField.getText();
        // Voeg zoeklogica toe voor producten
    }

    private void filterProductsByCity(ArrayList<BranchFilter> branchFilters, String selectedCity) {
        if (selectedCity == null || selectedCity.isEmpty()) {
            InventoryManagement.setInventory(null);
            fillProductTable();
        } else {
            BranchFilter branchFilter = UtilityFunctions.getBranchFilterFromArrayListByCity(branchFilters, selectedCity);
            InventoryManagement.setInventory(new Inventory(branchFilter.getId()));
            fillProductTable();
        }
    }

    public static String generateConflictMessage(List<ShoppingListProduct> conflictingProducts, String branchName) {
        int maxDisplayCount = 5;
        if (conflictingProducts.isEmpty()) {
            return null;
        }

        String productNames = conflictingProducts.stream().limit(maxDisplayCount).map(ShoppingListProduct::getName).collect(Collectors.joining(", "));

        if (conflictingProducts.size() > maxDisplayCount) {
            return TranslationHelper.get("shoppingList.changeBranch.conflict.truncated", productNames, conflictingProducts.size() - maxDisplayCount, branchName);
        }

        return TranslationHelper.get("shoppingList.changeBranch.conflict", productNames, branchName);
    }

    public static void showMissingProductsAlert(String branchId) {
        ShoppingList shoppingList = UserManagement.getLoggedInUserShoppingList();

        if (shoppingList == null) {
            return;
        }

        ArrayList<ShoppingListProduct> missingProducts = JsonHandler.getShoppingListProductsMissingInBranch(branchId, shoppingList);
        String message = generateConflictMessage(missingProducts, branchId);

        if (message != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Producten ontbreken");
            alert.setHeaderText(null);
            alert.setContentText(message);

            ButtonType proceedButton = new ButtonType("Doorgaan");
            ButtonType cancelButton = new ButtonType("Annuleren");
            alert.getButtonTypes().setAll(proceedButton, cancelButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == proceedButton) {
                    shoppingList.removeMissingProducts(missingProducts);
                }
            });
        }
    }

}
