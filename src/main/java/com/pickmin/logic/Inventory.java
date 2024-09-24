package com.pickmin.logic;

import java.util.ArrayList;

import com.pickmin.config.GlobalConfig;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Inventory {
    private static ArrayList<Product> products;

    // Functie die wordt uitgevoerd de eerste keer dat deze class wordt ingeladen.
    static {
        // Products worden één keer geïmporteerd uit de JSON-bestanden 
        products = JsonHandler.importProducts();
    }

    public static void addProduct(Product product) {
        products.add(product);
        if (GlobalConfig.SAVE_PRODUCTS_AFTER_CREATE) {
            JsonHandler.exportProducts(products);
        }        
    }

    public static Product findProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    public static void removeProduct(Product product) {
        products.remove(product);
        if (GlobalConfig.SAVE_PRODUCTS_AFTER_DELETE) {
            JsonHandler.exportProducts(products);
        }
    }

    public static void saveProducts() {
        JsonHandler.exportProducts(products);
    }

    public static ArrayList<Product> getProducts() {
        return products;
    }

    public static ObservableList<Product> getProductsObservableList() {
        return FXCollections.observableArrayList(products);
    }
}
