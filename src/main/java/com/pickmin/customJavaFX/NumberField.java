package com.pickmin.customJavaFX;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.UnaryOperator;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.converter.DoubleStringConverter;

public class NumberField extends TextField {
    private final DoubleProperty value = new SimpleDoubleProperty();
    private final DoubleProperty minValue = new SimpleDoubleProperty(Double.MIN_VALUE);
    private final DoubleProperty maxValue = new SimpleDoubleProperty(Double.MAX_VALUE);
    private final IntegerProperty decimalPlaces = new SimpleIntegerProperty(0);

    private final NumberFormat numberFormat;

    public NumberField() {
        this(0, 0);
    }

    public NumberField(double initialValue, int decimalPlaces) {
        this.decimalPlaces.set(decimalPlaces);
        this.numberFormat = getNumberFormat(decimalPlaces);
        initializeListeners(initialValue);
    }

    private void initializeListeners(double initialValue) {
        UnaryOperator<Change> integerFilter = change -> {
            String character = change.getText();
            String newText = change.getControlNewText();
            int positionTyped = change.getCaretPosition();
            if (character.equals(",") && getText().contains(character)) {
                return null;
            } else if (character.equals("-") && getText().contains(character)) {
                return null;
            } else if (character.equals("-") && positionTyped != 0) {
                return null;
            } else if (character.equals("-") && getMinValue() > 0) {
                return null;
            } else if (!",-0123456789".contains(character)) {
                return null;
            } else if (newText.contains(",") && newText.split(",").length == 2
                    && newText.split(",")[1].length() > getDecimalPlaces()) {
                return null;
            } else if (character.equals(",") && getDecimalPlaces() == 0) {
                return null;
            }
            return change;
        };

        DoubleStringConverter converter = new DoubleStringConverter() {
            @Override
            public Double fromString(String s) {
                Double number = getValue();
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s) || ",".equals(s) || "-,".equals(s)) {
                    return number;
                } else {
                    try {
                        return numberFormat.parse(s).doubleValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                return number;
            }

            @Override
            public String toString(Double value) {
                return formatValue(value);
            }
        };
        TextFormatter<Double> textFormatter = new TextFormatter<Double>(converter, initialValue, integerFilter);
        this.setTextFormatter(textFormatter);

        textFormatter.valueProperty().addListener(this::onValueChanged);
    }

    private void onValueChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (newValue == null) {
            setText("");
        } else {
            if (newValue.doubleValue() < getMinValue()) {
                setValue(getMinValue());
            } else if (newValue.doubleValue() > getMaxValue()) {
                setValue(getMaxValue());
            } else {
                setValue(newValue.doubleValue());
            }
        }
    }

    private String formatValue(double value) {
        return numberFormat.format(value);
    }

    // Genereer NumberFormat op basis van het aantal decimalen
    private NumberFormat getNumberFormat(int decimalPlaces) {
        NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("nl-NL"));
        format.setGroupingUsed(true);
        format.setMaximumFractionDigits(decimalPlaces);
        format.setMinimumFractionDigits(decimalPlaces);
        return format;
    }

    // Eigenschappen voor FXML bindings
    public Double getMinValue() {
        return minValue.get();
    }

    public void setMinValue(Double minValue) {
        this.minValue.set(minValue);
    }

    public DoubleProperty minValueProperty() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue.get();
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue.set(maxValue);
    }

    public DoubleProperty maxValueProperty() {
        return maxValue;
    }

    public int getDecimalPlaces() {
        return decimalPlaces.get();
    }

    public void setDecimalPlaces(int decimalPlaces) {
        if (decimalPlaces < 0) {
            return;
        }
        this.decimalPlaces.set(decimalPlaces);
        this.numberFormat.setMaximumFractionDigits(decimalPlaces);
        this.numberFormat.setMinimumFractionDigits(decimalPlaces);

        setText(formatValue(this.getValue()));
    }

    public IntegerProperty decimalPlacesProperty() {
        return decimalPlaces;
    }

    public Double getValue() {
        return value.get();
    }

    public Integer getValueAsInt() {
        return (int) value.get();
    }

    public void setValue(Double value) {
        this.value.set(value);
        setText(formatValue(value));
    }

    public DoubleProperty valueProperty() {
        return value;
    }
}