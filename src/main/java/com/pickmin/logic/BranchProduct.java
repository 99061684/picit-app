package com.pickmin.logic;

public class BranchProduct extends Product {
    private int stock;
    private ProductStatus status;

    public BranchProduct(Product product, int stock, ProductStatus status) {
        super(product.getId(), product.getName(), product.isAvailable(), product.getRipeningDate(), product.getTimesViewed(), product.getSeason(), product.getStock(), product.getPrice());
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
