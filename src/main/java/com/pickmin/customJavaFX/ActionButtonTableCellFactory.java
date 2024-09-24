package com.pickmin.customJavaFX;

import java.io.IOException;
import java.util.HashMap;

import com.pickmin.App;
import com.pickmin.controllers.ProductOverviewController;
import com.pickmin.exceptions.InvalidParametersControllerException;
import com.pickmin.logic.Customer;
import com.pickmin.logic.Employee;
import com.pickmin.logic.Inventory;
import com.pickmin.logic.Product;
import com.pickmin.logic.ShoppingList;
import com.pickmin.logic.User;
import com.pickmin.logic.UserManagement;
import com.pickmin.translation.TranslationHelper;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ActionButtonTableCellFactory implements Callback<TableColumn<Product, Void>, TableCell<Product, Void>> {
    private final ShoppingList shoppingList;
    private final ProductOverviewController controller;

    public ActionButtonTableCellFactory(ProductOverviewController controller) {
        User loggedInUser = UserManagement.getLoggedInUser();
        if (loggedInUser instanceof Customer) {
            this.shoppingList = ((Customer) loggedInUser).getShoppingList();
        } else {
            this.shoppingList = null;
        }
        this.controller = controller;
    }

    @Override
    public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
        return new TableCell<>() {
            private final Button addButton = new Button(TranslationHelper.get("product.action.add.shoppinglist"));
            private final Button viewButton = new Button(TranslationHelper.get("product.action.view"));
            private final Button editButton = new Button(TranslationHelper.get("product.action.edit"));
            private final Button deleteButton = new Button(TranslationHelper.get("product.action.delete"));
            private final HBox actionButtons = new HBox(5);

            {
                // Toewijzen van button-acties
                addButton.setOnAction(event -> handleAddToShoppingList());
                viewButton.setOnAction(event -> handleViewProduct());
                editButton.setOnAction(event -> handleEditProduct());
                deleteButton.setOnAction(event -> handleDeleteProduct());
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User loggedInUser = UserManagement.getLoggedInUser();
                    if (loggedInUser instanceof Customer) { // check of de ingelogde gebruiker een klant is
                        actionButtons.getChildren().setAll(addButton, viewButton);
                        addButtonStyling(addButton);
                    } else if (loggedInUser instanceof Employee) { // check of de ingelogde gebruiker een medewerker is
                        actionButtons.getChildren().setAll(editButton, deleteButton);
                    }
                    setGraphic(actionButtons);
                }
            }

            // Button actie: Product toevoegen aan shoppinglist
            private void handleAddToShoppingList() {
                Product product = getTableView().getItems().get(getIndex());
                if (shoppingList != null) {
                    if (product.isAvailable() && product.getStock() > 0) {
                        shoppingList.addProduct(product);
                        System.out.println("Product toegevoegd aan shoppinglist");
                    } else {
                        System.out.println("Product is niet beschikbaar of uit voorraad.");
                    }
                }
            }

            // Button actie: Product bekijken
            private void handleViewProduct() {
                Product product = getTableView().getItems().get(getIndex());
                if (product != null) {
                    product.increaseTimesViewed();
                    System.out.println("Product bekeken: " + product.getName());
                }
            }

            // Button actie: Product aanpassen
            private void handleEditProduct() {
                Product product = getTableView().getItems().get(getIndex());
                if (product != null) {
                    goToEditProductPage(product);
                }
            }

            // Button actie: Product verwijderen
            private void handleDeleteProduct() {
                Product product = getTableView().getItems().get(getIndex());
                if (product != null) {
                    Inventory.removeProduct(product);
                    controller.fillProductTable();
                    System.out.println("Product verwijderd: " + product.getName());
                }
            }

            private void addButtonStyling(Button addButton) {
                Product product = getTableView().getItems().get(getIndex());
                // addButton.getStyleClass().clear();
                if (product.isAvailable()) {
                    addButton.getStyleClass().add("button-available");
                } else {
                    addButton.getStyleClass().add("button-unavailable");
                }
            }

            private void goToEditProductPage(Product product) {
                HashMap<String, Object> args = new HashMap<>();
                args.put("product", product);
                try {
                    App.setRoot("EditProduct", args);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidParametersControllerException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
