package com.pickmin.controllers;

import java.util.HashMap;
import java.util.Map.Entry;

import com.pickmin.exceptions.InputException;
import com.pickmin.logic.validation.FieldKey;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public abstract class FormController {
    protected HashMap<FieldKey, Label> errorFields;
    @FXML
    private GridPane formGridPane;

    public FormController() {
        this.errorFields = new HashMap<>();
    }

    protected abstract void initializeErrorFields();

    protected void setErrorFields(InputException exception) {
        Label errorLabel = errorFields.get(exception.getFieldKey());
        if (errorLabel != null) {
            showErrorLabel(errorLabel, exception.getMessage());
        } else {
            throw new IllegalArgumentException("No ErrorLabel for field: " + exception.getFieldKey());
        }
    }

    protected void resetErrorFields() {
        for (Entry<FieldKey, Label> errorEntry : errorFields.entrySet()) {
            Label errorLabel = errorEntry.getValue();
            FieldKey errorFieldKey = errorEntry.getKey();
            if (errorLabel != null) {
                hideErrorLabel(errorLabel);
            } else {
                throw new IllegalArgumentException("No ErrorLabel for field: " + errorFieldKey);
            }
        }

    }

    private void showErrorLabel(Label errorLabel, String message) {
        if (!errorLabel.isVisible()) {
            shiftRowsDown(GridPane.getRowIndex(errorLabel), errorLabel);
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    private void hideErrorLabel(Label errorLabel) {
        if (errorLabel.isVisible()) {
            shiftRowsUp(GridPane.getRowIndex(errorLabel) + 1);
            errorLabel.setVisible(false);
            errorLabel.setText("");
        }
    }

    private void shiftRowsDown(int startRow, Label errorLabel) {
        formGridPane.getChildren().forEach(node -> {
            Integer row = GridPane.getRowIndex(node);
            if (row != null && row >= startRow && !node.equals(errorLabel)) {
                GridPane.setRowIndex(node, row + 1);
            }
        });
    }

    private void shiftRowsUp(int startRow) {
        formGridPane.getChildren().forEach(node -> {
            Integer row = GridPane.getRowIndex(node);
            if (row != null && row >= startRow) {
                GridPane.setRowIndex(node, row - 1);
            }
        });
    }
}
