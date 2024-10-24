package com.pickmin.logic.model;

public class ProductCategorie {
    private String name;

    public ProductCategorie(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
