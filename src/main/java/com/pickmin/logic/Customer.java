package com.pickmin.logic;

import com.pickmin.config.GlobalConfig;
import com.pickmin.translation.Language;

public class Customer extends User {
    private ShoppingList shoppingList;

    public Customer(String username, String password) {
        super(username, password);
        initializeShoppingList();
    }

    public Customer(String username, String password, Language preferredLanguage) {
        super(username, password, preferredLanguage);
        initializeShoppingList();
    }

    public Customer(String id, String username, String password, Language preferredLanguage) {
        super(id, username, password, preferredLanguage);
        initializeShoppingList();
    }

    private void initializeShoppingList(){
        if (GlobalConfig.LOAD_SHOPPINGLIST) {
            this.shoppingList = JsonHandler.loadShoppingListFromJson(this.getId());
        } else {
            this.shoppingList = new ShoppingList();
        }
    }

    @Override
    public UserType getUserType() {
        return UserType.CUSTOMER;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }
}