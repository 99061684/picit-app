package com.pickmin.logic.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Branch {
    private String id;
    private String name;
    private String province;
    private String city;
    private String address;

    private ArrayList<BranchProduct> branchProducts;

    public Branch(String id, String name, String province, String city, String address, ArrayList<BranchProduct> branchProducts) {
        this.id = id;
        this.name = name;
        this.province = province;
        this.city = city;
        this.address = address;
        setBranchProducts(branchProducts);
    }

    // Getters en Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<BranchProduct> getBranchProducts() {
        return branchProducts;
    }

    public void setBranchProducts(ArrayList<BranchProduct> branchProducts) {
        if (branchProducts == null) {
            this.branchProducts = new ArrayList<>();
        } else {
            this.branchProducts = branchProducts;
        }
    }

    public ArrayList<String> getBranchProductsIdArrayList() {
        if (branchProducts.isEmpty()) {
            return new ArrayList<>();
        }
        return branchProducts.stream().map(branchProduct -> branchProduct.getId()).collect(Collectors.toCollection(ArrayList::new));
    }
}
