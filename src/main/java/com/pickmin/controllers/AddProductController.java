package com.pickmin.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import com.pickmin.App;
import com.pickmin.config.GlobalConfig;
import com.pickmin.customJavaFX.NumberField;
import com.pickmin.exceptions.ExistingProductException;
import com.pickmin.exceptions.InvalidInputException;
import com.pickmin.exceptions.MissingFieldException;
import com.pickmin.logic.general.UtilityFunctions;
import com.pickmin.logic.json.JsonHandler;
import com.pickmin.logic.model.Product;
import com.pickmin.logic.model.ProductCategorie;
import com.pickmin.logic.model.ProductUnit;
import com.pickmin.logic.validation.FieldKey;
import com.pickmin.logic.validation.Validation;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddProductController extends FormController {

    @FXML
    private TextField productNameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField originField;
    @FXML
    private TextField categorieField;
    @FXML
    private DatePicker ripeningDatePicker;
    @FXML
    private TextField seasonField;
    @FXML
    private NumberField stockNLField;
    @FXML
    private NumberField priceField;

    // Error labels voor input velden.
    @FXML
    private Label productNameError;
    @FXML
    private Label descriptionError;
    @FXML
    private Label originError;
    @FXML
    private Label categorieError;
    @FXML
    private Label ripeningDateError;
    @FXML
    private Label seasonError;
    @FXML
    private Label stockError;
    @FXML
    private Label priceError;

    @FXML
    private void initialize() {
        UtilityFunctions.configureDatePicker(ripeningDatePicker);
        initializeErrorFields();
        // setErrorFields(new MissingFieldException(FieldKey.PRODUCT_NAME));
        // setErrorFields(new MissingFieldException(FieldKey.PRODUCT_PRICE));
        // setErrorFields(new MissingFieldException(FieldKey.PRODUCT_SEASONS));
        // setErrorFields(new MissingFieldException(FieldKey.PRODUCT_RIPENING_DATE));
    }

    @FXML
    private void handleAddProduct() {
        try {
            String name = productNameField.getText();
            String description = descriptionField.getText();
            String origin = originField.getText();
            ProductCategorie categorie = new ProductCategorie(categorieField.getText());

            LocalDate ripeningDate = ripeningDatePicker.getValue();
            ArrayList<String> seasons = UtilityFunctions.extractArrayListFromString(seasonField.getText());
            Integer stockNL = stockNLField.getValueAsInt();
            Double price = priceField.getValue();
            ProductUnit productUnit = ProductUnit.getDefault();

            Validation.validateProduct(null, name, description, origin, ripeningDate, seasons, stockNL, price, categorie);

            Product newProduct = new Product(UtilityFunctions.generateID(), name, description, origin, ripeningDate, 0, seasons, stockNL, price, categorie, productUnit);
            if (GlobalConfig.SAVE_PRODUCTS_AFTER_CHANGE) {
                JsonHandler.saveProduct(newProduct);
            }
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

    protected void initializeErrorFields() {
        errorFields = new HashMap<>();
        errorFields.put(FieldKey.PRODUCT_NAME, productNameError);
        errorFields.put(FieldKey.PRODUCT_CATEGORIE, categorieError);
        errorFields.put(FieldKey.PRODUCT_DESCRIPTION, descriptionError);
        errorFields.put(FieldKey.PRODUCT_ORIGIN, originError);
        errorFields.put(FieldKey.PRODUCT_RIPENING_DATE, ripeningDateError);
        errorFields.put(FieldKey.PRODUCT_SEASONS, seasonError);
        errorFields.put(FieldKey.PRODUCT_STOCK_NL, stockError);
        errorFields.put(FieldKey.PRODUCT_PRICE, priceError);
        resetErrorFields();
    }

    // Functie om terug te keren naar het overzicht zonder iets toe te voegen.
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