package com.pickmin.logic.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class BranchProduct extends Product {
    private String idBranchProduct;
    private int stock;
    private ProductStatus status;

    public BranchProduct(Product product, String idBranchProduct, int stock, ProductStatus status) {
        super(product.getId(), product.getName(), product.getDescription(), product.getOrigin(), product.getRipeningDate(), product.getTimesViewed(), product.getSeasonsArrayList(), product.getStockNL(), product.getPrice(), product.getCategorieObject(), product.getUnit());
        this.idBranchProduct = idBranchProduct;
        this.status = status;
        setStock(stock);
    }

    // Getters en Setters
    public String getIdBranchProduct() {
        return idBranchProduct;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (getStockNL() < stock) {
            throw new IllegalArgumentException("stock must be smaller than the entire stock of the product in NL");
        } else if (stock < 0) {
            throw new IllegalArgumentException("Stock must be more than 0");
        } else if (stock == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else if (stock > 0 && this.status.equals(ProductStatus.OUT_OF_STOCK)) {
            this.status = ProductStatus.AVAILABLE;
        }
        this.stock = stock;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    // Getters voor JavaFX Properties
    public IntegerProperty getStockProperty() {
        return new SimpleIntegerProperty(this.getStock());
    }

    public ObjectProperty<ProductStatus> getStatusProperty() {
        return new SimpleObjectProperty<ProductStatus>(this.getStatus());
    }
}
