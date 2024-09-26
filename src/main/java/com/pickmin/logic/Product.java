package com.pickmin.logic;

import java.util.UUID;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
    private String id;
    private String name;
    private boolean isAvailable;
    private String ripeningDate;
    private int timesViewed;
    private String season;
    private int stock;
    private double price;

    public Product(String id, String name, boolean isAvailable, String ripeningDate, int timesViewed, String season, int stock, double price) {
        this.id = id;
        this.name = name;
        this.isAvailable = isAvailable;
        this.ripeningDate = ripeningDate;
        this.season = season;
        this.timesViewed = timesViewed;
        this.stock = stock;
        this.price = price;
    }

    public Product(String name, boolean isAvailable, String ripeningDate, String season, int stock, double price) {
        this(UUID.randomUUID().toString(), name, isAvailable, ripeningDate, 0, season, stock, price);
    }

    public Product(String id, String name, boolean isAvailable, String ripeningDate, String season, int stock, double price) {
        this(id, name, isAvailable, ripeningDate, 0, season, stock, price);
    }

    public Product(String name, boolean isAvailable, String ripeningDate, int timesViewed, String season, int stock, double price) {
        this(UUID.randomUUID().toString(), name, isAvailable, ripeningDate, timesViewed, season, stock, price);
    }

    // Getters en Setters
    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean checkAvailable() {
        return this.isAvailable && this.stock > 0;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
