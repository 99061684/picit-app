package com.pickmin.controllers;

import java.util.ArrayList;
import java.util.Optional;

import org.controlsfx.control.SearchableComboBox;

import com.pickmin.customJavaFX.ActionButtonTableCellFactory;
import com.pickmin.exceptions.CustomIllegalArgumentException;
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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
    private TableColumn<BranchProduct, ProductStatus> productStatusColumn;
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

    private String lastSelectedCity;
    private static final int maxProductsToShow = 5;

    @FXML
    private void initialize() {
        // Configuratie van de tabelkolommen en initialiseren van de productlijst
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        productStatusColumn.setCellValueFactory(cellData -> cellData.getValue().getStatusProperty());
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
            // availabilityColumn.setVisible(false);
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
        cityComboBox.valueProperty().addListener((obs, oldValue, newValue) -> filterProductsByCity(branchFilters, newValue, oldValue));
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

    private void filterProductsByCity(ArrayList<BranchFilter> branchFilters, String selectedCity, String oldValue) {
        if (selectedCity == null || selectedCity.isEmpty()) {
            Platform.runLater(() -> {
                restoreLastSelectedCity();
            });
        } else {
            if (selectedCity.equals(lastSelectedCity)) {
                return;
            }
            ShoppingList shoppingList = UserManagement.getLoggedInUserShoppingList();
            ArrayList<ShoppingListProduct> missingProducts = new ArrayList<>();

            BranchFilter branchFilter = UtilityFunctions.getBranchFilterFromArrayListByCity(branchFilters, selectedCity);
            if (shoppingList != null) {
                missingProducts = JsonHandler.getShoppingListProductsMissingInBranch(branchFilter.getId(), shoppingList);
            }            

            if (shoppingList == null || missingProducts.isEmpty()) {
                InventoryManagement.setInventory(new Inventory(branchFilter.getId()));
                fillProductTable();
                lastSelectedCity = selectedCity;
            } else {
                boolean userProceeded = showMissingProductsAlert(missingProducts, branchFilter.getCity(), shoppingList);

                if (userProceeded) {
                    shoppingList.removeMissingProducts(missingProducts);
                    InventoryManagement.setInventory(new Inventory(branchFilter.getId()));
                    fillProductTable();
                    lastSelectedCity = selectedCity;
                } else {
                    Platform.runLater(() -> {
                        restoreLastSelectedCity();
                    });
                }
            }

        }
    }

    private void restoreLastSelectedCity() {
        if (lastSelectedCity != null) {
            cityComboBox.setValue(lastSelectedCity);
        }
    }

    private static String generateConflictMessage(ArrayList<ShoppingListProduct> missingProducts, String branchName) {
        StringBuilder productNames = new StringBuilder();

        boolean isTruncated = missingProducts.size() > ProductOverviewController.maxProductsToShow;
        int productsToShow = isTruncated ? ProductOverviewController.maxProductsToShow : missingProducts.size();

        for (int i = 0; i < productsToShow; i++) {
            if (i > 0) {
                productNames.append(", ");
            }
            productNames.append(missingProducts.get(i).getName());
        }

        if (isTruncated) {
            return TranslationHelper.get("branch.switch.conflict.truncated", missingProducts.size(), productsToShow, productNames.toString());
        } else if (missingProducts.size() == 1) {
            return TranslationHelper.get("branch.switch.conflict.singular", productNames.toString(), branchName);
        }
        return TranslationHelper.get("branch.switch.conflict.plural", productNames.toString(), branchName);
    }

    // boolean return is for: userProceeded
    public static boolean showMissingProductsAlert(ArrayList<ShoppingListProduct> missingProducts, String branchName, ShoppingList shoppingList) {
        if (shoppingList == null || missingProducts.isEmpty()) {
            throw new CustomIllegalArgumentException("showMissingProductsAlert");
        }

        String conflictMessage = generateConflictMessage(missingProducts, branchName);

        if (conflictMessage != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(TranslationHelper.get("branch.switch.conflict.title"));
            alert.setHeaderText(null);

            String footer = TranslationHelper.get("branch.switch.conflict.footer");
            Text textMessage = new Text(conflictMessage + "\n\n");
            Text textFooter = new Text(footer);

            TextFlow textFlow = new TextFlow(textMessage, textFooter);
            alert.getDialogPane().setContent(textFlow);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);

            ButtonType proceedButton = new ButtonType(TranslationHelper.get("button.continue"));
            ButtonType cancelButton = new ButtonType(TranslationHelper.get("button.cancel"));
            alert.getButtonTypes().setAll(proceedButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == proceedButton) {
                shoppingList.removeMissingProducts(missingProducts);
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

}
