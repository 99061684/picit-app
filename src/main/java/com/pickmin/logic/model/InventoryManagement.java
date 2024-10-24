package com.pickmin.logic.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InventoryManagement {
    private static Inventory inventory;

    public static ObservableList<BranchProduct> getBranchProductsObservableList() {
        if (!hasInventory()) {
            return FXCollections.emptyObservableList();
        }
        return getInventory().getBranchProductsObservableList();
    }

    public static boolean hasInventory() {
        return getInventory() != null;
    }

    // Getters and Setters
    public static Inventory getInventory() {
        return inventory;
    }

    public static void setInventory(Inventory inventory) {
        InventoryManagement.inventory = inventory;
    }
}
