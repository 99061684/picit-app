package com.pickmin.logic;

import com.pickmin.translation.Language;

public class Employee extends User {

    public Employee(String username, String password) {
        super(username, password);
    }

    public Employee(String username, String password, Language preferredLanguage) {
        super(username, password, preferredLanguage);
    }

    public Employee(String id, String username, String password, Language preferredLanguage) {
        super(id, username, password, preferredLanguage);
    }

    @Override
    public UserType getUserType() {
        return UserType.EMPLOYEE;
    }
}