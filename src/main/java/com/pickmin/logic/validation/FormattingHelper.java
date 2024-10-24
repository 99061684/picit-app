package com.pickmin.logic.validation;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.NumberFormat;
import java.util.Locale;

public class FormattingHelper {
    private static final Locale locale;

    static {
        locale = Locale.forLanguageTag("nl-NL");
    }

    // Formatteer prijs naar de Nederlandse valuta weergave (€X,XX)
    public static String formatPrice(Double price) {
        if (price == null) {
            return null;
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        return currencyFormat.format(price);
    }

    // Formatteer beschikbaarheid als "Beschikbaar" of "Niet beschikbaar"
    public static String formatAvailability(Boolean available) {
        if (available == null) {
            return null;
        }
        return available ? "Beschikbaar" : "Niet beschikbaar";
    }

    // Factory voor het formatteren van prijs-cellen
    public static <T> Callback<TableColumn<T, Double>, TableCell<T, Double>> priceCellFactory() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(formatPrice(price));
                }
            }
        };
    }

    public static <T> Callback<TableColumn<T, Boolean>, TableCell<T, Boolean>> availabilityCellFactory() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) {
                    setText(null);
                } else {
                    setText(formatAvailability(available));
                }
            }
        };
    }
}
