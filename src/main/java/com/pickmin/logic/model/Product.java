package com.pickmin.logic.model;

import java.time.LocalDate;
import java.util.ArrayList;

import com.pickmin.logic.general.UtilityFunctions;

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
    private LocalDate ripeningDate;
    private int timesViewed;
    private ArrayList<String> seasons;
    private int stockNL;
    private double price;

    private ProductCategorie categorie;
    private ProductUnit unit;

    public Product(String id, String name, String description, String origin, Object ripeningDate, int timesViewed, ArrayList<String> seasons, int stockNL, double price, ProductCategorie categorie, ProductUnit unit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.origin = origin;
        setRipeningDate(ripeningDate);
        this.timesViewed = timesViewed;
        setSeasons(seasons);
        this.stockNL = stockNL;
        this.price = price;
        this.categorie = categorie;
        this.unit = unit;
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

    public String getDescription() {
        return this.description;
    }

    public String getOrigin() {
        return origin;
    }

    public String getRipeningDate() {
        return UtilityFunctions.localDateToString(this.ripeningDate);
    }

    public LocalDate getRipeningDateAsLocalDate() {
        return this.ripeningDate;
    }

    public void setRipeningDate(Object ripeningDate) {
        if (ripeningDate instanceof LocalDate) {
            setRipeningDate((LocalDate) ripeningDate);
        } else if (ripeningDate instanceof String) {
            setRipeningDate((String) ripeningDate);
        } else {
            throw new IllegalArgumentException("Invalid type for ripeningDate");
        }
    }

    public void setRipeningDate(LocalDate ripeningDate) {
        this.ripeningDate = ripeningDate;
    }

    public void setRipeningDate(String ripeningDate) {
        this.ripeningDate = UtilityFunctions.stringToLocalDate(ripeningDate);
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
        this.seasons = UtilityFunctions.cleanStringArrayList(seasons);
    }

    public int getStockNL() {
        return this.stockNL;
    }

    public void setStockNL(int stockNL) {
        if (stockNL >= 0) {
            this.stockNL = stockNL;
        } else {
            throw new IllegalArgumentException("Minimum is 0");
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        } else {
            throw new IllegalArgumentException("Minimum is 0");
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

    public ProductUnit getUnit() {
        return unit;
    }

    // Getters voor JavaFX Properties
    public StringProperty getNameProperty() {
        return new SimpleStringProperty(this.name);
    }

    public StringProperty getRipeningDateProperty() {
        return new SimpleStringProperty(this.getRipeningDate());
    }

    public IntegerProperty getTimesViewedProperty() {
        return new SimpleIntegerProperty(this.timesViewed);
    }

    public StringProperty getSeasonProperty() {
        return new SimpleStringProperty(this.getSeasons());
    }

    public IntegerProperty getStockNLProperty() {
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
