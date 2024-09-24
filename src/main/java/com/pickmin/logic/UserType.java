package com.pickmin.logic;

public enum UserType {
    EMPLOYEE("Employee"),
    CUSTOMER("Customer");

    private final String type;

    UserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.type;
    }

    public static UserType getUserTypeFromType(String type) {
        for (UserType userType : UserType.values()) {
            if (userType.getType().equalsIgnoreCase(type)) {
                return userType;
            }
        }
        return null;
    }
}