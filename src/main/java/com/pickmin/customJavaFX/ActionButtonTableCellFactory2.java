package com.pickmin.customJavaFX;

import java.util.Optional;

import com.pickmin.controllers.ShoppingListController;
import com.pickmin.logic.model.ShoppingList;
import com.pickmin.logic.model.ShoppingListProduct;
import com.pickmin.logic.model.UserManagement;
import com.pickmin.translation.TranslationHelper;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ActionButtonTableCellFactory2 implements Callback<TableColumn<ShoppingListProduct, Void>, TableCell<ShoppingListProduct, Void>> {
    private final ShoppingList shoppingList;
    private final ShoppingListController controller;

    public ActionButtonTableCellFactory2(ShoppingListController controller) {
        this.shoppingList = UserManagement.getLoggedInUserShoppingList();
        this.controller = controller;
    }

    @Override
    public TableCell<ShoppingListProduct, Void> call(final TableColumn<ShoppingListProduct, Void> param) {
        return new TableCell<>() {
            private final Button editButton = new Button(TranslationHelper.get("shoppinglist.action.edit"));
            private final Button deleteButton = new Button(TranslationHelper.get("shoppinglist.action.delete"));
            private final HBox actionButtons = new HBox(5);

            {
                // Toewijzen van button-acties
                editButton.setOnAction(event -> handleEditShoppingListProduct());
                deleteButton.setOnAction(event -> handleDeleteShoppingListProduct());
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    actionButtons.getChildren().setAll(editButton, deleteButton);
                    setGraphic(actionButtons);
                }
            }

            private void handleEditShoppingListProduct() {
                ShoppingListProduct selectedProduct = getTableView().getItems().get(getIndex());
                if (selectedProduct != null) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Hoeveelheid Aanpassen");
                    dialog.setHeaderText("Pas de hoeveelheid aan voor " + selectedProduct.getName());
                    dialog.setContentText("Voer de nieuwe hoeveelheid in:");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(amountString -> {
                        try {
                            int newAmount = Integer.parseInt(amountString);
                            selectedProduct.setAmountInShoppingList(newAmount);
                        } catch (NumberFormatException e) {
                            System.out.println("Ongeldige hoeveelheid ingevoerd.");
                        }
                    });
                }
                controller.fillShoppingListProductTable();
            }

            // Button actie: ShoppingListProduct verwijderen
            private void handleDeleteShoppingListProduct() {
                ShoppingListProduct product = getTableView().getItems().get(getIndex());
                if (product != null) {
                    shoppingList.removeShoppingListProduct(product);
                    controller.fillShoppingListProductTable();
                }
            }
        };
    }
}
