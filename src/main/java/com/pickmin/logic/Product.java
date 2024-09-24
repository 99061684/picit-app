package com.pickmin.logic;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
    private String name;
    // private String orgin;
    private boolean isAvailable;
    private String ripeningDate;
    private int timesViewed;
    private String season;
    private int stock;
    private double price;

    public Product(String name, boolean isAvailable, String ripeningDate, int timesViewed, String season, int stock, double price) {
        this.name = name;
        this.isAvailable = isAvailable;
        this.ripeningDate = ripeningDate;
        this.season = season;
        this.timesViewed = timesViewed;
        this.stock = stock;
        this.price = price;
    }

    public Product(String name, boolean isAvailable, String ripeningDate, String season, int stock, double price) {
        this(name, isAvailable, ripeningDate, 0, season, stock, price);
    }

    // Getters en Setters
    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return isAvailable;
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

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public int getStock() {
        return stock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
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

    public void setAvailability(boolean available) {
        this.isAvailable = available;
    }

    public String availabilityColor() {
        return isAvailable ? "green" : "red";
    }

    // Getters voor JavaFX Properties
    public StringProperty getNameProperty() {
        return new SimpleStringProperty(this.name);
    }

    public BooleanProperty getAvailabilityProperty() {
        return new SimpleBooleanProperty(this.isAvailable);
    }

    public StringProperty getRipeningDateProperty() {
        return new SimpleStringProperty(this.ripeningDate);
    }

    public IntegerProperty getTimesViewedProperty() {
        return new SimpleIntegerProperty(this.timesViewed);
    }

    public StringProperty getSeasonProperty() {
        return new SimpleStringProperty(this.season);
    }

    public IntegerProperty getStockProperty() {
        return new SimpleIntegerProperty(this.stock);
    }

    public DoubleProperty getPriceProperty() {
        return new SimpleDoubleProperty(this.price);
    }

    @Override
    public String toString() {
        return "Product [name=" + getName() + "]";
    }
}
