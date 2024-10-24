package com.pickmin.logic.model.filter;

public class BranchFilter {
    private String id;
    private String city;

    public BranchFilter(String id, String city) {
        this.id = id;
        this.city = city;
    }    

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return getCity();
    }
}
