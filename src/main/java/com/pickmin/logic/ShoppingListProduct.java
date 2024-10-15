package com.pickmin.logic;

import java.util.ArrayList;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ShoppingListProduct extends Product {
    private int amountInShoppingList;

    public ShoppingListProduct(Product product, int amountInShoppingList) {
        super(product.getId(), product.getName(), product.getDescription(), product.getOrigin(), product.getRipeningDate(), product.getTimesViewed(), product.getSeasonsArrayList(), product.getStockNL(), product.getPrice(), product.getCategorieObject());
        this.amountInShoppingList = amountInShoppingList;
    }

    public ShoppingListProduct(String id, String name, String description, String origin, String ripeningDate, int timesViewed, ArrayList<String> seasons, int stockNL, double price, ProductCategorie categorie, int amountInShoppingList) {
        super(id, name, description, origin, ripeningDate, timesViewed, seasons, stockNL, price, categorie);
        this.amountInShoppingList = amountInShoppingList;
    }

    public ShoppingListProduct(Product product) {
        this(product, 1);
    }

    public double calculateTotalPrice() {
        return this.amountInShoppingList * this.getPrice();
    }

    // Getters and setters
    public int getAmountInShoppingList() {
        return amountInShoppingList;
    }

    public void setAmountInShoppingList(int amountInShoppingList) {
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
