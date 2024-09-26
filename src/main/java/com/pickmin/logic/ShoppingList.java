package com.pickmin.logic;

import java.util.ArrayList;

import com.pickmin.config.GlobalConfig;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ShoppingList {
    private ArrayList<ShoppingListProduct> shoppingItems;

    public ShoppingList() {
        this.shoppingItems = new ArrayList<>();
    }

    public ShoppingList(ArrayList<ShoppingListProduct> shoppingItems) {
        this.shoppingItems = shoppingItems;
    }

    public void addProduct(Product product) {
        ShoppingListProduct shoppingListProduct = findShoppingListProductById(product.getId());
        if (shoppingListProduct != null) {
            shoppingListProduct.increaseAmountInShoppingList();
        } else {
            shoppingItems.add(new ShoppingListProduct(product));
        }
        if (GlobalConfig.SAVE_SHOPPINGLIST) {
            JsonHandler.saveShoppingListToJson(UserManagement.getLoggedInUser().getId(), shoppingItems);
        }
    }

    public void removeProduct(Product product) {
        ShoppingListProduct shoppingListProduct = findShoppingListProductById(product.getId());
        if (shoppingListProduct != null) {
            shoppingItems.remove(shoppingListProduct);

            if (GlobalConfig.SAVE_SHOPPINGLIST) {
                JsonHandler.saveShoppingListToJson(UserManagement.getLoggedInUser().getId(), shoppingItems);
            }
        }
    }

    public void removeShoppingListProduct(ShoppingListProduct shoppingListProduct) {
        if (shoppingListProduct != null) {
            shoppingItems.remove(shoppingListProduct);

            if (GlobalConfig.SAVE_SHOPPINGLIST) {
                JsonHandler.saveShoppingListToJson(UserManagement.getLoggedInUser().getId(), shoppingItems);
            }
        }
    }

    public ArrayList<ShoppingListProduct> getShoppingItems() {
        return shoppingItems;
    }

    public ObservableList<ShoppingListProduct> getShoppingItemsObservableList() {
        return FXCollections.observableArrayList(shoppingItems);
    }

    public ShoppingListProduct findShoppingListProductById(String id) {
        for (ShoppingListProduct shoppingListProduct : shoppingItems) {
            if (shoppingListProduct.getId().equals(id)) {
                return shoppingListProduct;
            }
        }
        return null;
    }

    public double calculateTotalPrice() {
        return this.shoppingItems.stream().mapToDouble(shoppingListProduct -> shoppingListProduct.calculateTotalPrice()).sum();
    }
}
