package com.pickmin.logic.model;

import java.util.ArrayList;

import com.pickmin.config.GlobalConfig;
import com.pickmin.logic.general.UtilityFunctions;
import com.pickmin.logic.json.JsonHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ShoppingList {
    private String id;
    private String userId;
    private ArrayList<ShoppingListProduct> shoppingListProducts;

    public ShoppingList(String userId) {
        this.id = UtilityFunctions.generateID();
        this.userId = userId;
        this.shoppingListProducts = new ArrayList<>();
    }

    public ShoppingList(String userId, ArrayList<ShoppingListProduct> shoppingListProducts) {
        this.id = UtilityFunctions.generateID();
        this.userId = userId;
        this.shoppingListProducts = shoppingListProducts;
    }

    public ShoppingList(String id, String userId, ArrayList<ShoppingListProduct> shoppingListProducts) {
        this.id = id;
        this.userId = userId;
        this.shoppingListProducts = shoppingListProducts;
    }

    public void addProduct(BranchProduct product) {
        ShoppingListProduct shoppingListProduct = findShoppingListProductById(product.getId());
        if (shoppingListProduct != null) {
            shoppingListProduct.increaseAmountInShoppingList();
        } else {
            shoppingListProducts.add(new ShoppingListProduct(product));
        }
        if (GlobalConfig.SAVE_SHOPPINGLIST) {
            JsonHandler.saveShoppingListToJson(this);
        }
    }

    public void removeProduct(BranchProduct product) {
        ShoppingListProduct shoppingListProduct = findShoppingListProductById(product.getId());
        if (shoppingListProduct != null) {
            shoppingListProducts.remove(shoppingListProduct);

            if (GlobalConfig.SAVE_SHOPPINGLIST) {
                JsonHandler.saveShoppingListToJson(this);
            }
        }
    }

    public void removeShoppingListProduct(ShoppingListProduct shoppingListProduct) {
        if (shoppingListProduct != null) {
            shoppingListProducts.remove(shoppingListProduct);

            if (GlobalConfig.SAVE_SHOPPINGLIST) {
                JsonHandler.saveShoppingListToJson(this);
            }
        }
    }

    public void removeMissingProducts(ArrayList<ShoppingListProduct> missingProducts) {
        missingProducts.forEach(this::removeShoppingListProduct);
    }

    public ShoppingListProduct findShoppingListProductById(String id) {
        for (ShoppingListProduct shoppingListProduct : shoppingListProducts) {
            if (shoppingListProduct.getId().equals(id)) {
                return shoppingListProduct;
            }
        }
        return null;
    }

    public double calculateTotalPrice() {
        return this.shoppingListProducts.stream().mapToDouble(shoppingListProduct -> shoppingListProduct.calculateTotalPrice()).sum();
    }

    // Getters and Setters
    public ArrayList<ShoppingListProduct> getShoppingListProducts() {
        return shoppingListProducts;
    }

    public ObservableList<ShoppingListProduct> getShoppingListProductsObservableList() {
        return FXCollections.observableArrayList(shoppingListProducts);
    }

    public String getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }
}
