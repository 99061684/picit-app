package com.pickmin.logic.model;

import com.pickmin.config.GlobalConfig;
import com.pickmin.logic.general.UtilityFunctions;
import com.pickmin.logic.validation.Validation;
import com.pickmin.translation.Language;

public abstract class User {
    private String id;
    private String username;
    private String password;
    private Language preferredLanguage;

    public User(String id, String username, String password, Language preferredLanguage) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.preferredLanguage = preferredLanguage;
    }

    public User(String username, String password) {
        this(UtilityFunctions.generateID(), username, password, GlobalConfig.DEFAULT_LANGUAGE);
    }

    public User(String username, String password, Language preferredLanguage) {
        this(UtilityFunctions.generateID(), username, password, preferredLanguage);
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public abstract UserType getUserType();

    public Language getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getId() {
        return id;
    }

    public boolean comparePassword(String otherPassword) {
        return Validation.checkEncodedPassword(otherPassword, this.password);
    }
}