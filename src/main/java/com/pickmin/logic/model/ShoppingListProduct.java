package com.pickmin.logic.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ShoppingListProduct extends BranchProduct {
    private int amountInShoppingList;

    public ShoppingListProduct(BranchProduct branchProduct, int amountInShoppingList) {
        super(branchProduct, branchProduct.getIdBranchProduct(), branchProduct.getStock(), branchProduct.getStatus());
        this.amountInShoppingList = amountInShoppingList;
    }

    public ShoppingListProduct(BranchProduct branchProduct) {
        this(branchProduct, 1);
    }

    public double calculateTotalPrice() {
        return this.amountInShoppingList * this.getPrice();
    }

    // Getters and setters
    public int getAmountInShoppingList() {
        return amountInShoppingList;
    }

    public void setAmountInShoppingList(int amountInShoppingList) {
        if (getStock() < amountInShoppingList) {
            throw new IllegalArgumentException("amountInShoppingList must be smaller than the stock of the product");
        }
        this.amountInShoppingList = amountInShoppingList;
    }

    public void increaseAmountInShoppingList() {
        this.amountInShoppingList++;
    }

    // Getters voor JavaFX Properties
    public IntegerProperty getAmountInShoppingListProperty() {
        return new SimpleIntegerProperty(this.amountInShoppingList);
    }

    public DoubleProperty getTotalPriceProperty() {
        return new SimpleDoubleProperty(this.calculateTotalPrice());
    }

}
