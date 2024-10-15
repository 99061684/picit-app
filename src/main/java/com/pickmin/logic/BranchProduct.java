package com.pickmin.logic;

import java.util.ArrayList;

public class BranchProduct extends Product {
    private int stock;
    private ProductStatus status;

    public BranchProduct(Product product, int stock, ProductStatus status) {
        super(product.getId(), product.getName(), product.getDescription(), product.getOrigin(), product.getRipeningDate(), product.getTimesViewed(), product.getSeasonsArrayList(), product.getStockNL(), product.getPrice(), product.getCategorieObject());
        this.stock = stock;
        this.status = status;
    }

    public BranchProduct(String id, String name, String description, String origin, String ripeningDate, int timesViewed, ArrayList<String> seasons, int stockNL, double price, ProductCategorie categorie, int stock, ProductStatus status) {
        super(id, name, description, origin, ripeningDate, timesViewed, seasons, stockNL, price, categorie);
        this.stock = stock;
        this.status = status;
    }

    // Getters en Setters
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
