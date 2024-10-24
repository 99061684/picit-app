package com.pickmin.logic.model;

import java.util.ArrayList;

import com.pickmin.logic.json.JsonHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Inventory {
    private String currentBranchId;
    private Branch currentBranch;
    private ArrayList<BranchProduct> branchProducts;

    public Inventory(String currentBranchId) {
        this.currentBranchId = currentBranchId;
        branchProducts = JsonHandler.loadBranchProductsFromJson(currentBranchId);
    }

    public Inventory(Branch currentBranch) {
        this.currentBranchId = currentBranch.getId();
        this.currentBranch = currentBranch;
        branchProducts = JsonHandler.loadBranchProductsFromJson(currentBranchId);
    }

    // public static void addBranchProduct(BranchProduct product) {
    //     branchProducts.add(product);
    //     if (GlobalConfig.SAVE_PRODUCTS_AFTER_CREATE) {
    //         JsonHandler.saveProductsToJson(branchProducts);
    //     }        
    // }

    public Product findProductByName(String name) {
        for (Product product : this.branchProducts) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    public Product findProductById(String id) {
        for (Product product : branchProducts) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    // public static void removeBranchProduct(Product product) {
    //     branchProducts.remove(product);
    //     if (GlobalConfig.SAVE_PRODUCTS_AFTER_DELETE) {
    //         JsonHandler.saveProductsToJson(branchProducts);
    //     }
    // }

    // public static void saveBranchProducts() {
    //     JsonHandler.saveProductsToJson(branchProducts);
    // }

    public ArrayList<BranchProduct> getBranchProducts() {
        return branchProducts;
    }

    public ObservableList<BranchProduct> getBranchProductsObservableList() {
        return FXCollections.observableArrayList(branchProducts);
    }
}
