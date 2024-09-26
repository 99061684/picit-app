package com.pickmin.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.pickmin.App;
import com.pickmin.config.GlobalConfig;
import com.pickmin.customJavaFX.NumberField;
import com.pickmin.exceptions.ExistingProductException;
import com.pickmin.exceptions.InputException;
import com.pickmin.exceptions.InvalidInputException;
import com.pickmin.exceptions.InvalidParametersControllerException;
import com.pickmin.exceptions.MissingFieldException;
import com.pickmin.logic.FieldKey;
import com.pickmin.logic.Inventory;
import com.pickmin.logic.Product;
import com.pickmin.logic.Validation;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditProductController extends ControllerWithParameters {

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

    private final ArrayList<String> parameterKeys = new ArrayList<>(Arrays.asList("product"));
    private Product product;

    @FXML
    @Override
    public void setParameters(HashMap<String, Object> parameters) throws InvalidParametersControllerException {
        if (checkContainsAllParameters(parameters, parameterKeys) == false) {
            throw new InvalidParametersControllerException();
        }

        this.product = (Product) parameters.get("product");

        // Vul de velden met de bestaande productgegevens
        productNameField.setText(product.getName());
        isAvailableCheckbox.setSelected(product.isAvailable());
        ripeningDateField.setText(product.getRipeningDate());
        seasonField.setText(product.getSeason());
        stockField.setValue((double) product.getStock());
        priceField.setValue(product.getPrice());
    }

    public EditProductController() {
    }

    @FXML
    private void handleSaveProduct() {
        try {
            if (product == null) {
                throw new NullPointerException();
            }
            String name = productNameField.getText();
            boolean isAvailable = isAvailableCheckbox.isSelected();
            String ripeningDate = ripeningDateField.getText();
            String season = seasonField.getText();
            Integer stock = stockField.getValueAsInt();
            Double price = priceField.getValue();

            Validation.validateProduct(name, isAvailable, ripeningDate, season, stock, price);

            // Update het product
            product.setName(name);
            product.setAvailability(isAvailable);
            product.setRipeningDate(ripeningDate);
            product.setSeason(season);
            product.setStock(stock);
            product.setPrice(price);

            if (GlobalConfig.SAVE_PRODUCTS_AFTER_CHANGE) {
                Inventory.saveProducts();
            }

            handleSave();
            System.out.println("Product succesvol bijgewerkt: " + product.getName());

        } catch (NullPointerException e) {
            e.printStackTrace();
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

    // Functie om terug te keren naar het overzicht zonder wijzigingen.
    @FXML
    private void handleCancel() {
        try {
            App.goToPage("ProductOverview");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        try {
            App.goToPage("ProductOverview");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}