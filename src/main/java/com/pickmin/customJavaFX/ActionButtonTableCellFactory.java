package com.pickmin.customJavaFX;

import java.io.IOException;
import java.util.ArrayList;

import com.pickmin.App;
import com.pickmin.controllers.ProductOverviewController;
import com.pickmin.exceptions.InvalidParametersControllerException;
import com.pickmin.logic.model.BranchProduct;
import com.pickmin.logic.model.Customer;
import com.pickmin.logic.model.Employee;
import com.pickmin.logic.model.Parameter;
import com.pickmin.logic.model.ParameterKey;
import com.pickmin.logic.model.Product;
import com.pickmin.logic.model.ProductStatus;
import com.pickmin.logic.model.ShoppingList;
import com.pickmin.logic.model.User;
import com.pickmin.logic.model.UserManagement;
import com.pickmin.translation.TranslationHelper;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ActionButtonTableCellFactory implements Callback<TableColumn<BranchProduct, Void>, TableCell<BranchProduct, Void>> {
    private final ShoppingList shoppingList;
    private final ProductOverviewController controller;

    public ActionButtonTableCellFactory(ProductOverviewController controller) {
        this.shoppingList = UserManagement.getLoggedInUserShoppingList();
        this.controller = controller;
    }

    @Override
    public TableCell<BranchProduct, Void> call(final TableColumn<BranchProduct, Void> param) {
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
                        addButtonStyling(addButton);
                        actionButtons.getChildren().setAll(addButton, viewButton);
                    } else if (loggedInUser instanceof Employee) { // check of de ingelogde gebruiker een medewerker is
                        actionButtons.getChildren().setAll(editButton, deleteButton);
                    }
                    setGraphic(actionButtons);
                }
            }

            // Button actie: Product toevoegen aan shoppinglist
            private void handleAddToShoppingList() {
                BranchProduct product = getTableView().getItems().get(getIndex());
                if (shoppingList != null) {
                    ProductStatus productStatus = product.getStatus();
                    if (productStatus.isAvailable()) {
                        shoppingList.addProduct(product);
                        System.out.println("Product toegevoegd aan shoppinglist");
                    } else {
                        System.out.println(TranslationHelper.get(productStatus.getTranslationKey()));
                    }
                }
            }

            // Button actie: Product bekijken
            private void handleViewProduct() {
                BranchProduct product = getTableView().getItems().get(getIndex());
                if (product != null) {
                    product.increaseTimesViewed();
                    controller.fillProductTable();
                    System.out.println("Product bekeken: " + product.getName());
                }
            }

            // Button actie: Product aanpassen
            private void handleEditProduct() {
                BranchProduct product = getTableView().getItems().get(getIndex());
                if (product != null) {
                    goToEditProductPage(product);
                }
            }

            // Button actie: Product verwijderen
            private void handleDeleteProduct() {
                throw new UnsupportedOperationException("Unimplemented method 'handleDeleteProduct'");
                // BranchProduct product = getTableView().getItems().get(getIndex());
                // if (product != null) {
                //     Inventory.removeProduct(product);
                //     controller.fillProductTable();
                //     System.out.println("Product verwijderd: " + product.getName());
                // }
            }

            private void addButtonStyling(Button addButton) {
                BranchProduct product = getTableView().getItems().get(getIndex());
                addButton.getStyleClass().removeAll("button-unavailable", "button-available");
                addButton.setDisable(false);

                ProductStatus productStatus = product.getStatus();
                if (productStatus.isAvailable()) {
                    addButton.getStyleClass().add("button-available");
                    addButton.setDisable(false);
                } else {
                    addButton.getStyleClass().add("button-unavailable");
                    addButton.setDisable(true);
                }
            }

            private void goToEditProductPage(Product product) {
                ArrayList<Parameter<?>> params = new ArrayList<>();
                params.add(new Parameter<>(ParameterKey.PRODUCT, product));

                try {
                    App.goToPage("EditProduct", params);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidParametersControllerException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
