package com.pickmin.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.controlsfx.control.SearchableComboBox;

import com.pickmin.App;
import com.pickmin.config.GlobalConfig;
import com.pickmin.customJavaFX.NumberField;
import com.pickmin.exceptions.ExistingProductException;
import com.pickmin.exceptions.InputException;
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

public class AddProductController {

    @FXML
    private TextField productNameField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField originTextField;
    @FXML
    private SearchableComboBox<ProductCategorie> categorieSearchableComboBox;
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
    private Label ripeningDateError;
    @FXML
    private Label seasonError;
    @FXML
    private Label stockError;
    @FXML
    private Label priceError;

    public AddProductController() {
    }

    @FXML
    private void initialize() {
        UtilityFunctions.configureDatePicker(ripeningDatePicker);
    }

    @FXML
    private void handleAddProduct() {
        try {
            String name = productNameField.getText();
            String description = descriptionTextField.getText();
            String origin = originTextField.getText();
            ProductCategorie categorie = categorieSearchableComboBox.getSelectionModel().getSelectedItem();

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

    @FXML
    private void setErrorFields(InputException exception) {
        if (exception.getFieldKey().equals(FieldKey.PRODUCT_NAME)) {
            productNameError.setText(exception.getMessage());
        } else if (exception.getFieldKey().equals(FieldKey.PRODUCT_RIPENING_DATE)) {
            ripeningDateError.setText(exception.getMessage());
        } else if (exception.getFieldKey().equals(FieldKey.PRODUCT_SEASONS)) {
            seasonError.setText(exception.getMessage());
        } else if (exception.getFieldKey().equals(FieldKey.BRANCH_PRODUCT_STOCK)) {
            stockError.setText(exception.getMessage());
        } else if (exception.getFieldKey().equals(FieldKey.PRODUCT_PRICE)) {
            priceError.setText(exception.getMessage());
        }
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