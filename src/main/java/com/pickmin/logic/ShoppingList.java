package com.pickmin.logic;

import java.util.ArrayList;

import com.pickmin.config.GlobalConfig;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ShoppingList {
    private ArrayList<Product> shoppingItems;

    public ShoppingList() {
        this.shoppingItems = new ArrayList<>();
    }

    public ShoppingList(ArrayList<Product> shoppingItems) {
        this.shoppingItems = shoppingItems;
    }

    public void addProduct(Product product) {
        shoppingItems.add(product);
        if (GlobalConfig.SAVE_SHOPPINGLIST) {
            JsonHandler.saveShoppingList(UserManagement.getLoggedInUser().getId(), shoppingItems);
        }
    }

    public void removeProduct(Product product) {
        shoppingItems.remove(product);
        if (GlobalConfig.SAVE_SHOPPINGLIST) {
            JsonHandler.saveShoppingList(UserManagement.getLoggedInUser().getId(), shoppingItems);
        }
    }

    public ArrayList<Product> getShoppingItems() {
        return shoppingItems;
    }

    public ObservableList<Product> getShoppingItemsObservableList() {
        return FXCollections.observableArrayList(shoppingItems);
    }
}
