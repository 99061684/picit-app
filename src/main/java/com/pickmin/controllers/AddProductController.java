package com.pickmin.controllers;

import java.io.IOException;

import com.pickmin.App;
import com.pickmin.customJavaFX.NumberField;
import com.pickmin.exceptions.ExistingProductException;
import com.pickmin.exceptions.InputException;
import com.pickmin.exceptions.InvalidInputException;
import com.pickmin.exceptions.MissingFieldException;
import com.pickmin.logic.FieldKey;
import com.pickmin.logic.Inventory;
import com.pickmin.logic.Product;
import com.pickmin.logic.Validation;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddProductController {

    @FXML
    private TextField productNameField;
    @FXML
    private CheckBox isAvailableCheckbox;
    @FXML
    private TextField ripeningDateField;
    @FXML
    private TextField seasonField;
    @FXML
    private NumberField stockField;
    @FXML
    private NumberField priceField;

    // Error labels voor input velden.
    @FXML
    private Label productNameError;
    @FXML
    private Label ripeningDateError;
    @FXML
    private Label seasonError;
    @FXML
    private Label stockError;
    @FXML
    private Label priceError;

    public AddProductController() {}

    @FXML
    private void handleAddProduct() {
        try {
            String name = productNameField.getText();
            boolean isAvailable = isAvailableCheckbox.isSelected();
            String ripeningDate = ripeningDateField.getText();
            String season = seasonField.getText();
            Integer stock = stockField.getValueAsInt();
            Double price = priceField.getValue();

            Validation.validateProduct(name, isAvailable, ripeningDate, season, stock, price);

            // Nieuw product aanmaken en toevoegen aan de inventory
            Product newProduct = new Product(name, isAvailable, ripeningDate, season, stock, price);
            Inventory.addProduct(newProduct);
            handleSave();

            System.out.println("Product succesvol toegevoegd: " + newProduct.getName());

        } catch (ExistingProductException e) {
            productNameError.setText(e.getMessage());
        } catch (MissingFieldException e) {
            setErrorFields(e);
        } catch (InvalidInputException e) {
            setErrorFields(e);
        }
    }

    @FXML
    private void setErrorFields(InputException exception) {
        if (exception instanceof MissingFieldException) {
            exception = (MissingFieldException) exception;
        } else if (exception instanceof InvalidInputException) {
            exception = (InvalidInputException) exception;
        } else {
            return;
        }

        if (exception.getFieldKey().equals(FieldKey.PRODUCT_NAME)) {
            productNameError.setText(exception.getMessage());
        } else if (exception.getFieldKey().equals(FieldKey.RIPENING_DATE)) {
            ripeningDateError.setText(exception.getMessage());
        } else if (exception.getFieldKey().equals(FieldKey.SEASON)) {
            seasonError.setText(exception.getMessage());
        } else if (exception.getFieldKey().equals(FieldKey.STOCK)) {
            stockError.setText(exception.getMessage());
        } else if (exception.getFieldKey().equals(FieldKey.PRODUCT_PRICE)) {
            priceError.setText(exception.getMessage());
        }
    }

    // Functie om terug te keren naar het overzicht zonder iets toe te voegen.
    @FXML
    private void handleCancel() {
        try {
            App.setRoot("ProductOverview");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        try {
            App.setRoot("ProductOverview");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}