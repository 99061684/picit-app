package com.pickmin.logic;

import java.util.ArrayList;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
    private String id;
    private String name;
    private String description;
    private String origin;
    private String ripeningDate;
    private int timesViewed;
    private ArrayList<String> seasons;
    private int stockNL;
    private double price;

    private ProductCategorie categorie;

    public Product(String id, String name, String description, String origin, String ripeningDate, int timesViewed, ArrayList<String> seasons, int stockNL, double price, ProductCategorie categorie) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.origin = origin;
        this.ripeningDate = ripeningDate;
        this.timesViewed = timesViewed;
        this.seasons = seasons;
        this.stockNL = stockNL;
        this.price = price;
        this.categorie = categorie;
    }

    public Product(String id) {
        this.id = id;
        this.seasons = new ArrayList<>();
    }

    // Getters en Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getRipeningDate() {
        return ripeningDate;
    }

    public void setRipeningDate(String ripeningDate) {
        this.ripeningDate = ripeningDate;
    }

    public int getTimesViewed() {
        return timesViewed;
    }

    public void increaseTimesViewed() {
        this.timesViewed++;
    }

    public String getSeasons() {
        return String.join(", ", this.seasons);
    }

    public ArrayList<String> getSeasonsArrayList() {
        return this.seasons;
    }

    public void setSeasons(ArrayList<String> seasons) {
        this.seasons = seasons;
    }

    public int getStockNL() {
        return this.stockNL;
    }

    public void setStockNL(int stockNL) {
        if (stockNL >= 0) {
            this.stockNL = stockNL;
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    public String getCategorie() {
        return categorie.getName();
    }

    public ProductCategorie getCategorieObject() {
        return categorie;
    }

    public void setCategorie(ProductCategorie categorie) {
        this.categorie = categorie;
    }

    // Getters voor JavaFX Properties
    public StringProperty getNameProperty() {
        return new SimpleStringProperty(this.name);
    }

    public StringProperty getRipeningDateProperty() {
        return new SimpleStringProperty(this.ripeningDate);
    }

    public IntegerProperty getTimesViewedProperty() {
        return new SimpleIntegerProperty(this.timesViewed);
    }

    public StringProperty getSeasonProperty() {
        return new SimpleStringProperty(this.getSeasons());
    }

    public IntegerProperty getStockProperty() {
        return new SimpleIntegerProperty(this.getStockNL());
    }

    public DoubleProperty getPriceProperty() {
        return new SimpleDoubleProperty(this.price);
    }

    @Override
    public String toString() {
        return "Product [name=" + getName() + "]";
    }
}
